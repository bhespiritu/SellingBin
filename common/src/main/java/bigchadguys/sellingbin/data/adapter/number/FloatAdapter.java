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

public class FloatAdapter extends NumberAdapter<Float> {

    public FloatAdapter(boolean nullable) {
        super(nullable);
    }

    public FloatAdapter asNullable() {
        return new FloatAdapter(true);
    }

    @Override
    protected void writeNumberBits(Float value, BitBuffer buffer) {
        buffer.writeFloat(value);
    }

    @Override
    protected Float readNumberBits(BitBuffer buffer) {
        return buffer.readFloat();
    }

    @Override
    protected void writeNumberBytes(Float value, ByteBuf buffer) {
        buffer.writeFloat(value);
    }

    @Override
    protected Float readNumberBytes(ByteBuf buffer) {
        return buffer.readFloat();
    }

    @Override
    protected void writeNumberData(Float value, DataOutput data) throws IOException {
        data.writeFloat(value);
    }

    @Override
    protected Float readNumberData(DataInput data) throws IOException {
        return data.readFloat();
    }

    
    @Override
    protected NbtElement writeNumberNbt(Float value) {
        return wrap(reduce(value));
    }

    
    @Override
    protected Float readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return numeric.floatValue();
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.asString()).map(Number::floatValue).orElse(null);
        }

        return null;
    }

    
    @Override
    protected JsonElement writeNumberJson(Float value) {
        return new JsonPrimitive(value);
    }

    
    @Override
    protected Float readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsFloat();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(Number::floatValue).orElse(null);
            }
        }

        return null;
    }

}
