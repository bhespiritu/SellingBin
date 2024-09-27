package bigchadguys.sellingbin.data.adapter.number;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NumericAdapter extends NumberAdapter<Number> {

    private static NumberAdapter[] ADAPTERS = new NumberAdapter[] {
        Adapters.BYTE,
        Adapters.SHORT,
        Adapters.INT,
        Adapters.FLOAT,
        Adapters.LONG,
        Adapters.DOUBLE,
        Adapters.BIG_INTEGER,
        Adapters.BIG_DECIMAL
    };

    private static final BoundedIntAdapter ID = new BoundedIntAdapter(0, ADAPTERS.length - 1, false);
    private static final Object2IntMap<Class<?>> TYPE_TO_ID = new Object2IntOpenHashMap<>();

    static {
        TYPE_TO_ID.put(Byte.class, 0);
        TYPE_TO_ID.put(Short.class, 1);
        TYPE_TO_ID.put(Integer.class, 2);
        TYPE_TO_ID.put(Float.class, 3);
        TYPE_TO_ID.put(Long.class, 4);
        TYPE_TO_ID.put(Double.class, 5);
        TYPE_TO_ID.put(BigInteger.class, 6);
        TYPE_TO_ID.put(BigDecimal.class, 7);
    }

    public NumericAdapter(boolean nullable) {
        super(nullable);
    }

    public NumericAdapter asNullable() {
        return new NumericAdapter(true);
    }

    @Override
    protected void writeNumberBits(Number value, BitBuffer buffer) {
        int id = TYPE_TO_ID.getInt(value.getClass());
        ID.writeBits(id, buffer);
        ADAPTERS[id].writeBits(value, buffer);
    }

    @Override
    protected Number readNumberBits(BitBuffer buffer) {
        int id = ID.readBits(buffer).orElseThrow();
        return (Number)ADAPTERS[id].readBits(buffer).orElse(null);
    }

    @Override
    protected void writeNumberBytes(Number value, ByteBuf buffer) {
        int id = TYPE_TO_ID.getInt(value.getClass());
        ID.writeBytes(id, buffer);
        ADAPTERS[id].writeBytes(value, buffer);
    }

    @Override
    protected Number readNumberBytes(ByteBuf buffer) {
        int id = ID.readBytes(buffer).orElseThrow();
        return (Number)ADAPTERS[id].readBytes(buffer).orElse(null);
    }

    @Override
    protected void writeNumberData(Number value, DataOutput data) throws IOException {
        int id = TYPE_TO_ID.getInt(value.getClass());
        ID.writeData(id, data);
        ADAPTERS[id].writeData(value, data);
    }

    @Override
    protected Number readNumberData(DataInput data) throws IOException {
        int id = ID.readData(data).orElseThrow();
        return (Number)ADAPTERS[id].readData(data).orElse(null);
    }

    
    @Override
    protected NbtElement writeNumberNbt(Number value) {
        return wrap(reduce(value));
    }

    
    @Override
    protected Number readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return numeric.numberValue();
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.toString()).orElse(null);
        }

        return null;
    }

    
    @Override
    protected JsonElement writeNumberJson(Number value) {
        return new JsonPrimitive(value);
    }

    
    @Override
    protected Number readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            return parse(primitive.getAsString()).orElse(null);
        }

        return null;
    }

}
