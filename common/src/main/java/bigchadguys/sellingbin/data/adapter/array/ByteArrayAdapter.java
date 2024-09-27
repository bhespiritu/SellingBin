package bigchadguys.sellingbin.data.adapter.array;

import bigchadguys.sellingbin.data.adapter.*;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class ByteArrayAdapter implements ISimpleAdapter<byte[], NbtElement, JsonArray> {
    
    private final Object elementAdapter;
    private final boolean nullable;

    public ByteArrayAdapter(Object elementAdapter, boolean nullable) {
        this.elementAdapter = elementAdapter;
        this.nullable = nullable;
    }

    public Object getElementAdapter() {
        return this.elementAdapter;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public ByteArrayAdapter asNullable() {
        return new ByteArrayAdapter(this.elementAdapter, true);
    }

    @Override
    public final void writeBits(byte[] value, BitBuffer buffer) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.INT_SEGMENTED_7.writeBits(value.length, buffer);

            for(byte element : value) {
                adapter.writeBits(element, buffer, null);
            }
        }
    }

    @Override
    public final Optional<byte[]> readBits(BitBuffer buffer) {
        if(!(this.elementAdapter instanceof IBitAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        byte[] value = new byte[Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (byte)adapter.readBits(buffer, null).orElse((byte)0);
        }

        return Optional.of(value);
    }

    @Override
    public final void writeBytes(byte[] value, ByteBuf buffer) {
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
    public final Optional<byte[]> readBytes(ByteBuf buffer) {
        if(!(this.elementAdapter instanceof IByteAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        byte[] value = new byte[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (byte)adapter.readBytes(buffer, null).orElse((byte)0);
        }

        return Optional.of(value);
    }

    @Override
    public void writeData(byte[] value, DataOutput data) throws IOException {
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
    public Optional<byte[]> readData(DataInput data) throws IOException {
        if(!(this.elementAdapter instanceof IDataAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        byte[] value = new byte[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

        for(int i = 0; i < value.length; i++) {
            value[i] = (byte)adapter.readData(data, null).orElse((byte)0);
        }

        return Optional.of(value);
    }

    @Override
    public final Optional<NbtElement> writeNbt(byte[] value) {
        if(!(this.elementAdapter instanceof INbtAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(value == null) {
            return Optional.empty();
        }

        byte[] bytes = new byte[value.length];

        for(int i = 0; i < value.length; i++) {
            bytes[i] = ((AbstractNbtNumber)adapter.writeNbt(value[i], null).orElseThrow()).byteValue();
        }

        return Optional.of(new NbtByteArray(bytes));
    }

    @Override
    public final Optional<byte[]> readNbt(NbtElement nbt) {
        if(!(this.elementAdapter instanceof INbtAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(nbt instanceof AbstractNbtNumber numeric) {
            return Optional.of(new byte[] { numeric.byteValue() });
        } else if(nbt instanceof AbstractNbtList<?> array) {
            byte[] value = new byte[array.size()];

            for(int i = 0; i < array.size(); i++) {
                value[i] = (byte)adapter.readNbt(array.get(i), null).orElse((byte)0);
            }

            return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public final Optional<JsonArray> writeJson(byte[] value) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(value == null) return Optional.empty();

        JsonPrimitive[] primitives = new JsonPrimitive[value.length];

        for(int i = 0; i < value.length; i++) {
            primitives[i] = (JsonPrimitive)adapter.writeJson(value[i], null).orElseGet(() -> new JsonPrimitive((byte)0));
        }

        JsonArray bytes = new JsonArray(primitives.length);
        for(JsonPrimitive primitive : primitives) bytes.add(primitive.getAsByte());
        return Optional.of(bytes);
    }

    @Override
    public final Optional<byte[]> readJson(JsonArray json) {
        if(!(this.elementAdapter instanceof IJsonAdapter adapter)) {
            throw new UnsupportedOperationException();
        }

        if(json == null) {
            return Optional.empty();
        }

        byte[] value = new byte[json.size()];

        for(int i = 0; i < json.size(); i++) {
            value[i] = (byte)adapter.readJson(json.get(i), null).orElse((byte)0);
        }

        return Optional.of(value);
    }

}
