package bigchadguys.sellingbin.data.tile;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class PartialBlockState implements TilePlacement<PartialBlockState> {

    private PartialBlock block;
    private PartialBlockProperties properties;

	protected PartialBlockState(PartialBlock block, PartialBlockProperties properties) {
		this.block = block;
		this.properties = properties;
	}

	public static PartialBlockState of(PartialBlock block, PartialBlockProperties properties) {
		return new PartialBlockState(block, properties);
	}

	public static PartialBlockState of(Block block) {
		return new PartialBlockState(PartialBlock.of(block), PartialBlockProperties.empty());
	}

	public static PartialBlockState of(BlockState state) {
		return new PartialBlockState(PartialBlock.of(state), PartialBlockProperties.of(state));
	}

	public static PartialBlockState at(BlockView world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return new PartialBlockState(PartialBlock.of(state), PartialBlockProperties.of(state));
	}

	public PartialBlock getBlock() {
		return this.block;
	}

	public PartialBlockProperties getProperties() {
		return this.properties;
	}

	public <T extends Comparable<T>> T get(Property<T> property) {
		return this.properties.get(property);
	}

	public <T extends Comparable<T>, V extends T> PartialBlockState set(Property<T> property, V value) {
		this.properties.set(property, value);
		return this;
	}

	@Override
	public boolean isSubsetOf(PartialBlockState other) {
		if(!this.block.isSubsetOf(other.block)) return false;
		if(!this.properties.isSubsetOf(other.properties)) return false;
		return true;
	}

	@Override
	public boolean isSubsetOf(BlockView world, BlockPos pos) {
		return this.isSubsetOf(PartialBlockState.of(world.getBlockState(pos)));
	}

	@Override
	public void fillInto(PartialBlockState other) {
		this.block.fillInto(other.block);
		this.properties.fillInto(other.properties);
	}

	@Override
	public void place(WorldAccess world, BlockPos pos, int flags) {
		this.asWhole().ifPresent(state -> {
			world.setBlockState(pos, state, flags);
		});
	}

	@Override
	public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
		return this.isSubsetOf(state);
	}

	public Optional<BlockState> asWhole() {
		return this.block.asWhole().map(block -> this.properties.apply(block.getDefaultState()));
	}

	public void mapAndSet(UnaryOperator<BlockState> mapper) {
		this.asWhole().ifPresent(oldState -> {
			BlockState newState = mapper.apply(oldState);
			if(oldState == newState) return;
			PartialBlock.of(newState.getBlock()).fillInto(this.block);
			PartialBlockProperties.of(newState).fillInto(this.properties);
		});
	}

	public void mirror(BlockMirror mirror) {
		this.mapAndSet(state -> state.mirror(mirror));
	}

	public void rotate(BlockRotation rotation) {
		this.mapAndSet(state -> state.rotate(rotation));
	}

	public boolean is(Block block) {
		return this.block.asWhole().map(value -> block == value).orElse(false);
	}

	@Override
	public PartialBlockState copy() {
		return PartialBlockState.of(this.block.copy(), this.properties.copy());
	}

	@Override
	public String toString() {
		return this.block.toString() + this.properties.toString();
	}

	public static class Adapter implements ISimpleAdapter<PartialBlockState, NbtElement, JsonElement> {
		@Override
		public Optional<NbtElement> writeNbt(PartialBlockState value) {
			if(value == null) {
				return Optional.empty();
			}

			NbtCompound nbt = new NbtCompound();
			Adapters.PARTIAL_BLOCK.writeNbt(value.block).ifPresent(tag -> nbt.put("Name", tag));
			Adapters.PARTIAL_BLOCK_PROPERTIES.writeNbt(value.properties).ifPresent(tag -> nbt.put("Properties", tag));
			return Optional.of(nbt);
		}

		@Override
		public Optional<PartialBlockState> readNbt(NbtElement nbt) {
			if(nbt == null) {
				return Optional.empty();
			}

			if(nbt instanceof NbtCompound compound) {
				return Optional.of(PartialBlockState.of(
					Adapters.PARTIAL_BLOCK.readNbt(compound.get("Name")).orElseGet(PartialBlock::empty),
					Adapters.PARTIAL_BLOCK_PROPERTIES.readNbt(compound.get("Properties")).orElseGet(PartialBlockProperties::empty)
				));
			}

			return Optional.empty();
		}
	}

	public static Optional<PartialBlockState> parse(String string, boolean logErrors) {
		try {
			return Optional.of(parse(new StringReader(string)));
		} catch(CommandSyntaxException | IllegalArgumentException e) {
			if(logErrors) {
				e.printStackTrace();
			}
		}

		return Optional.empty();
	}

	public static PartialBlockState parse(String string) throws CommandSyntaxException {
		return parse(new StringReader(string));
	}

	public static PartialBlockState parse(StringReader reader) throws CommandSyntaxException {
		return PartialBlockState.of(PartialBlock.parse(reader), PartialBlockProperties.parse(reader));
	}

}
