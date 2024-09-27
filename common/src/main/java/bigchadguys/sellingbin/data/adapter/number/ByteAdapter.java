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

public class ByteAdapter extends NumberAdapter<Byte> {

    public ByteAdapter(boolean nullable) {
        super(nullable);
    }

    public ByteAdapter asNullable() {
        return new ByteAdapter(true);
    }

    @Override
    protected void writeNumberBits(Byte value, BitBuffer buffer) {
        buffer.writeByte(value);
    }

    @Override
    protected Byte readNumberBits(BitBuffer buffer) {
        return buffer.readByte();
    }

    @Override
    protected void writeNumberBytes(Byte value, ByteBuf buffer) {
        buffer.writeByte(value);
    }

    @Override
    protected Byte readNumberBytes(ByteBuf buffer) {
        return buffer.readByte();
    }

    @Override
    protected void writeNumberData(Byte value, DataOutput data) throws IOException {
        data.writeByte(value);
    }

    @Override
    protected Byte readNumberData(DataInput data) throws IOException {
        return data.readByte();
    }

    @Override
    protected NbtElement writeNumberNbt(Byte value) {
        return wrap(reduce(value));
    }
    
    @Override
    protected Byte readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return numeric.byteValue();
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.toString()).map(Number::byteValue).orElse(null);
        }

        return null;
    }
    
    @Override
    protected JsonElement writeNumberJson(Byte value) {
        return new JsonPrimitive(value);
    }
    
    @Override
    protected Byte readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsByte();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(Number::byteValue).orElse(null);
            }
        }

        return null;
    }

}
