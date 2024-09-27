package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteArrayNbtAdapter extends NbtAdapter<NbtByteArray> {

    public ByteArrayNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public ByteArrayNbtAdapter asNullable() {
        return new ByteArrayNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtByteArray value, BitBuffer buffer) {
        Adapters.BYTE_ARRAY.writeBits(value.getByteArray(), buffer);
    }

    @Override
    protected NbtByteArray readTagBits(BitBuffer buffer) {
        return new NbtByteArray(Adapters.BYTE_ARRAY.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtByteArray value, ByteBuf buffer) {
        Adapters.BYTE_ARRAY.writeBytes(value.getByteArray(), buffer);
    }

    @Override
    protected NbtByteArray readTagBytes(ByteBuf buffer) {
        return new NbtByteArray(Adapters.BYTE_ARRAY.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtByteArray value, DataOutput data) throws IOException {
        Adapters.BYTE_ARRAY.writeData(value.getByteArray(), data);
    }

    @Override
    protected NbtByteArray readTagData(DataInput data) throws IOException {
        return new NbtByteArray(Adapters.BYTE_ARRAY.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtByteArray value) {
        return value.copy();
    }

    
    @Override
    protected NbtByteArray readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtByteArray tag ? (NbtByteArray)tag.copy() : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtByteArray value) {
        return Adapters.BYTE_ARRAY.writeJson(value.getByteArray()).map(array -> {
            JsonArray copy = new JsonArray();
            copy.add("B");

            for(int i = 1; i < array.size(); i++) {
                copy.add(array.get(i));
            }

            return copy;
        }).orElse(null);
    }

    
    @Override
    protected NbtByteArray readTagJson(JsonElement json) {
        if(json instanceof JsonArray array) {
            if(array.size() > 0 && array.get(0) instanceof JsonPrimitive primitive && primitive.getAsString().equals("B")) {
                JsonArray copy = new JsonArray();

                for(int i = 1; i < array.size(); i++) {
                    copy.add(array.get(i));
                }

                array = copy;
            }

            return Adapters.BYTE_ARRAY.readJson(array).map(NbtByteArray::new).orElse(null);
        }

        return null;
    }

}
