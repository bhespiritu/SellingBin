package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public abstract class NbtAdapter<T extends NbtElement> implements ISimpleAdapter<T, NbtElement, JsonElement> {

    private final boolean nullable;

    public NbtAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    protected abstract void writeTagBits(T value, BitBuffer buffer);

    protected abstract T readTagBits(BitBuffer buffer);

    protected abstract void writeTagBytes(T value, ByteBuf buffer);

    protected abstract T readTagBytes(ByteBuf buffer);

    protected abstract void writeTagData(T value, DataOutput data) throws IOException;

    protected abstract T readTagData(DataInput data) throws IOException;

    protected abstract NbtElement writeTagNbt(T value);

    
    protected abstract T readTagNbt(NbtElement nbt);

    protected abstract JsonElement writeTagJson(T value);

    
    protected abstract T readTagJson(JsonElement json);

    @Override
    public void writeBits(T value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeTagBits(value, buffer);
        }
    }

    @Override
    public Optional<T> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.readTagBits(buffer));
    }

    @Override
    public void writeBytes(T value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeTagBytes(value, buffer);
        }
    }

    @Override
    public Optional<T> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.readTagBytes(buffer));
    }

    @Override
    public void writeData(T value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeTagData(value, data);
        }
    }

    @Override
    public Optional<T> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.readTagData(data));
    }

    @Override
    public Optional<NbtElement> writeNbt(T value) {
        return value == null ? Optional.empty() : Optional.ofNullable(this.writeTagNbt(value));
    }

    @Override
    public Optional<T> readNbt(NbtElement nbt) {
        return nbt == null ? Optional.empty() : Optional.ofNullable(this.readTagNbt(nbt));
    }

    @Override
    public Optional<JsonElement> writeJson(T value) {
        return value == null ? Optional.empty() : Optional.ofNullable(this.writeTagJson(value));
    }

    @Override
    public Optional<T> readJson(JsonElement json) {
        return json == null || json instanceof JsonNull ? Optional.empty() : Optional.ofNullable(this.readTagJson(json));
    }

}
