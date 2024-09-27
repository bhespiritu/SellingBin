package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongNbtAdapter extends NbtAdapter<NbtLong> {

    public LongNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public LongNbtAdapter asNullable() {
        return new LongNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtLong value, BitBuffer buffer) {
        Adapters.LONG.writeBits(value.longValue(), buffer);
    }

    @Override
    protected NbtLong readTagBits(BitBuffer buffer) {
        return NbtLong.of(Adapters.LONG.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtLong value, ByteBuf buffer) {
        Adapters.LONG.writeBytes(value.longValue(), buffer);
    }

    @Override
    protected NbtLong readTagBytes(ByteBuf buffer) {
        return NbtLong.of(Adapters.LONG.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtLong value, DataOutput data) throws IOException {
        Adapters.LONG.writeData(value.longValue(), data);
    }

    @Override
    protected NbtLong readTagData(DataInput data) throws IOException {
        return NbtLong.of(Adapters.LONG.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtLong value) {
        return value;
    }

    
    @Override
    protected NbtLong readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtLong tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtLong value) {
        return Adapters.LONG.writeJson(value.longValue()).orElseThrow();
    }

    
    @Override
    protected NbtLong readTagJson(JsonElement json) {
        return Adapters.LONG.readJson(json).map(NbtLong::of).orElse(null);
    }

}
