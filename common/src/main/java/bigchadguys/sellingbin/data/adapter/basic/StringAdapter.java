package bigchadguys.sellingbin.data.adapter.basic;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class StringAdapter implements ISimpleAdapter<String, NbtElement, JsonElement> {

    private final Charset charset;
    private final boolean nullable;

    public StringAdapter(Charset charset, boolean nullable) {
        this.charset = charset;
        this.nullable = nullable;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public StringAdapter asNullable() {
        return new StringAdapter(this.charset, true);
    }

    @Override
    public void writeBits(String value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeString(value, this.charset);
        }
    }

    @Override
    public Optional<String> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(buffer.readString(this.charset));
    }

    @Override
    public void writeBytes(String value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            byte[] bytes = value.getBytes(this.charset);
            Adapters.INT_SEGMENTED_7.writeBytes(bytes.length, buffer);
            buffer.writeBytes(bytes);
        }
    }

    @Override
    public Optional<String> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        byte[] bytes = new byte[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];
        buffer.readBytes(bytes);
        return Optional.of(new String(bytes, this.charset));
    }

    @Override
    public void writeData(String value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            byte[] bytes = value.getBytes(this.charset);
            Adapters.INT_SEGMENTED_7.writeData(bytes.length, data);

            for(byte b : bytes) {
                data.writeByte(b);
            }
        }
    }

    @Override
    public Optional<String> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        byte[] bytes = new byte[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = data.readByte();
        }

        return Optional.of(new String(bytes, this.charset));
    }

    @Override
    public Optional<NbtElement> writeNbt(String value) {
        return value == null ? Optional.empty() : Optional.of(NbtString.of(value));
    }

    @Override
    public Optional<String> readNbt(NbtElement nbt) {
        if(nbt instanceof NbtString string) {
            return Optional.of(string.asString());
        }

        return Optional.empty();
    }

    @Override
    public Optional<JsonElement> writeJson(String value) {
        return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value));
    }

    @Override
    public Optional<String> readJson(JsonElement json) {
        if(json instanceof JsonArray array && array.size() == 1) {
            return this.readJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            return Optional.of(primitive.getAsString());
        }

        return Optional.empty();
    }

}
