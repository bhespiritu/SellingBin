package bigchadguys.sellingbin.data.adapter.util;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public class RegistryAdapter<V> implements ISimpleAdapter<V, NbtElement, JsonElement> {

    private final Supplier<Registry<V>> registry;
    private final boolean nullable;

    public RegistryAdapter(Supplier<Registry<V>> registry, boolean nullable) {
        this.registry = registry;
        this.nullable = nullable;
    }

    public Registry<V> getRegistry() {
        return this.registry.get();
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public RegistryAdapter<V> asNullable() {
        return new RegistryAdapter<>(this.registry, true);
    }

    @Override
    public void writeBits(V value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.IDENTIFIER.writeBits(this.registry.get().getId(value), buffer);
        }
    }

    @Override
    public Optional<V> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        Identifier id = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
        return this.getRegistry().getOrEmpty(id);
    }

    @Override
    public void writeBytes(V value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.IDENTIFIER.writeBytes(this.registry.get().getId(value), buffer);
        }
    }

    @Override
    public Optional<V> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        Identifier id = Adapters.IDENTIFIER.readBytes(buffer).orElseThrow();
        return this.getRegistry().getOrEmpty(id);
    }

    @Override
    public void writeData(V value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.IDENTIFIER.writeData(this.registry.get().getId(value), data);
        }
    }

    @Override
    public Optional<V> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        Identifier id = Adapters.IDENTIFIER.readData(data).orElseThrow();
        return this.getRegistry().getOrEmpty(id);
    }

    @Override
    public Optional<NbtElement> writeNbt(V value) {
        return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(this.getRegistry().getId(value));
    }

    @Override
    public Optional<V> readNbt(NbtElement nbt) {
        Identifier id = Adapters.IDENTIFIER.readNbt(nbt).orElse(null);
        return this.getRegistry().getOrEmpty(id);
    }

    @Override
    public Optional<JsonElement> writeJson(V value) {
        return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeJson(this.getRegistry().getId(value));
    }

    @Override
    public Optional<V> readJson(JsonElement json) {
        Identifier id = Adapters.IDENTIFIER.readJson(json).orElse(null);
        return this.getRegistry().getOrEmpty(id);
    }

}
