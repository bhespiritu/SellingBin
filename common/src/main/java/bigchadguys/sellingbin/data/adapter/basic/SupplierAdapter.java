package bigchadguys.sellingbin.data.adapter.basic;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import bigchadguys.sellingbin.data.serializable.ISerializable;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public abstract class SupplierAdapter<T extends ISerializable<?, ?>> implements ISimpleAdapter<T, NbtElement, JsonElement> {

    private final boolean nullable;

    public SupplierAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    protected abstract void writeSuppliedBits(T value, BitBuffer buffer);

    protected abstract T readSuppliedBits(BitBuffer buffer);

    protected abstract void writeSuppliedBytes(T value, ByteBuf buffer);

    protected abstract T readSuppliedBytes(ByteBuf buffer);

    protected abstract void writeSuppliedData(T value, DataOutput data) throws IOException;

    protected abstract T readSuppliedData(DataInput data) throws IOException;

    
    protected abstract NbtElement writeSuppliedNbt(T value);

    
    protected abstract T readSuppliedNbt(NbtElement nbt);

    
    protected abstract JsonElement writeSuppliedJson(T value);

    
    protected abstract T readSuppliedJson(JsonElement json);

    @Override
    public void writeBits(T value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeSuppliedBits(value, buffer);
        }
    }

    @Override
    public Optional<T> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.readSuppliedBits(buffer));
    }

    @Override
    public void writeBytes(T value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeSuppliedBytes(value, buffer);
        }
    }

    @Override
    public Optional<T> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.readSuppliedBytes(buffer));
    }

    @Override
    public void writeData(T value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeSuppliedData(value, data);
        }
    }

    @Override
    public Optional<T> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.readSuppliedData(data));
    }

    @Override
    public Optional<NbtElement> writeNbt(T value) {
        return value == null ? Optional.empty() : Optional.ofNullable(this.writeSuppliedNbt(value));
    }

    @Override
    public Optional<T> readNbt(NbtElement nbt) {
        if(nbt == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.readSuppliedNbt(nbt));
    }

    @Override
    public Optional<JsonElement> writeJson(T value) {
        return value == null ? Optional.empty() : Optional.ofNullable(this.writeSuppliedJson(value));
    }

    @Override
    public Optional<T> readJson(JsonElement json) {
        if(json == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.readSuppliedJson(json));
    }

}
