package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLongArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongArrayNbtAdapter extends NbtAdapter<NbtLongArray> {

    public LongArrayNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public LongArrayNbtAdapter asNullable() {
        return new LongArrayNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtLongArray value, BitBuffer buffer) {
        Adapters.LONG_ARRAY.writeBits(value.getLongArray(), buffer);
    }

    @Override
    protected NbtLongArray readTagBits(BitBuffer buffer) {
        return new NbtLongArray(Adapters.LONG_ARRAY.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtLongArray value, ByteBuf buffer) {
        Adapters.LONG_ARRAY.writeBytes(value.getLongArray(), buffer);
    }

    @Override
    protected NbtLongArray readTagBytes(ByteBuf buffer) {
        return new NbtLongArray(Adapters.LONG_ARRAY.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtLongArray value, DataOutput data) throws IOException {
        Adapters.LONG_ARRAY.writeData(value.getLongArray(), data);
    }

    @Override
    protected NbtLongArray readTagData(DataInput data) throws IOException {
        return new NbtLongArray(Adapters.LONG_ARRAY.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtLongArray value) {
        return value.copy();
    }

    
    @Override
    protected NbtLongArray readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtLongArray tag ? tag.copy() : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtLongArray value) {
        return Adapters.LONG_ARRAY.writeJson(value.getLongArray()).map(array -> {
            JsonArray copy = new JsonArray();
            copy.add("L");

            for(int i = 1; i < array.size(); i++) {
                copy.add(array.get(i));
            }

            return copy;
        }).orElse(null);
    }

    
    @Override
    protected NbtLongArray readTagJson(JsonElement json) {
        if(json instanceof JsonArray array) {
            if(array.size() > 0 && array.get(0) instanceof JsonPrimitive primitive && primitive.getAsString().equals("L")) {
                JsonArray copy = new JsonArray();

                for(int i = 1; i < array.size(); i++) {
                    copy.add(array.get(i));
                }

                array = copy;
            }

            return Adapters.LONG_ARRAY.readJson(array).map(NbtLongArray::new).orElse(null);
        }

        return null;
    }

}
