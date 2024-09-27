package bigchadguys.sellingbin.data.nbt;

import bigchadguys.sellingbin.data.entity.EntityPlacement;
import bigchadguys.sellingbin.data.tile.PartialBlockState;
import bigchadguys.sellingbin.data.tile.TilePlacement;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

public class PartialListNbt implements TilePlacement<PartialListNbt>, EntityPlacement<PartialListNbt> {

	private AbstractNbtList<?> nbt;

	protected PartialListNbt(AbstractNbtList<?> nbt) {
		this.nbt = nbt;
	}

	public static PartialListNbt empty() {
		return new PartialListNbt(new NbtList());
	}

	public static PartialListNbt of(AbstractNbtList<?> nbt) {
		return new PartialListNbt(nbt);
	}

	@Override
	public boolean isSubsetOf(PartialListNbt other) {
		if(this.nbt == null) {
			return true;
		} else if(other.nbt == null || this.nbt.size() > other.nbt.size()) {
			return false;
		}

		for(NbtElement e1 : this.nbt) {
			boolean matched = false;
			for(NbtElement e2 : other.nbt) {
				if(e1.getType() != e2.getType()) continue;

				if(e1.getType() == NbtElement.COMPOUND_TYPE) {
					if(PartialCompoundNbt.of((NbtCompound)e1).isSubsetOf(PartialCompoundNbt.of((NbtCompound)e2))) {
						matched = true;
						break;
					}
				} else if(e1 instanceof AbstractNbtList<?>) {
					if(PartialListNbt.of((AbstractNbtList<?>)e1).isSubsetOf(PartialListNbt.of((AbstractNbtList<?>)e2))) {
						matched = true;
						break;
					}
				} else if(e1.equals(e2)) {
					matched = true;
					break;
				}
			}
			if (!matched) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isSubsetOf(BlockView world, BlockPos pos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSubsetOf(Entity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillInto(PartialListNbt other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void place(ModifiableWorld world) {

	}

	@Override
	public void place(WorldAccess world, BlockPos pos, int flags) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean test(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
		return false;
	}

	@Override
	public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
		return false;
	}

	public Optional<AbstractNbtList<?>> asWhole() {
		return Optional.ofNullable(this.nbt);
	}

	@Override
	public PartialListNbt copy() {
		return new PartialListNbt(this.nbt == null ? null : (AbstractNbtList<?>)this.nbt.copy());
	}

}
