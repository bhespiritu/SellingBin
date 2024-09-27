package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntArrayNbtAdapter extends NbtAdapter<NbtIntArray> {

    public IntArrayNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public IntArrayNbtAdapter asNullable() {
        return new IntArrayNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtIntArray value, BitBuffer buffer) {
        Adapters.INT_ARRAY.writeBits(value.getIntArray(), buffer);
    }

    @Override
    protected NbtIntArray readTagBits(BitBuffer buffer) {
        return new NbtIntArray(Adapters.INT_ARRAY.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeTagBytes(NbtIntArray value, ByteBuf buffer) {
        Adapters.INT_ARRAY.writeBytes(value.getIntArray(), buffer);
    }

    @Override
    protected NbtIntArray readTagBytes(ByteBuf buffer) {
        return new NbtIntArray(Adapters.INT_ARRAY.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeTagData(NbtIntArray value, DataOutput data) throws IOException {
        Adapters.INT_ARRAY.writeData(value.getIntArray(), data);
    }

    @Override
    protected NbtIntArray readTagData(DataInput data) throws IOException {
        return new NbtIntArray(Adapters.INT_ARRAY.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeTagNbt(NbtIntArray value) {
        return value.copy();
    }

    
    @Override
    protected NbtIntArray readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtIntArray tag ? tag.copy() : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtIntArray value) {
        return Adapters.INT_ARRAY.writeJson(value.getIntArray()).map(array -> {
            JsonArray copy = new JsonArray();
            copy.add("I");

            for(int i = 1; i < array.size(); i++) {
                copy.add(array.get(i));
            }

            return copy;
        }).orElse(null);
    }

    
    @Override
    protected NbtIntArray readTagJson(JsonElement json) {
        if(json instanceof JsonArray array) {
            if(array.size() > 0 && array.get(0) instanceof JsonPrimitive primitive && primitive.getAsString().equals("I")) {
                JsonArray copy = new JsonArray();

                for(int i = 1; i < array.size(); i++) {
                    copy.add(array.get(i));
                }

                array = copy;
            }

            return Adapters.INT_ARRAY.readJson(array).map(NbtIntArray::new).orElse(null);
        }

        return null;
    }

}
