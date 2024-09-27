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

public class ShortAdapter extends NumberAdapter<Short> {

    public ShortAdapter(boolean nullable) {
        super(nullable);
    }

    public ShortAdapter asNullable() {
        return new ShortAdapter(true);
    }

    @Override
    protected void writeNumberBits(Short value, BitBuffer buffer) {
        buffer.writeShort(value);
    }

    @Override
    protected Short readNumberBits(BitBuffer buffer) {
        return buffer.readShort();
    }

    @Override
    protected void writeNumberBytes(Short value, ByteBuf buffer) {
        buffer.writeShort(value);
    }

    @Override
    protected Short readNumberBytes(ByteBuf buffer) {
        return buffer.readShort();
    }

    @Override
    protected void writeNumberData(Short value, DataOutput data) throws IOException {
        data.writeShort(value);
    }

    @Override
    protected Short readNumberData(DataInput data) throws IOException {
        return data.readShort();
    }

    @Override
    protected NbtElement writeNumberNbt(Short value) {
        return wrap(reduce(value));
    }

    
    @Override
    protected Short readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return numeric.shortValue();
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.asString()).map(Number::shortValue).orElse(null);
        }

        return null;
    }

    @Override
    protected JsonElement writeNumberJson(Short value) {
        return new JsonPrimitive(value);
    }

    
    @Override
    protected Short readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsShort();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(Number::shortValue).orElse(null);
            }
        }

        return null;
    }

}
