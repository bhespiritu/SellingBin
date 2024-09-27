package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleNbtAdapter extends NbtAdapter<NbtDouble> {

    public DoubleNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public DoubleNbtAdapter asNullable() {
        return new DoubleNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtDouble value, BitBuffer buffer) {
        Adapters.DOUBLE.writeBits(value.doubleValue(), buffer);
    }

    @Override
    protected NbtDouble readTagBits(BitBuffer buffer) {
        return NbtDouble.of(Adapters.DOUBLE.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtDouble value, ByteBuf buffer) {
        Adapters.DOUBLE.writeBytes(value.doubleValue(), buffer);
    }

    @Override
    protected NbtDouble readTagBytes(ByteBuf buffer) {
        return NbtDouble.of(Adapters.DOUBLE.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtDouble value, DataOutput data) throws IOException {
        Adapters.DOUBLE.writeData(value.doubleValue(), data);
    }

    @Override
    protected NbtDouble readTagData(DataInput data) throws IOException {
        return NbtDouble.of(Adapters.DOUBLE.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtDouble value) {
        return value;
    }

    
    @Override
    protected NbtDouble readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtDouble tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtDouble value) {
        return Adapters.DOUBLE.writeJson(value.doubleValue()).orElseThrow();
    }

    
    @Override
    protected NbtDouble readTagJson(JsonElement json) {
        return Adapters.DOUBLE.readJson(json).map(NbtDouble::of).orElse(null);
    }

}
