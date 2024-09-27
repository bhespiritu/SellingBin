package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatNbtAdapter extends NbtAdapter<NbtFloat> {

    public FloatNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public FloatNbtAdapter asNullable() {
        return new FloatNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtFloat value, BitBuffer buffer) {
        Adapters.FLOAT.writeBits(value.floatValue(), buffer);
    }

    @Override
    protected NbtFloat readTagBits(BitBuffer buffer) {
        return NbtFloat.of(Adapters.FLOAT.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtFloat value, ByteBuf buffer) {
        Adapters.FLOAT.writeBytes(value.floatValue(), buffer);
    }

    @Override
    protected NbtFloat readTagBytes(ByteBuf buffer) {
        return NbtFloat.of(Adapters.FLOAT.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtFloat value, DataOutput data) throws IOException {
        Adapters.FLOAT.writeData(value.floatValue(), data);
    }

    @Override
    protected NbtFloat readTagData(DataInput data) throws IOException {
        return NbtFloat.of(Adapters.FLOAT.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtFloat value) {
        return value;
    }

    
    @Override
    protected NbtFloat readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtFloat tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtFloat value) {
        return Adapters.FLOAT.writeJson(value.floatValue()).orElseThrow();
    }

    
    @Override
    protected NbtFloat readTagJson(JsonElement json) {
        return Adapters.FLOAT.readJson(json).map(NbtFloat::of).orElse(null);
    }

}
