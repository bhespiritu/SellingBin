package bigchadguys.sellingbin.data.adapter.basic;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class UuidAdapter implements ISimpleAdapter<UUID, NbtElement, JsonElement> {

    private final boolean nullable;

    public UuidAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public UuidAdapter asNullable() {
        return new UuidAdapter(true);
    }

    @Override
    public void writeBits(UUID value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeLong(value.getMostSignificantBits());
            buffer.writeLong(value.getLeastSignificantBits());
        }
    }

    @Override
    public Optional<UUID> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(new UUID(buffer.readLong(), buffer.readLong()));
    }

    @Override
    public void writeBytes(UUID value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeLong(value.getMostSignificantBits());
            buffer.writeLong(value.getLeastSignificantBits());
        }
    }

    @Override
    public Optional<UUID> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(new UUID(buffer.readLong(), buffer.readLong()));
    }

    @Override
    public void writeData(UUID value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            data.writeLong(value.getMostSignificantBits());
            data.writeLong(value.getLeastSignificantBits());
        }
    }

    @Override
    public Optional<UUID> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(new UUID(data.readLong(), data.readLong()));
    }

    @Override
    public Optional<NbtElement> writeNbt(UUID value) {
        if(value == null) {
            return Optional.empty();
        }

        return Optional.of(new NbtIntArray(new int[] {
            (int)(value.getMostSignificantBits() >>> 32), (int)value.getMostSignificantBits(),
            (int)(value.getLeastSignificantBits() >>> 32), (int)value.getLeastSignificantBits()
        }));
    }

    @Override
    public Optional<UUID> readNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtList<?> array && array.size() == 4) {
            return Optional.of(new UUID(
                (long) Adapters.INT.readNbt(array.get(0)).orElse(0) << 32
                    | Integer.toUnsignedLong(Adapters.INT.readNbt(array.get(1)).orElse(0)),
                (long)Adapters.INT.readNbt(array.get(2)).orElse(0) << 32
                    | Integer.toUnsignedLong(Adapters.INT.readNbt(array.get(3)).orElse(0))));
        } else if(nbt instanceof AbstractNbtList<?> array && array.size() == 2) {
            return Optional.of(new UUID(
                Adapters.LONG.readNbt(array.get(0)).orElse(0L),
                Adapters.LONG.readNbt(array.get(1)).orElse(0L)));
        } else if(nbt instanceof NbtString string) {
            try { return Optional.of(UUID.fromString(string.asString())); }
            catch(IllegalStateException exception) { return Optional.empty(); }
        }

        return Optional.empty();
    }

    @Override
    public Optional<JsonElement> writeJson(UUID value) {
        return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value.toString()));
    }

    @Override
    public Optional<UUID> readJson(JsonElement json) {
        if(json instanceof JsonArray array && array.size() == 4) {
            return Optional.of(new UUID(
                (long)Adapters.INT.readJson(array.get(0)).orElse(0) << 32
                    | Integer.toUnsignedLong(Adapters.INT.readJson(array.get(1)).orElse(0)),
                (long)Adapters.INT.readJson(array.get(2)).orElse(0) << 32
                    | Integer.toUnsignedLong(Adapters.INT.readJson(array.get(3)).orElse(0))));
        } else if(json instanceof JsonArray array && array.size() == 2) {
            return Optional.of(new UUID(
                Adapters.LONG.readJson(array.get(0)).orElse(0L),
                Adapters.LONG.readJson(array.get(1)).orElse(0L)));
        } else if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            try { return Optional.of(UUID.fromString(primitive.getAsString())); }
            catch(IllegalStateException exception) { return Optional.empty(); }
        }

        return Optional.empty();
    }

}
