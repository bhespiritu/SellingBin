package bigchadguys.sellingbin.data.adapter.number;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtShort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class CharAdapter implements ISimpleAdapter<Character, NbtElement, JsonElement> {

    private final boolean nullable;

    public CharAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public CharAdapter asNullable() {
        return new CharAdapter(true);
    }

    @Override
    public final void writeBits(Character value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeChar(value);
        }
    }

    @Override
    public final Optional<Character> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(buffer.readChar());
    }

    @Override
    public final void writeBytes(Character value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeChar(value);
        }
    }

    @Override
    public final Optional<Character> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(buffer.readChar());
    }

    @Override
    public void writeData(Character value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            data.writeChar(value);
        }
    }

    @Override
    public Optional<Character> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(data.readChar());
    }

    @Override
    public final Optional<NbtElement> writeNbt(Character value) {
        return value == null ? Optional.empty() : Optional.of(NbtShort.of((short)(int)value));
    }

    @Override
    public final Optional<Character> readNbt(NbtElement nbt) {
        return nbt instanceof NbtShort tag ? Optional.of((char)Short.toUnsignedInt(tag.shortValue())) : Optional.empty();
    }

    @Override
    public final Optional<JsonElement> writeJson(Character value) {
        return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value));
    }

    @Override
    public final Optional<Character> readJson(JsonElement json) {
        if(json instanceof JsonArray array && array.size() == 1) {
            return this.readJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            return Optional.of(primitive.getAsString().charAt(0));
        }

        return Optional.empty();
    }

}
