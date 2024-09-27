package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EndNbtAdapter extends NbtAdapter<NbtEnd> {

    public EndNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public EndNbtAdapter asNullable() {
        return new EndNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtEnd value, BitBuffer buffer) {

    }

    @Override
    protected NbtEnd readTagBits(BitBuffer buffer) {
        return NbtEnd.INSTANCE;
    }

    @Override
    protected void writeTagBytes(NbtEnd value, ByteBuf buffer) {

    }

    @Override
    protected NbtEnd readTagBytes(ByteBuf buffer) {
        return NbtEnd.INSTANCE;
    }

    @Override
    protected void writeTagData(NbtEnd value, DataOutput data) throws IOException {

    }

    @Override
    protected NbtEnd readTagData(DataInput data) throws IOException {
        return NbtEnd.INSTANCE;
    }

    @Override
    protected NbtElement writeTagNbt(NbtEnd value) {
        return value;
    }

    @Override
    protected NbtEnd readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtEnd tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtEnd value) {
        return JsonNull.INSTANCE;
    }
    
    @Override
    protected NbtEnd readTagJson(JsonElement json) {
        return NbtEnd.INSTANCE;
    }

}
