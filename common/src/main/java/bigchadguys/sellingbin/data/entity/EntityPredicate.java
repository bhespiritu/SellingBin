package bigchadguys.sellingbin.data.entity;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.adapter.array.ArrayAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

@FunctionalInterface
public interface EntityPredicate {

	EntityPredicate FALSE = (pos, blockPos, nbt) -> false;
	EntityPredicate TRUE = (pos, blockPos, nbt) -> true;

	boolean test(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt);

	default boolean test(PartialEntity entity) {
		return this.test(entity.getPos(), entity.getBlockPos(), entity.getNbt());
	}

	default boolean test(Entity entity) {
		return this.test(entity.getPos(), entity.getBlockPos(), PartialCompoundNbt.of(entity.writeNbt(new NbtCompound())));
	}

	static Optional<EntityPredicate> of(String string, boolean logErrors) {
		if(string.isEmpty()) {
			return Optional.of(TRUE);
		}

		return (switch(string.charAt(0)) {
			case '#' -> PartialEntityTag.parse(string, logErrors);
			case '@' -> PartialEntityGroup.parse(string, logErrors);
			default -> PartialEntity.parse(string, logErrors);
		}).map(o -> (EntityPredicate)o);
	}

	class Adapter implements ISimpleAdapter<EntityPredicate, NbtElement, JsonElement> {
		private static ArrayAdapter<EntityPredicate> LIST = Adapters.ofArray(EntityPredicate[]::new, new Adapter());

		@Override
		public void writeBits(EntityPredicate value, BitBuffer buffer) {
			buffer.writeBoolean(value == null);

			if(value != null) {
				if(value instanceof OrEntityPredicate or) {
					buffer.writeBoolean(true);
					LIST.writeBits(or.getChildren(), buffer);
				} else {
					buffer.writeBoolean(false);
					Adapters.UTF_8.writeBits(value.toString(), buffer);
				}
			}
		}

		@Override
		public final Optional<EntityPredicate> readBits(BitBuffer buffer) {
			if(buffer.readBoolean()) {
				return Optional.empty();
			}

			if(buffer.readBoolean()) {
				return LIST.readBits(buffer).map(OrEntityPredicate::new);
			}

			return Adapters.UTF_8.readBits(buffer).map(string -> of(string, true).orElse(FALSE));
		}

		@Override
		public Optional<NbtElement> writeNbt(EntityPredicate value) {
			if(value == null) {
				return Optional.empty();
			} else if(value instanceof OrEntityPredicate or) {
				return LIST.writeNbt(or.getChildren());
			}

			return Optional.of(NbtString.of(value.toString()));
		}

		@Override
		public Optional<EntityPredicate> readNbt(NbtElement nbt) {
			if(nbt == null) {
				return Optional.empty();
			}

			if(nbt instanceof NbtList list) {
				return LIST.readNbt(list).map(OrEntityPredicate::new);
			} else if(nbt instanceof NbtString string) {
				return Optional.of(of(string.asString(), true).orElse(FALSE));
			}

			return Optional.empty();
		}

		@Override
		public Optional<JsonElement> writeJson(EntityPredicate value) {
			if(value == null) {
				return Optional.empty();
			} else if(value instanceof OrEntityPredicate or) {
				return LIST.writeJson(or.getChildren());
			}

			return Optional.of(new JsonPrimitive(value.toString()));
		}

		@Override
		public Optional<EntityPredicate> readJson(JsonElement json) {
			if(json == null) {
				return Optional.empty();
			}

			if(json instanceof JsonArray array) {
				return LIST.readJson(array).map(OrEntityPredicate::new);
			} else if(json instanceof JsonPrimitive primitive && primitive.isString()) {
				return Optional.of(of(json.getAsString(), true).orElse(FALSE));
			}

			return Optional.empty();
		}
	}

}
