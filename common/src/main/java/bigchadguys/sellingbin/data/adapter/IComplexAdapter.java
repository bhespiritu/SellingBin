package bigchadguys.sellingbin.data.adapter;

import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

public interface IComplexAdapter<T, N extends NbtElement, J extends JsonElement, C> extends IAdapter<T, N, J, C> {

    default void writeBits(T value, BitBuffer buffer) {
        this.writeBits(value, buffer, null);
    }

    default Optional<T> readBits(BitBuffer buffer) {
         return this.readBits(buffer, null);
    }

    default void writeBytes(T value, ByteBuf buffer) {
        this.writeBytes(value, buffer, null);
    }

    default Optional<T> readBytes(ByteBuf buffer) {
        return this.readBytes(buffer, null);
    }

    default void writeData(T value, DataOutput data) throws IOException {
        this.writeData(value, data, null);
    }

    default Optional<T> readData(DataInput data) throws IOException {
        return this.readData(data, null);
    }

    default Optional<N> writeNbt(T value) {
        return this.writeNbt(value, null);
    }

    default Optional<T> readNbt(N nbt) {
        return this.readNbt(nbt, null);
    }

    default Optional<J> writeJson(T value) {
        return this.writeJson(value, null);
    }

    default Optional<T> readJson(J json) {
        return this.readJson(json, null);
    }

    @Override
    void writeBits(T value, BitBuffer buffer, C context);

    @Override
    Optional<T> readBits(BitBuffer buffer, C context);

    @Override
    void writeBytes(T value, ByteBuf buffer, C context);

    @Override
    Optional<T> readBytes(ByteBuf buffer, C context);

    @Override
    void writeData(T value, DataOutput data, C context) throws IOException;

    @Override
    Optional<T> readData(DataInput data, C context) throws IOException;

    @Override
    Optional<N> writeNbt(T value, C context);

    @Override
    Optional<T> readNbt(N nbt, C context);

    @Override
    Optional<J> writeJson(T value, C context);

    @Override
    Optional<T> readJson(J json, C context);
    
    @Override
    default JsonElement serialize(T value, Type source, JsonSerializationContext context) {
        return this.writeJson(value).map(json -> (JsonElement)json).orElse(JsonNull.INSTANCE);
    }

    @Override
    default T deserialize(JsonElement json, Type source, JsonDeserializationContext context) {
        return this.readJson((J)json).orElse(null);
    }

}
