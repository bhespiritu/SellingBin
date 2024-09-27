package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtShort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortNbtAdapter extends NbtAdapter<NbtShort> {

    public ShortNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public ShortNbtAdapter asNullable() {
        return new ShortNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtShort value, BitBuffer buffer) {
        Adapters.SHORT.writeBits(value.shortValue(), buffer);
    }

    @Override
    protected NbtShort readTagBits(BitBuffer buffer) {
        return NbtShort.of(Adapters.SHORT.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtShort value, ByteBuf buffer) {
        Adapters.SHORT.writeBytes(value.shortValue(), buffer);
    }

    @Override
    protected NbtShort readTagBytes(ByteBuf buffer) {
        return NbtShort.of(Adapters.SHORT.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtShort value, DataOutput data) throws IOException {
        Adapters.SHORT.writeData(value.shortValue(), data);
    }

    @Override
    protected NbtShort readTagData(DataInput data) throws IOException {
        return NbtShort.of(Adapters.SHORT.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtShort value) {
        return value;
    }
    
    @Override
    protected NbtShort readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtShort tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtShort value) {
        return Adapters.SHORT.writeJson(value.shortValue()).orElseThrow();
    }

    @Override
    protected NbtShort readTagJson(JsonElement json) {
        return Adapters.SHORT.readJson(json).map(NbtShort::of).orElse(null);
    }

}
