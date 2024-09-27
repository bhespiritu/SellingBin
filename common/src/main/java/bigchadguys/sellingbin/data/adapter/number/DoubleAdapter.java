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

public class DoubleAdapter extends NumberAdapter<Double> {

    public DoubleAdapter(boolean nullable) {
        super(nullable);
    }

    public DoubleAdapter asNullable() {
        return new DoubleAdapter(true);
    }

    @Override
    protected void writeNumberBits(Double value, BitBuffer buffer) {
        buffer.writeDouble(value);
    }

    @Override
    protected Double readNumberBits(BitBuffer buffer) {
        return buffer.readDouble();
    }

    @Override
    protected void writeNumberBytes(Double value, ByteBuf buffer) {
        buffer.writeDouble(value);
    }

    @Override
    protected Double readNumberBytes(ByteBuf buffer) {
        return buffer.readDouble();
    }

    @Override
    protected void writeNumberData(Double value, DataOutput data) throws IOException {
        data.writeDouble(value);
    }

    @Override
    protected Double readNumberData(DataInput data) throws IOException {
        return data.readDouble();
    }
    
    @Override
    protected NbtElement writeNumberNbt(Double value) {
        return wrap(reduce(value));
    }

    @Override
    protected Double readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return numeric.doubleValue();
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.asString()).map(Number::doubleValue).orElse(null);
        }

        return null;
    }
    
    @Override
    protected JsonElement writeNumberJson(Double value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected Double readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsDouble();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(Number::doubleValue).orElse(null);
            }
        }

        return null;
    }

}
