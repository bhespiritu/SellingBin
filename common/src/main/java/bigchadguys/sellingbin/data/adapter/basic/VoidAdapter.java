package bigchadguys.sellingbin.data.adapter.basic;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class VoidAdapter<T> implements ISimpleAdapter<T, NbtElement, JsonElement> {

    @Override
    public void writeBits(T value, BitBuffer buffer) {

    }

    @Override
    public Optional<T> readBits(BitBuffer buffer) {
        return Optional.empty();
    }

    @Override
    public void writeBytes(T value, ByteBuf buffer) {

    }

    @Override
    public Optional<T> readBytes(ByteBuf buffer) {
        return Optional.empty();
    }

    @Override
    public void writeData(T value, DataOutput data) throws IOException {

    }

    @Override
    public Optional<T> readData(DataInput data) throws IOException {
        return Optional.empty();
    }

    @Override
    public Optional<NbtElement> writeNbt(T value) {
        return Optional.empty();
    }

    @Override
    public Optional<T> readNbt(NbtElement nbt) {
        return Optional.empty();
    }

    @Override
    public Optional<JsonElement> writeJson(T value) {
        return Optional.empty();
    }

    @Override
    public Optional<T> readJson(JsonElement json) {
        return Optional.empty();
    }
    
}
