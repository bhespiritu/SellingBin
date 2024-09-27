package bigchadguys.sellingbin.data.adapter.number;

import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntAdapter extends NumberAdapter<Integer> {

    public IntAdapter(boolean nullable) {
        super(nullable);
    }

    public IntAdapter asNullable() {
        return new IntAdapter(true);
    }

    @Override
    protected void writeNumberBits(Integer value, BitBuffer buffer) {
        buffer.writeInt(value);
    }

    @Override
    protected Integer readNumberBits(BitBuffer buffer) {
        return buffer.readInt();
    }

    @Override
    protected void writeNumberBytes(Integer value, ByteBuf buffer) {
        buffer.writeInt(value);
    }

    @Override
    protected Integer readNumberBytes(ByteBuf buffer) {
        return buffer.readInt();
    }

    @Override
    protected void writeNumberData(Integer value, DataOutput data) throws IOException {
        data.writeInt(value);
    }

    @Override
    protected Integer readNumberData(DataInput data) throws IOException {
        return data.readInt();
    }

    @Override
    protected NbtElement writeNumberNbt(Integer value) {
        return wrap(reduce(value));
    }

    @Override
    protected Integer readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return numeric.intValue();
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.asString()).map(Number::intValue).orElse(null);
        }

        return null;
    }

    @Override
    protected JsonElement writeNumberJson(Integer value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected Integer readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsInt();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(Number::intValue).orElse(null);
            }
        }

        return null;
    }

}
