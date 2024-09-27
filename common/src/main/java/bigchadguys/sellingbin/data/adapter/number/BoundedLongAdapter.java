package bigchadguys.sellingbin.data.adapter.number;

import bigchadguys.sellingbin.data.bit.BitBuffer;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BoundedLongAdapter extends LongAdapter {

    protected final long min;
    protected final long max;
    protected final int bits;

    public BoundedLongAdapter(long min, long max, boolean nullable) {
        super(nullable);
        this.min = min;
        this.max = max;
        this.bits = 64 - Long.numberOfLeadingZeros(this.max - this.min);
    }

    public long getMin() {
        return this.min;
    }

    public long getMax() {
        return this.max;
    }

    public int getBits() {
        return this.bits;
    }

    @Override
    protected void writeNumberBits(Long value, BitBuffer buffer) {
        buffer.writeLongBits(value - this.min, this.bits);
    }

    @Override
    protected Long readNumberBits(BitBuffer buffer) {
        return this.min + buffer.readLongBits(this.bits);
    }

    @Override
    protected void writeNumberBytes(Long value, ByteBuf buffer) {
        long number = value - this.min;

        for(int offset = 0; offset < this.bits; offset += 8) {
            buffer.writeByte((int)(number >>> offset));
        }
    }

    @Override
    protected Long readNumberBytes(ByteBuf buffer) {
        long number = 0;

        for(int offset = 0; offset < this.bits; offset += 8) {
            number |= (long)buffer.readByte() << offset;
        }

        return number;
    }

    @Override
    protected void writeNumberData(Long value, DataOutput data) throws IOException {
        long number = value - this.min;

        for(int offset = 0; offset < this.bits; offset += 8) {
            data.writeByte((int)(number >>> offset));
        }
    }

    @Override
    protected Long readNumberData(DataInput data) throws IOException {
        long number = 0;

        for(int offset = 0; offset < this.bits; offset += 8) {
            number |= (long)data.readByte() << offset;
        }

        return number;
    }

    @Override
    protected NbtElement writeNumberNbt(Long value) {
        return super.writeNumberNbt(value - this.min);
    }
    
    @Override
    protected Long readNumberNbt(NbtElement nbt) {
        Long value = super.readNumberNbt(nbt);
        return value == null ? null : value + this.min;
    }

}
