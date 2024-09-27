package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntNbtAdapter extends NbtAdapter<NbtInt> {

    public IntNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public IntNbtAdapter asNullable() {
        return new IntNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtInt value, BitBuffer buffer) {
        Adapters.INT.writeBits(value.intValue(), buffer);
    }

    @Override
    protected NbtInt readTagBits(BitBuffer buffer) {
        return NbtInt.of(Adapters.INT.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtInt value, ByteBuf buffer) {
        Adapters.INT.writeBytes(value.intValue(), buffer);
    }

    @Override
    protected NbtInt readTagBytes(ByteBuf buffer) {
        return NbtInt.of(Adapters.INT.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtInt value, DataOutput data) throws IOException {
        Adapters.INT.writeData(value.intValue(), data);
    }

    @Override
    protected NbtInt readTagData(DataInput data) throws IOException {
        return NbtInt.of(Adapters.INT.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtInt value) {
        return value;
    }

    
    @Override
    protected NbtInt readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtInt tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtInt value) {
        return Adapters.INT.writeJson(value.intValue()).orElseThrow();
    }

    
    @Override
    protected NbtInt readTagJson(JsonElement json) {
        return Adapters.INT.readJson(json).map(NbtInt::of).orElse(null);
    }

}
