package bigchadguys.sellingbin.data.tile;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.adapter.array.ArrayAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.Optional;

@FunctionalInterface
public interface TilePredicate {

	TilePredicate FALSE = (state, nbt) -> false;
	TilePredicate TRUE = (state, nbt) -> true;

	boolean test(PartialBlockState state, PartialCompoundNbt nbt);

	default boolean test(PartialTile tile) {
		return this.test(tile.getState(), tile.getEntity());
	}

	default boolean test(BlockView world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		BlockEntity entity = world.getBlockEntity(pos);
		return this.test(PartialBlockState.of(state), PartialCompoundNbt.of(entity));
	}

	static TilePredicate of(Block block) {
		return (state, nbt) -> state.getBlock().asWhole().map(other -> other == block).orElse(false);
	}

	static Optional<TilePredicate> of(String string, boolean logErrors) {
		if(string.isEmpty()) {
			return Optional.of(TRUE);
		}

		return (switch(string.charAt(0)) {
			case '#' -> PartialBlockTag.parse(string, logErrors);
			case '@' -> PartialBlockGroup.parse(string, logErrors);
			default -> PartialTile.parse(string, logErrors);
		}).map(o -> (TilePredicate)o);
	}

	class Adapter implements ISimpleAdapter<TilePredicate, NbtElement, JsonElement> {
		private static ArrayAdapter<TilePredicate> LIST = Adapters.ofArray(TilePredicate[]::new, new Adapter());

		@Override
		public void writeBits(TilePredicate value, BitBuffer buffer) {
			buffer.writeBoolean(value == null);

			if(value != null) {
				if(value instanceof OrTilePredicate or) {
					buffer.writeBoolean(true);
					LIST.writeBits(or.getChildren(), buffer);
				} else {
					buffer.writeBoolean(false);
					Adapters.UTF_8.writeBits(value.toString(), buffer);
				}
			}
		}

		@Override
		public final Optional<TilePredicate> readBits(BitBuffer buffer) {
			if(buffer.readBoolean()) {
				return Optional.empty();
			}

			if(buffer.readBoolean()) {
				return LIST.readBits(buffer).map(OrTilePredicate::new);
			}

			return Adapters.UTF_8.readBits(buffer).map(string -> of(string, true).orElse(FALSE));
		}

		@Override
		public Optional<NbtElement> writeNbt(TilePredicate value) {
			if(value == null) {
				return Optional.empty();
			} else if(value instanceof OrTilePredicate or) {
				return LIST.writeNbt(or.getChildren());
			}

			return Optional.of(NbtString.of(value.toString()));
		}

		@Override
		public Optional<TilePredicate> readNbt(NbtElement nbt) {
			if(nbt == null) {
				return Optional.empty();
			}

			if(nbt instanceof NbtList list) {
				return LIST.readNbt(list).map(OrTilePredicate::new);
			} else if(nbt instanceof NbtString string) {
				return Optional.of(of(string.asString(), true).orElse(FALSE));
			}

			return Optional.empty();
		}

		@Override
		public Optional<JsonElement> writeJson(TilePredicate value) {
			if(value == null) {
				return Optional.empty();
			} else if(value instanceof OrTilePredicate or) {
				return LIST.writeJson(or.getChildren());
			}

			return Optional.of(new JsonPrimitive(value.toString()));
		}

		@Override
		public Optional<TilePredicate> readJson(JsonElement json) {
			if(json == null) {
				return Optional.empty();
			}

			if(json instanceof JsonArray array) {
				return LIST.readJson(array).map(OrTilePredicate::new);
			} else if(json instanceof JsonPrimitive primitive && primitive.isString()) {
				return Optional.of(of(json.getAsString(), true).orElse(FALSE));
			}

			return Optional.empty();
		}
	}

}
