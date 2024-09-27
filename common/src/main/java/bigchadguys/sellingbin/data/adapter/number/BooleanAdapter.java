package bigchadguys.sellingbin.data.adapter.number;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class BooleanAdapter implements ISimpleAdapter<Boolean, NbtElement, JsonElement> {

    private final boolean nullable;

    public BooleanAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public BooleanAdapter asNullable() {
        return new BooleanAdapter(true);
    }

    @Override
    public final void writeBits(Boolean value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeBoolean(value);
        }
    }

    @Override
    public final Optional<Boolean> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(buffer.readBoolean());
    }

    @Override
    public final void writeBytes(Boolean value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeBoolean(value);
        }
    }

    @Override
    public final Optional<Boolean> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(buffer.readBoolean());
    }

    @Override
    public void writeData(Boolean value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            data.writeBoolean(value);
        }
    }

    @Override
    public Optional<Boolean> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(data.readBoolean());
    }

    @Override
    public final Optional<NbtElement> writeNbt(Boolean value) {
        return value == null ? Optional.empty() : Optional.of(NbtByte.of(value));
    }

    @Override
    public final Optional<Boolean> readNbt(NbtElement nbt) {
        return nbt instanceof NbtByte tag ? Optional.of(tag.byteValue() != 0) : Optional.empty();
    }

    @Override
    public final Optional<JsonElement> writeJson(Boolean value) {
        return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value));
    }

    @Override
    public final Optional<Boolean> readJson(JsonElement json) {
        if(json instanceof JsonArray array && array.size() == 1) {
            return this.readJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive && !primitive.isBoolean()) {
            try { return Optional.of(primitive.getAsBoolean()); }
            catch(NumberFormatException e) { return Optional.empty(); }
        }

        return Optional.empty();
    }

}
