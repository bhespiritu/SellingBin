package bigchadguys.sellingbin.data.adapter.nbt;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.number.NumericAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NumericNbtAdapter extends NbtAdapter<AbstractNbtNumber> {

    public NumericNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public NumericNbtAdapter asNullable() {
        return new NumericNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(AbstractNbtNumber value, BitBuffer buffer) {
        Adapters.NUMERIC.writeBits(value.numberValue(), buffer);
    }

    @Override
    protected AbstractNbtNumber readTagBits(BitBuffer buffer) {
        NbtElement tag = NumericAdapter.wrap(Adapters.NUMERIC.readBits(buffer).orElseThrow());
        return tag instanceof AbstractNbtNumber numeric ? numeric : null;
    }

    @Override
    protected void writeTagBytes(AbstractNbtNumber value, ByteBuf buffer) {
        Adapters.NUMERIC.writeBytes(value.numberValue(), buffer);
    }

    @Override
    protected AbstractNbtNumber readTagBytes(ByteBuf buffer) {
        NbtElement tag = NumericAdapter.wrap(Adapters.NUMERIC.readBytes(buffer).orElseThrow());
        return tag instanceof AbstractNbtNumber numeric ? numeric : null;
    }

    @Override
    protected void writeTagData(AbstractNbtNumber value, DataOutput data) throws IOException {
        Adapters.NUMERIC.writeData(value.numberValue(), data);
    }

    @Override
    protected AbstractNbtNumber readTagData(DataInput data) throws IOException {
        NbtElement tag = NumericAdapter.wrap(Adapters.NUMERIC.readData(data).orElseThrow());
        return tag instanceof AbstractNbtNumber numeric ? numeric : null;
    }

    @Override
    protected NbtElement writeTagNbt(AbstractNbtNumber value) {
        return value;
    }

    
    @Override
    protected AbstractNbtNumber readTagNbt(NbtElement nbt) {
        return nbt instanceof AbstractNbtNumber tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(AbstractNbtNumber value) {
        return Adapters.NUMERIC.writeJson(value.numberValue()).orElseThrow();
    }

    
    @Override
    protected AbstractNbtNumber readTagJson(JsonElement json) {
        NbtElement tag = Adapters.NUMERIC.readJson(json).map(NumericAdapter::wrap).orElse(null);
        return tag instanceof AbstractNbtNumber numeric ? numeric : null;
    }

}
