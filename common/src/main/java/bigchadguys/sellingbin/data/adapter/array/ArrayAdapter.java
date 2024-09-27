package bigchadguys.sellingbin.data.adapter.array;

import bigchadguys.sellingbin.data.adapter.*;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ArrayAdapter<T> implements IComplexAdapter<T[], NbtElement, JsonElement, Object> {

    private final IntFunction<T[]> constructor;
    private final Object elementAdapter;
    private final Supplier<T> defaultValue;
    private final boolean nullable;

    public ArrayAdapter(IntFunction<T[]> constructor, Object elementAdapter, Supplier<T> defaultValue, boolean nullable) {
        this.constructor = constructor;
        this.elementAdapter = elementAdapter;
        this.defaultValue = defaultValue;
        this.nullable = nullable;
    }

    public IntFunction<T[]> getConstructor() {
        return this.constructor;
    }

    public Object getElementAdapter() {
        return this.elementAdapter;
    }

    public Supplier<T> getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public ArrayAdapter<T> asNullable() {
        return new ArrayAdapter<>(this.constructor, this.elementAdapter, this.defaultValue, true);
    }

    @Override
    public final void writeBits(T[] value, BitBuffer buffer, Object context) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_3.writeBits(value.length, buffer);

            for(T element : value) {
                adapter.writeBits(element, buffer, context);
            }
        }
    }

    @Override
    public final Optional<T[]> readBits(BitBuffer buffer, Object context) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        T[] value = this.constructor.apply(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow());

        for(int i = 0; i < value.length; i++) {
            value[i] = (T)adapter.readBits(buffer, context).orElseGet(this.defaultValue);
        }

        return Optional.of(value);
    }

    @Override
    public final void writeBytes(T[] value, ByteBuf buffer, Object context) {
        if(!(this.elementAdapter instanceof IByteAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_3.writeBytes(value.length, buffer);

            for(T element : value) {
                adapter.writeBytes(element, buffer, context);
            }
        }
    }

    @Override
    public final Optional<T[]> readBytes(ByteBuf buffer, Object context) {
        if(!(this.elementAdapter instanceof IByteAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        T[] value = this.constructor.apply(Adapters.INT_SEGMENTED_3.readBytes(buffer).orElseThrow());

        for(int i = 0; i < value.length; i++) {
            value[i] = (T)adapter.readBytes(buffer, context).orElseGet(this.defaultValue);
        }

        return Optional.of(value);
    }

    @Override
    public void writeData(T[] value, DataOutput data, Object context) throws IOException {
        if(!(this.elementAdapter instanceof IDataAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_3.writeData(value.length, data);

            for(T element : value) {
                adapter.writeData(element, data, context);
            }
        }
    }

    @Override
    public Optional<T[]> readData(DataInput data, Object context) throws IOException {
        if(!(this.elementAdapter instanceof IDataAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        T[] value = this.constructor.apply(Adapters.INT_SEGMENTED_3.readData(data).orElseThrow());

        for(int i = 0; i < value.length; i++) {
            value[i] = (T)adapter.readData(data, context).orElseGet(this.defaultValue);
        }

        return Optional.of(value);
    }

    @Override
    public final Optional<NbtElement> writeNbt(T[] value, Object context) {
        if(!(this.elementAdapter instanceof INbtAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(value == null) {
            return Optional.empty();
        }

        NbtList list = new NbtList();

        for(T element : value) {
            list.add((NbtElement)adapter.writeNbt(element, context)
                .orElseGet(() -> adapter.writeNbt(this.defaultValue.get(), context).orElseThrow()));
        }

        return Optional.of(list);
    }

    @Override
    public final Optional<T[]> readNbt(NbtElement nbt, Object context) {
        if(!(this.elementAdapter instanceof INbtAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(!(nbt instanceof NbtList list)) {
            return Optional.empty();
        }

        T[] value = this.constructor.apply(list.size());

        for(int i = 0; i < list.size(); i++) {
            value[i] = (T)adapter.readNbt(list.get(i), context).orElseGet(this.defaultValue);
        }

        return Optional.of(value);
    }

    @Override
    public final Optional<JsonElement> writeJson(T[] value, Object context) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(value == null) {
            return Optional.empty();
        }

        JsonArray array = new JsonArray();

        for(T element : value) {
            array.add((JsonElement)adapter.writeJson(element, context)
                .orElseGet(() -> adapter.writeJson(this.defaultValue.get(), context).orElse(null)));
        }

        return Optional.of(array);
    }

    @Override
    public final Optional<T[]> readJson(JsonElement json, Object context) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(json == null) {
            return Optional.empty();
        }

        if(json instanceof JsonArray array) {
            T[] value = this.constructor.apply(array.size());

            for(int i = 0; i < array.size(); i++) {
                value[i] = (T)adapter.readJson(array.get(i), context).orElseGet(this.defaultValue);
            }

            return Optional.of(value);
        }

        return Optional.empty();
    }

}
