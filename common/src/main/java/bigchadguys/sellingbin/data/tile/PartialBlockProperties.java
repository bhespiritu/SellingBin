package bigchadguys.sellingbin.data.tile;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class PartialBlockProperties implements TilePlacement<PartialBlockProperties> {

    private final Map<String, String> properties;

	protected PartialBlockProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public static PartialBlockProperties empty() {
		return new PartialBlockProperties(new HashMap<>());
	}

	public static PartialBlockProperties of(Map<String, String> properties) {
		return new PartialBlockProperties(properties);
	}

	public static PartialBlockProperties of(BlockState state) {
		Map<String, String> properties = new HashMap<>();

		for(Property property : state.getProperties()) {
			properties.put(property.getName(), property.name(state.get(property)));
		}

		return new PartialBlockProperties(properties);
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public <T extends Comparable<T>> T get(Property<T> property) {
		return (T)property.parse(this.properties.get(property.getName())).orElse(null);
	}

	public <T extends Comparable<T>, V extends T> PartialBlockProperties set(Property<T> property, V value) {
		this.properties.put(property.getName(), property.name(value));
		return this;
	}

	@Override
	public boolean isSubsetOf(PartialBlockProperties other) {
		if(other == null) {
			return false;
		}

		for(Map.Entry<String, String> entry : this.properties.entrySet()) {
			if(!entry.getValue().equals(other.properties.get(entry.getKey()))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isSubsetOf(BlockView world, BlockPos pos) {
		return this.isSubsetOf(PartialBlockProperties.of(world.getBlockState(pos)));
	}

	@Override
	public void fillInto(PartialBlockProperties other) {
		other.properties.putAll(this.properties);
	}

	@Override
	public void place(WorldAccess world, BlockPos pos, int flags) {
		BlockState oldState = world.getBlockState(pos);
		BlockState newState = this.apply(oldState);

		if(oldState != newState) {
			world.setBlockState(pos, newState, flags);
		}
	}

	@Override
	public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
		return this.isSubsetOf(state.getProperties());
	}

	public BlockState apply(BlockState state) {
		StateManager<Block, BlockState> definition = state.getBlock().getStateManager();

		for(Map.Entry<String, String> entry : this.properties.entrySet()) {
			Property property = definition.getProperty(entry.getKey());
			if(property == null || !state.contains(property)) continue;
			Optional<?> value = property.parse(entry.getValue());
			if(value.isEmpty()) continue;
			state = state.with(property, (Comparable)value.get());
		}

		return state;
	}

	@Override
	public PartialBlockProperties copy() {
		return new PartialBlockProperties(new HashMap<>(this.properties));
	}

	@Override
	public String toString() {
		if(this.properties.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');

		Iterator<Map.Entry<String, String>> iterator = this.properties.entrySet().iterator();

		while(iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			sb.append(entry.getKey()).append('=').append(entry.getValue());
			if(iterator.hasNext()) sb.append(',');
		}

		return sb.append(']').toString();
	}

	public static class Adapter implements ISimpleAdapter<PartialBlockProperties, NbtElement, JsonElement> {
		@Override
		public Optional<NbtElement> writeNbt(PartialBlockProperties value) {
			if(value == null || value.properties.isEmpty()) {
				return Optional.empty();
			}

			NbtCompound nbt = new NbtCompound();
			value.properties.forEach(nbt::putString);
			return Optional.of(nbt);
		}

		@Override
		public Optional<PartialBlockProperties> readNbt(NbtElement nbt) {
			if(nbt instanceof NbtCompound compound) {
				Map<String, String> properties = new HashMap<>();

				for(String key : compound.getKeys()) {
					if(compound.get(key) instanceof NbtString string) {
						properties.put(key, string.asString());
					}
				}

				return Optional.of(PartialBlockProperties.of(properties));
			} else if(nbt instanceof NbtString string) {
				return parse(string.asString(), true);
			}

			return Optional.empty();
		}
	}

	public static Optional<PartialBlockProperties> parse(String string, boolean logErrors) {
		try {
			return Optional.of(parse(new StringReader(string)));
		} catch(CommandSyntaxException | IllegalArgumentException e) {
			if(logErrors) {
				e.printStackTrace();
			}
		}

		return Optional.empty();
	}

	public static PartialBlockProperties parse(String string) throws CommandSyntaxException {
		return parse(new StringReader(string));
	}

	public static PartialBlockProperties parse(StringReader reader) throws CommandSyntaxException {
		Map<String, String> properties = new HashMap<>();

		if(!reader.canRead() || reader.peek() != '[') {
			return PartialBlockProperties.of(properties);
		}

		reader.skip();
		int cursor = -1;
		reader.skipWhitespace();

		while(true) {
			if(reader.canRead() && reader.peek() != ']') {
				reader.skipWhitespace();
				String key = reader.readString();
				int prevCursor = reader.getCursor();
				reader.skipWhitespace();

				if(properties.containsKey(key)) {
					reader.setCursor(cursor);
					throw new IllegalArgumentException("Duplicate property <" + key + "> in tile '" + reader.getString() + "'");
				}

				if(!reader.canRead() || reader.peek() != '=') {
					reader.setCursor(prevCursor);
					throw new IllegalArgumentException("Empty property <" + key + "> in tile '" + reader.getString() + "'");
				}

				reader.skip();
				reader.skipWhitespace();

				cursor = reader.getCursor();
				String value = reader.readString();
				properties.put(key, value);
				reader.skipWhitespace();

				if(!reader.canRead()) {
					continue;
				}

				cursor = -1;

				if(reader.peek() == ',') {
					reader.skip();
					continue;
				}

				if(reader.peek() != ']') {
					throw new IllegalArgumentException("Unclosed properties in tile '" + reader.getString() + "'");
				}
			}

			if(reader.canRead()) {
				reader.skip();
				return PartialBlockProperties.of(properties);
			}

			if(cursor >= 0) {
				reader.setCursor(cursor);
			}

			throw new IllegalArgumentException("Unclosed properties in tile '" + reader.getString() + "'");
		}
	}

}
