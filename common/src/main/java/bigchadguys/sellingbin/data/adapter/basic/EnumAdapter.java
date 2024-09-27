package bigchadguys.sellingbin.data.adapter.basic;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.adapter.number.IntAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class EnumAdapter<E extends Enum<E>> implements ISimpleAdapter<E, NbtElement, JsonElement> {

    private static final IntAdapter ORDINAL = Adapters.INT_SEGMENTED_3;
    private static final StringAdapter NAME = Adapters.UTF_8;

    private final Class<E> type;
    private final Mode mode;
    private final boolean nullable;

    public EnumAdapter(Class<E> type, Mode mode, boolean nullable) {
        this.type = type;
        this.mode = mode;
        this.nullable = nullable;
    }

    public Class<E> getType() {
        return this.type;
    }

    public Mode getMode() {
        return this.mode;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public EnumAdapter<E> asNullable() {
        return new EnumAdapter<>(this.type, this.mode, true);
    }

    @Override
    public void writeBits(E value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            if(this.mode == Mode.ORDINAL) {
                ORDINAL.writeBits(value.ordinal(), buffer);
            } else if(this.mode == Mode.NAME) {
                NAME.writeBits(value.name(), buffer);
            }
        }
    }

    @Override
    public Optional<E> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        if(this.mode == Mode.ORDINAL) {
            return Optional.of(this.type.getEnumConstants()[ORDINAL.readBits(buffer).orElseThrow()]);
        } else if(this.mode == Mode.NAME) {
            try { return Optional.of(Enum.valueOf(this.type, NAME.readBits(buffer).orElseThrow())); }
            catch(IllegalArgumentException exception) { return Optional.empty(); }
        }

        return Optional.empty();
    }

    @Override
    public void writeBytes(E value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            if(this.mode == Mode.ORDINAL) {
                ORDINAL.writeBytes(value.ordinal(), buffer);
            } else if(this.mode == Mode.NAME) {
                NAME.writeBytes(value.name(), buffer);
            }
        }
    }

    @Override
    public Optional<E> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        if(this.mode == Mode.ORDINAL) {
            return Optional.of(this.type.getEnumConstants()[ORDINAL.readBytes(buffer).orElseThrow()]);
        } else if(this.mode == Mode.NAME) {
            try { return Optional.of(Enum.valueOf(this.type, NAME.readBytes(buffer).orElseThrow())); }
            catch(IllegalArgumentException exception) { return Optional.empty(); }
        }

        return Optional.empty();
    }

    @Override
    public void writeData(E value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            if(this.mode == Mode.ORDINAL) {
                ORDINAL.writeData(value.ordinal(), data);
            } else if(this.mode == Mode.NAME) {
                NAME.writeData(value.name(), data);
            }
        }
    }

    @Override
    public Optional<E> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        if(this.mode == Mode.ORDINAL) {
            return Optional.of(this.type.getEnumConstants()[ORDINAL.readData(data).orElseThrow()]);
        } else if(this.mode == Mode.NAME) {
            try { return Optional.of(Enum.valueOf(this.type, NAME.readData(data).orElseThrow())); }
            catch(IllegalArgumentException exception) { return Optional.empty(); }
        }

        return Optional.empty();
    }

    @Override
    public Optional<NbtElement> writeNbt(E value) {
        if(value == null) {
            return Optional.empty();
        } else if(this.mode == Mode.ORDINAL) {
            return ORDINAL.writeNbt(value.ordinal());
        } else if(this.mode == Mode.NAME) {
            return NAME.writeNbt(value.name());
        }

        return Optional.empty();
    }

    @Override
    public Optional<E> readNbt(NbtElement nbt) {
        if(this.mode == Mode.ORDINAL) {
            for(Optional<Integer> numeric = Adapters.INT.readNbt(nbt); numeric.isPresent(); ) {
                return Optional.of(this.type.getEnumConstants()[numeric.get()]);
            }

            for(Optional<String> string = Adapters.UTF_8.readNbt(nbt); string.isPresent(); ) {
                try { return Optional.of(Enum.valueOf(this.type, string.get())); }
                catch(IllegalArgumentException exception) { return Optional.empty(); }
            }
        } else if(this.mode == Mode.NAME) {
            for(Optional<String> string = Adapters.UTF_8.readNbt(nbt); string.isPresent(); ) {
                try { return Optional.of(Enum.valueOf(this.type, string.get())); }
                catch(IllegalArgumentException exception) { return Optional.empty(); }
            }

            for(Optional<Integer> numeric = Adapters.INT.readNbt(nbt); numeric.isPresent(); ) {
                return Optional.of(this.type.getEnumConstants()[numeric.get()]);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<JsonElement> writeJson(E value) {
        if(value == null) {
            return Optional.empty();
        } else if(this.mode == Mode.ORDINAL) {
            return ORDINAL.writeJson(value.ordinal());
        } else if(this.mode == Mode.NAME) {
            return NAME.writeJson(value.name());
        }

        return Optional.empty();
    }

    @Override
    public Optional<E> readJson(JsonElement json) {
        if(this.mode == Mode.ORDINAL) {
            for(Optional<Integer> numeric = Adapters.INT.readJson(json); numeric.isPresent(); ) {
                return Optional.of(this.type.getEnumConstants()[numeric.get()]);
            }

            for(Optional<String> string = Adapters.UTF_8.readJson(json); string.isPresent(); ) {
                try { return Optional.of(Enum.valueOf(this.type, string.get())); }
                catch(IllegalArgumentException exception) { return Optional.empty(); }
            }
        } else if(this.mode == Mode.NAME) {
            for(Optional<String> string = Adapters.UTF_8.readJson(json); string.isPresent(); ) {
                try { return Optional.of(Enum.valueOf(this.type, string.get())); }
                catch(IllegalArgumentException exception) { return Optional.empty(); }
            }

            for(Optional<Integer> numeric = Adapters.INT.readJson(json); numeric.isPresent(); ) {
                return Optional.of(this.type.getEnumConstants()[numeric.get()]);
            }
        }

        return Optional.empty();
    }

    public enum Mode {
        ORDINAL, NAME
    }

}
