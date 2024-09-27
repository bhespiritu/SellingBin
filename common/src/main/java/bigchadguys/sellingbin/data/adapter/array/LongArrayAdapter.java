package bigchadguys.sellingbin.data.adapter.array;

import bigchadguys.sellingbin.data.adapter.*;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class LongArrayAdapter implements ISimpleAdapter<long[], NbtElement, JsonArray> {
    
    private final Object elementAdapter;
    private final boolean nullable;

    public LongArrayAdapter(Object elementAdapter, boolean nullable) {
        this.elementAdapter = elementAdapter;
        this.nullable = nullable;
    }

    public Object getElementAdapter() {
        return this.elementAdapter;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public LongArrayAdapter asNullable() {
        return new LongArrayAdapter(this.elementAdapter, true);
    }

    @Override
    public final void writeBits(long[] value, BitBuffer buffer) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_7.writeBits(value.length, buffer);

            for(long element : value) {
                adapter.writeBits(element, buffer, null);
            }
        }
    }

    @Override
    public final Optional<long[]> readBits(BitBuffer buffer) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        long[] value = new long[Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (long)adapter.readBits(buffer, null).orElse(0L);
        }

        return Optional.of(value);
    }

    @Override
    public final void writeBytes(long[] value, ByteBuf buffer) {
        if(!(this.elementAdapter instanceof IByteAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_7.writeBytes(value.length, buffer);

            for(long element : value) {
                adapter.writeBytes(element, buffer, null);
            }
        }
    }

    @Override
    public final Optional<long[]> readBytes(ByteBuf buffer) {
        if(!(this.elementAdapter instanceof IByteAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        long[] value = new long[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (long)adapter.readBytes(buffer, null).orElse(0L);
        }

        return Optional.of(value);
    }

    @Override
    public void writeData(long[] value, DataOutput data) throws IOException {
        if(!(this.elementAdapter instanceof IDataAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_7.writeData(value.length, data);

            for(long element : value) {
                adapter.writeData(element, data, null);
            }
        }
    }

    @Override
    public Optional<long[]> readData(DataInput data) throws IOException {
        if(!(this.elementAdapter instanceof IDataAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        long[] value = new long[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (long)adapter.readData(data, null).orElse(0L);
        }

        return Optional.of(value);
    }

    @Override
    public final Optional<NbtElement> writeNbt(long[] value) {
        if(!(this.elementAdapter instanceof INbtAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(value == null) {
            return Optional.empty();
        }

        AbstractNbtNumber[] tags = new AbstractNbtNumber[value.length];

        for(int i = 0; i < value.length; i++) {
            tags[i] = (AbstractNbtNumber)adapter.writeNbt(value[i], null).orElseThrow();
        }

        if(Arrays.stream(tags).allMatch(element -> element.byteValue() == element.longValue())) {
            byte[] bytes = new byte[tags.length];
            for(int i = 0; i < tags.length; i++) bytes[i] = tags[i].byteValue();
            return Optional.of(new NbtByteArray(bytes));
        } else if(Arrays.stream(tags).allMatch(element -> element.intValue() == element.longValue())) {
            int[] bytes = new int[tags.length];
            for(int i = 0; i < tags.length; i++) bytes[i] = tags[i].intValue();
            return Optional.of(new NbtIntArray(bytes));
        }

        long[] longs = new long[tags.length];
        for(int i = 0; i < tags.length; i++) longs[i] = tags[i].intValue();
        return Optional.of(new NbtLongArray(longs));
    }

    @Override
    public final Optional<long[]> readNbt(NbtElement nbt) {
        if(!(this.elementAdapter instanceof INbtAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(nbt instanceof AbstractNbtNumber numeric) {
            return Optional.of(new long[] { numeric.longValue() });
        } else if(nbt instanceof AbstractNbtList<?> array) {
            long[] value = new long[array.size()];

            for(int i = 0; i < array.size(); i++) {
                value[i] = (long)adapter.readNbt(array.get(i), null).orElse(0L);
            }

            return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public final Optional<JsonArray> writeJson(long[] value) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(value == null) return Optional.empty();

        JsonPrimitive[] primitives = new JsonPrimitive[value.length];

        for(int i = 0; i < value.length; i++) {
            primitives[i] = (JsonPrimitive)adapter.writeJson(value[i], null).orElseGet(() -> new JsonPrimitive(0L));
        }

        JsonArray ints = new JsonArray(primitives.length);
        for(JsonPrimitive primitive : primitives) ints.add(primitive.getAsInt());
        return Optional.of(ints);
    }

    @Override
    public final Optional<long[]> readJson(JsonArray json) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(json == null) {
            return Optional.empty();
        }

        long[] value = new long[json.size()];

        for(int i = 0; i < json.size(); i++) {
            value[i] = (long)adapter.readJson(json.get(i), null).orElse(0L);
        }

        return Optional.of(value);
    }

}
