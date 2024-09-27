package bigchadguys.sellingbin.block.entity;

import bigchadguys.sellingbin.SellingBinInventory;
import bigchadguys.sellingbin.block.BinMaterial;
import bigchadguys.sellingbin.block.BinMode;
import bigchadguys.sellingbin.block.SellingBinBlock;
import bigchadguys.sellingbin.init.ModBlocks;
import bigchadguys.sellingbin.init.ModConfigs;
import bigchadguys.sellingbin.init.ModWorldData;
import bigchadguys.sellingbin.screen.handler.SellingBinScreenHandler;
import bigchadguys.sellingbin.world.data.SellingBinData;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SellingBinBlockEntity extends BaseBlockEntity implements ExtendedMenuProvider {

    private final SellingBinInventory inventory;
    private long timeLeft;

    public SellingBinBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlocks.Entities.SELLING_BIN.get(), pos, state);
    }

    public SellingBinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.inventory = switch(this.getMode()) {
            case BLOCK_BOUND -> new SellingBinInventory(this, state.get(SellingBinBlock.MATERIAL).getRows() * 9);
            case PLAYER_BOUND -> null;
        };
    }

    public SellingBinInventory getInventory() {
        return this.inventory;
    }

    public long getTimeLeft() {
        return this.timeLeft;
    }

    public void setTimeLeft(long updateTimeleft) {
        this.timeLeft = updateTimeleft;
        this.sendUpdatesToClient();
    }

    public BinMaterial getMaterial() {
        return this.getCachedState().get(SellingBinBlock.MATERIAL);
    }

    public BinMode getMode() {
        return this.getCachedState().get(SellingBinBlock.MODE);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SellingBinBlockEntity entity) {
        if(world instanceof ServerWorld) {
            if(entity.getMode() == BinMode.BLOCK_BOUND) {
                if(entity.getTimeLeft() > entity.getMaterial().getUpdateDelayTicks()) {
                    entity.setTimeLeft(entity.getMaterial().getUpdateDelayTicks());
                }

                if(entity.getTimeLeft() < 0) {
                    entity.getInventory().executeTrades();
                    entity.setTimeLeft(entity.getMaterial().getUpdateDelayTicks());
                }

                entity.setTimeLeft(entity.getTimeLeft() - 1);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, UpdateType type) {
        if(this.inventory != null && !this.inventory.isEmpty()) {
            this.inventory.writeNbt().ifPresent(tag -> nbt.put("inventory", tag));
        }

        if(this.timeLeft > 0) {
            nbt.putLong("timeLeft", this.timeLeft);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, UpdateType type) {
        if(this.inventory != null) {
            this.inventory.readNbt(nbt.getCompound("inventory"));
        }

        this.timeLeft = nbt.getLong("timeLeft");
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.selling_bin");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if(this.getMode() == BinMode.BLOCK_BOUND) {
            this.getInventory().setCapacity(this.getMaterial().getRows() * 9);
            return new SellingBinScreenHandler(syncId, playerInventory, this.getInventory(), this.getPos(), this.getMaterial().getRows());
        } else if(this.getMode() == BinMode.PLAYER_BOUND) {
            SellingBinData data = ModWorldData.SELLING_BIN.getGlobal(player.getWorld());
            SellingBinInventory inventory = data.getOrCreate(player, this.getMaterial()).getInventory();
            inventory.setCapacity(this.getMaterial().getRows() * 9);
            data.sendEntriesToClient((ServerPlayerEntity)player);
            return new SellingBinScreenHandler(syncId, playerInventory, inventory, this.getPos(), this.getMaterial().getRows());
        }

        return null;
    }

    @Override
    public void saveExtraData(PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
        buf.writeVarInt(this.getMaterial().getRows());
    }

}
