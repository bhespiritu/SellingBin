package bigchadguys.sellingbin.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public abstract class BaseBlockEntity extends BlockEntity {

    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract void writeNbt(NbtCompound nbt, UpdateType type);

    public abstract void readNbt(NbtCompound nbt, UpdateType type);

    @Override
    public final void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        this.writeNbt(nbt, UpdateType.SERVER);
    }

    @Override
    public final void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.readNbt(nbt, UpdateType.SERVER);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        this.writeNbt(nbt, UpdateType.INITIAL_PACKET);
        return nbt;
    }

    @Override
    public final BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, entity -> {
            NbtCompound nbt = new NbtCompound();

            if(entity instanceof BaseBlockEntity baseEntity) {
                baseEntity.writeNbt(nbt, UpdateType.UPDATE_PACKET);
            } else {
                throw new IllegalStateException("BlockEntity is not an instance of BaseBlockEntity");
            }

            return nbt;
        });
    }

    public void sendUpdatesToClient() {
        this.markDirty();

        if(this.getWorld() != null) {
            this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
            this.getWorld().updateNeighbors(this.getPos(), this.getCachedState().getBlock());
        }
    }

    public enum UpdateType {
        SERVER, INITIAL_PACKET, UPDATE_PACKET
    }

}
