package bigchadguys.sellingbin.screen.handler;

import bigchadguys.sellingbin.block.entity.SellingBinBlockEntity;
import bigchadguys.sellingbin.init.ModScreenHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class SellingBinScreenHandler extends ScreenHandler {

    private final Inventory binInventory;
    private final BlockPos pos;
    private final int rows;

    public SellingBinScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buffer) {
        this(syncId, playerInventory, buffer.readBlockPos(), buffer.readVarInt());
    }

    public SellingBinScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos, int rows) {
        this(syncId, playerInventory, new SimpleInventory(9 * rows), pos, rows);
    }

    public SellingBinScreenHandler(int syncId, PlayerInventory playerInventory, Inventory binInventory, BlockPos pos, int rows) {
        super(ModScreenHandlers.SELLING_BIN.get(), syncId);
        this.binInventory = binInventory;
        this.pos = pos;
        this.rows = rows;
        int i = (this.rows - 4) * 18;

        for(int y = 0; y < this.rows; y++) {
            for(int x = 0; x < 9; x++) {
                this.addSlot(new Slot(binInventory, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++) {
            for(int y = 0; y < 3; y++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + i));
            }
        }

        for(int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 161 + i));
        }

        this.binInventory.onOpen(playerInventory.player);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @Environment(EnvType.CLIENT)
    public Optional<SellingBinBlockEntity> getBlockEntity() {
        if(this.pos == null) return Optional.empty();
        ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null) return Optional.empty();

        if(world.getBlockEntity(this.pos) instanceof SellingBinBlockEntity bin) {
            return Optional.of(bin);
        }

        return Optional.empty();
    }

    public int getRows() {
        return this.rows;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.binInventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if(!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();

        if(slotIndex < this.binInventory.size()) {
            if(!this.insertItem(stack, this.binInventory.size(), this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if(!this.insertItem(stack, 0, this.binInventory.size(), false)) {
                return ItemStack.EMPTY;
            }
        }

        if(stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return copy;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.binInventory.onClose(player);
    }

}
