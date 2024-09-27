package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringNbtAdapter extends NbtAdapter<NbtString> {

    public StringNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public StringNbtAdapter asNullable() {
        return new StringNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtString value, BitBuffer buffer) {
        Adapters.UTF_8.writeBits(value.asString(), buffer);
    }

    @Override
    protected NbtString readTagBits(BitBuffer buffer) {
        return NbtString.of(Adapters.UTF_8.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtString value, ByteBuf buffer) {
        Adapters.UTF_8.writeBytes(value.asString(), buffer);
    }

    @Override
    protected NbtString readTagBytes(ByteBuf buffer) {
        return NbtString.of(Adapters.UTF_8.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtString value, DataOutput data) throws IOException {
        Adapters.UTF_8.writeData(value.asString(), data);
    }

    @Override
    protected NbtString readTagData(DataInput data) throws IOException {
        return NbtString.of(Adapters.UTF_8.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtString value) {
        return value;
    }

    
    @Override
    protected NbtString readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtString tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtString value) {
        return Adapters.UTF_8.writeJson(value.asString()).orElseThrow();
    }

    
    @Override
    protected NbtString readTagJson(JsonElement json) {
        return Adapters.UTF_8.readJson(json).map(NbtString::of).orElse(null);
    }

}
