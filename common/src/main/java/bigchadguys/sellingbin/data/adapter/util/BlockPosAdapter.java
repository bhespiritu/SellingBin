package bigchadguys.sellingbin.data.adapter.util;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtLong;
import net.minecraft.util.math.BlockPos;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class BlockPosAdapter implements ISimpleAdapter<BlockPos, NbtLong, JsonPrimitive> {

	private final boolean nullable;

	public BlockPosAdapter(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public BlockPosAdapter asNullable() {
		return new BlockPosAdapter(true);
	}

	@Override
	public void writeBits(BlockPos value, BitBuffer buffer) {
		if (this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if (value != null) {
			buffer.writeLong(value.asLong());
		}
	}

	@Override
	public Optional<BlockPos> readBits(BitBuffer buffer) {
		if (this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(BlockPos.fromLong(buffer.readLong()));
	}

	@Override
	public void writeBytes(BlockPos value, ByteBuf buffer) {
		if (this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if (value != null) {
			buffer.writeLong(value.asLong());
		}
	}

	@Override
	public Optional<BlockPos> readBytes(ByteBuf buffer) {
		if (this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(BlockPos.fromLong(buffer.readLong()));
	}

	@Override
	public void writeData(BlockPos value, DataOutput data) throws IOException {
		if (this.nullable) {
			data.writeBoolean(value == null);
		}

		if (value != null) {
			data.writeLong(value.asLong());
		}
	}

	@Override
	public Optional<BlockPos> readData(DataInput data) throws IOException {
		if (this.nullable && data.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(BlockPos.fromLong(data.readLong()));
	}

	@Override
	public Optional<NbtLong> writeNbt(BlockPos value) {
		if (value == null) {
			return Optional.empty();
		}

		return Optional.of(NbtLong.of(value.asLong()));
	}

	@Override
	public Optional<BlockPos> readNbt(NbtLong nbt) {
		return nbt != null ? Optional.of(BlockPos.fromLong(nbt.longValue())) : Optional.empty();
	}

	@Override
	public Optional<JsonPrimitive> writeJson(BlockPos value) {
		return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value.asLong()));
	}

	@Override
	public Optional<BlockPos> readJson(JsonPrimitive json) {
		return json != null && json.isNumber() ? Optional.of(BlockPos.fromLong(json.getAsLong())) : Optional.empty();
	}
}
