package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteNbtAdapter extends NbtAdapter<NbtByte> {

    public ByteNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public ByteNbtAdapter asNullable() {
        return new ByteNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtByte value, BitBuffer buffer) {
        Adapters.BYTE.writeBits(value.byteValue(), buffer);
    }

    @Override
    protected NbtByte readTagBits(BitBuffer buffer) {
        return NbtByte.of(Adapters.BYTE.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtByte value, ByteBuf buffer) {
        Adapters.BYTE.writeBytes(value.byteValue(), buffer);
    }

    @Override
    protected NbtByte readTagBytes(ByteBuf buffer) {
        return NbtByte.of(Adapters.BYTE.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtByte value, DataOutput data) throws IOException {
        Adapters.BYTE.writeData(value.byteValue(), data);
    }

    @Override
    protected NbtByte readTagData(DataInput data) throws IOException {
        return NbtByte.of(Adapters.BYTE.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtByte value) {
        return value;
    }

    
    @Override
    protected NbtByte readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtByte tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtByte value) {
        return Adapters.BYTE.writeJson(value.byteValue()).orElseThrow();
    }

    
    @Override
    protected NbtByte readTagJson(JsonElement json) {
        return Adapters.BYTE.readJson(json).map(NbtByte::of).orElse(null);
    }

}
