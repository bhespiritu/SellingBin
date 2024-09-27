package bigchadguys.sellingbin.data.adapter.basic;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.adapter.number.BoundedIntAdapter;
import bigchadguys.sellingbin.data.adapter.number.IntAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.ToIntFunction;

public class OrdinalAdapter<T> implements ISimpleAdapter<T, NbtElement, JsonElement> {

    private final ToIntFunction<T> mapper;
    private final boolean nullable;
    private final T[] array;

    private final IntAdapter intAdapter;

    public OrdinalAdapter(ToIntFunction<T> mapper, boolean nullable, T... array) {
        this.mapper = mapper;
        this.nullable = nullable;
        this.array = array;

        this.intAdapter = new BoundedIntAdapter(0, array.length - 1, false);
    }

    public OrdinalAdapter<T> asNullable() {
        return new OrdinalAdapter<>(this.mapper, true, this.array);
    }

    @Override
    public void writeBits(T value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeOrdinal(value, this.mapper, this.array);
        }
    }

    @Override
    public Optional<T> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(buffer.readOrdinal(this.array));
    }

    @Override
    public void writeBytes(T value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            this.intAdapter.writeBytes(this.mapper.applyAsInt(value), buffer);
        }
    }

    @Override
    public Optional<T> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.array[this.intAdapter.readBytes(buffer).orElseThrow()]);
    }

    @Override
    public void writeData(T value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            this.intAdapter.writeData(this.mapper.applyAsInt(value), data);
        }
    }

    @Override
    public Optional<T> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.array[this.intAdapter.readData(data).orElseThrow()]);
    }

    @Override
    public Optional<NbtElement> writeNbt(T value) {
        return value == null ? Optional.empty() : this.intAdapter.writeNbt(this.mapper.applyAsInt(value));
    }

    @Override
    public Optional<T> readNbt(NbtElement nbt) {
        return this.intAdapter.readNbt(nbt).map(i -> this.array[i]);
    }

    @Override
    public Optional<JsonElement> writeJson(T value) {
        return value == null ? Optional.empty() : this.intAdapter.writeJson(this.mapper.applyAsInt(value));
    }

    @Override
    public Optional<T> readJson(JsonElement json) {
        return this.intAdapter.readJson(json).map(i -> this.array[i]);
    }

}
