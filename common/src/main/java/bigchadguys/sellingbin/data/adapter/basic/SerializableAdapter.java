package bigchadguys.sellingbin.data.adapter.basic;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import bigchadguys.sellingbin.data.serializable.*;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public class SerializableAdapter<T, N extends NbtElement, J extends JsonElement> implements ISimpleAdapter<T, N, J> {

    private final Supplier<T> constructor;
    private final boolean nullable;

    public SerializableAdapter(Supplier<T> constructor, boolean nullable) {
        this.constructor = constructor;
        this.nullable = nullable;
    }

    public Supplier<T> getConstructor() {
        return this.constructor;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public SerializableAdapter<T, N, J> asNullable() {
        return new SerializableAdapter<>(this.constructor, true);
    }

    @Override
    public void writeBits(T value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            ((IBitSerializable)value).writeBits(buffer);
        }
    }

    @Override
    public Optional<T> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        T value = this.constructor.get();
        ((IBitSerializable)value).readBits(buffer);
        return Optional.of(value);
    }

    @Override
    public void writeBytes(T value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            ((IByteSerializable)value).writeBytes(buffer);
        }
    }

    @Override
    public Optional<T> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        T value = this.constructor.get();
        ((IByteSerializable)value).readBytes(buffer);
        return Optional.of(value);
    }

    @Override
    public void writeData(T value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            ((IDataSerializable)value).writeData(data);
        }
    }

    @Override
    public Optional<T> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        T value = this.constructor.get();
        ((IDataSerializable)value).readData(data);
        return Optional.of(value);
    }

    @Override
    public Optional<N> writeNbt(T value) {
        return value == null ? Optional.empty() : ((INbtSerializable)value).writeNbt();
    }

    @Override
    public Optional<T> readNbt(N nbt) {
        if(nbt == null) {
            return Optional.empty();
        }

        T value = this.constructor.get();
        ((INbtSerializable)value).readNbt(nbt);
        return Optional.of(value);
    }

    @Override
    public Optional<J> writeJson(T value) {
        return value == null ? Optional.empty() : ((IJsonSerializable)value).writeJson();
    }

    @Override
    public Optional<T> readJson(J json) {
        if(json == null) {
            return Optional.empty();
        }

        T value = this.constructor.get();
        ((IJsonSerializable)value).readJson(json);
        return Optional.of(value);
    }

}
