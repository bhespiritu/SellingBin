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

public class LongAdapter extends NumberAdapter<Long> {

    public LongAdapter(boolean nullable) {
        super(nullable);
    }

    public LongAdapter asNullable() {
        return new LongAdapter(true);
    }

    @Override
    protected void writeNumberBits(Long value, BitBuffer buffer) {
        buffer.writeLong(value);
    }

    @Override
    protected Long readNumberBits(BitBuffer buffer) {
        return buffer.readLong();
    }

    @Override
    protected void writeNumberBytes(Long value, ByteBuf buffer) {
        buffer.writeLong(value);
    }

    @Override
    protected Long readNumberBytes(ByteBuf buffer) {
        return buffer.readLong();
    }

    @Override
    protected void writeNumberData(Long value, DataOutput data) throws IOException {
        data.writeLong(value);
    }

    @Override
    protected Long readNumberData(DataInput data) throws IOException {
        return data.readLong();
    }

    
    @Override
    protected NbtElement writeNumberNbt(Long value) {
        return wrap(reduce(value));
    }

    
    @Override
    protected Long readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return numeric.longValue();
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.asString()).map(Number::longValue).orElse(null);
        }

        return null;
    }

    
    @Override
    protected JsonElement writeNumberJson(Long value) {
        return new JsonPrimitive(value);
    }

    
    @Override
    protected Long readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsLong();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(Number::longValue).orElse(null);
            }
        }

        return null;
    }

}
