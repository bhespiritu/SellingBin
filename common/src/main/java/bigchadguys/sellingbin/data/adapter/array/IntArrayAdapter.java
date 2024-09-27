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

public class IntArrayAdapter implements ISimpleAdapter<int[], NbtElement, JsonArray> {
    
    private final Object elementAdapter;
    private final boolean nullable;

    public IntArrayAdapter(Object elementAdapter, boolean nullable) {
        this.elementAdapter = elementAdapter;
        this.nullable = nullable;
    }

    public Object getElementAdapter() {
        return this.elementAdapter;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public IntArrayAdapter asNullable() {
        return new IntArrayAdapter(this.elementAdapter, true);
    }

    @Override
    public final void writeBits(int[] value, BitBuffer buffer) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_7.writeBits(value.length, buffer);

            for(int element : value) {
                adapter.writeBits(element, buffer, null);
            }
        }
    }

    @Override
    public final Optional<int[]> readBits(BitBuffer buffer) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        int[] value = new int[Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (int)adapter.readBits(buffer, null).orElse(0);
        }

        return Optional.of(value);
    }

    @Override
    public final void writeBytes(int[] value, ByteBuf buffer) {
        if(!(this.elementAdapter instanceof IByteAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_7.writeBytes(value.length, buffer);

            for(int element : value) {
                adapter.writeBytes(element, buffer, null);
            }
        }
    }

    @Override
    public final Optional<int[]> readBytes(ByteBuf buffer) {
        if(!(this.elementAdapter instanceof IByteAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        int[] value = new int[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (int)adapter.readBytes(buffer, null).orElse(0);
        }

        return Optional.of(value);
    }

    @Override
    public void writeData(int[] value, DataOutput data) throws IOException {
        if(!(this.elementAdapter instanceof IDataAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_7.writeData(value.length, data);

            for(int element : value) {
                adapter.writeData(element, data, null);
            }
        }
    }

    @Override
    public Optional<int[]> readData(DataInput data) throws IOException {
        if(!(this.elementAdapter instanceof IDataAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        int[] value = new int[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (int)adapter.readData(data, null).orElse(0);
        }

        return Optional.of(value);
    }

    @Override
    public final Optional<NbtElement> writeNbt(int[] value) {
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

        if(Arrays.stream(tags).allMatch(element -> element.byteValue() == element.intValue())) {
            byte[] bytes = new byte[tags.length];
            for(int i = 0; i < tags.length; i++) bytes[i] = tags[i].byteValue();
            return Optional.of(new NbtByteArray(bytes));
        }

        int[] ints = new int[tags.length];
        for(int i = 0; i < tags.length; i++) ints[i] = tags[i].intValue();
        return Optional.of(new NbtIntArray(ints));
    }

    @Override
    public final Optional<int[]> readNbt(NbtElement nbt) {
        if(!(this.elementAdapter instanceof INbtAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(nbt instanceof AbstractNbtNumber numeric) {
            return Optional.of(new int[] { numeric.intValue() });
        } else if(nbt instanceof AbstractNbtList<?> array) {
            int[] value = new int[array.size()];

            for(int i = 0; i < array.size(); i++) {
                value[i] = (int)adapter.readNbt(array.get(i), null).orElse(0);
            }

            return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public final Optional<JsonArray> writeJson(int[] value) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(value == null) return Optional.empty();

        JsonPrimitive[] primitives = new JsonPrimitive[value.length];

        for(int i = 0; i < value.length; i++) {
            primitives[i] = (JsonPrimitive)adapter.writeJson(value[i], null).orElseGet(() -> new JsonPrimitive(0));
        }

        JsonArray ints = new JsonArray(primitives.length);
        for(JsonPrimitive primitive : primitives) ints.add(primitive.getAsInt());
        return Optional.of(ints);
    }

    @Override
    public final Optional<int[]> readJson(JsonArray json) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(json == null) {
            return Optional.empty();
        }

        int[] value = new int[json.size()];

        for(int i = 0; i < json.size(); i++) {
            value[i] = (int)adapter.readJson(json.get(i), null).orElse(0);
        }

        return Optional.of(value);
    }

}
