package bigchadguys.sellingbin.util;

import bigchadguys.sellingbin.world.random.RandomSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

public class WeightedList<T> extends AbstractMap<T, Double> {

	private static final WeightedList<?> EMPTY = new WeightedList<>();
	private final Map<T, Double> delegate = new LinkedHashMap<>();

	public static <T> T empty() {
		return (T)EMPTY;
	}

	public WeightedList() {

	}

	public <J extends JsonElement> Optional<JsonElement> writeJson(Function<T, Optional<J>> serializer) {
		if(this.delegate.isEmpty()) {
			return Optional.of(JsonNull.INSTANCE);
		} else if(this.delegate.size() == 1) {
			return (Optional<JsonElement>)serializer.apply(this.delegate.keySet().iterator().next());
		}

		JsonArray array = new JsonArray();

		for(Entry<T, Double> entry : this.delegate.entrySet()) {
			JsonElement element = serializer.apply(entry.getKey()).orElse(null);
			if(element == null) continue;
			JsonObject object;

			if(element instanceof JsonObject) {
				object = (JsonObject)element;
			} else {
				object = new JsonObject();
				object.add("value", element);
			}

			object.addProperty("weight", entry.getValue());
			array.add(object);
		}

		return Optional.of(array);
	}

	public <J extends JsonElement> void readJson(JsonElement json, Function<J, Optional<T>> deserializer) {
		this.clear();

		if(json instanceof JsonObject object) {
			if(object.has("value")) {
				deserializer.apply((J)object.get("value")).ifPresent(value -> {
					this.put(value, object.get("weight").getAsDouble());
				});
			} else {
				deserializer.apply((J)object).ifPresent(value -> {
					this.put(value, object.has("weight") ? object.get("weight").getAsDouble() : 1);
				});
			}
		} else if(json instanceof JsonArray array) {
			for(JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();

				if(object.has("value")) {
					deserializer.apply((J)object.get("value")).ifPresent(value -> {
						this.put(value, object.get("weight").getAsDouble());
					});
				} else {
					deserializer.apply((J)object).ifPresent(value -> {
						this.put(value, object.get("weight").getAsDouble());
					});
				}
			}
		}
	}

	public <J extends JsonElement> void readJson(JsonElement json, Supplier<T> constructor, BiConsumer<T, J> deserializer) {
		this.clear();

		if(json instanceof JsonObject object) {
			T value = constructor.get();

			if(object.has("value")) {
				deserializer.accept(value, (J)object.get("value"));
			} else {
				deserializer.accept(value, (J)object);
			}

			this.put(value, object.has("weight") ? object.get("weight").getAsDouble() : 1);
		} else if(json instanceof JsonArray array) {
			for(JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();
				T value = constructor.get();

				if(object.has("value")) {
					deserializer.accept(value, (J)object.get("value"));
				} else {
					deserializer.accept(value, (J)object);
				}

				this.put(value, object.get("weight").getAsDouble());
			}
		}
	}

	@NotNull
	@Override
	public Set<Entry<T, Double>> entrySet() {
		return this.delegate.entrySet();
	}

	@Override
	public Double put(T value, Double weight) {
		return this.delegate.put(value, weight);
	}

	public Double put(T value, Number weight) {
		return this.put(value, weight.doubleValue());
	}

	public WeightedList<T> add(T value, Double weight) {
		double current = this.getOrDefault(value, 0.0D);
		this.put(value, current + weight);
		return this;
	}

	public WeightedList<T> add(T value, Number weight) {
		return this.add(value, weight.doubleValue());
	}

	public double getTotalWeight() {
		double sum = 0;

		for(double weight : this.values()) {
			sum += Math.max(weight, 0);
		}

		return sum;
	}

	public Optional<T> getRandom() {
		return this.getRandom(new Random());
	}

	public Optional<T> getRandom(RandomGenerator random) {
		return this.getRandom((DoubleUnaryOperator)random::nextDouble);
	}

	public Optional<T> getRandom(RandomSource random) {
		return this.getRandom((DoubleUnaryOperator)random::nextDouble);
	}

	public Optional<T> getRandom(DoubleUnaryOperator random) {
		double total = this.getTotalWeight();

		if(total <= 0) {
			return Optional.empty();
		}

		double index = random.applyAsDouble(total);

		for(Entry<T, Double> entry : this.delegate.entrySet()) {
			T value = entry.getKey();
			double weight = Math.max(entry.getValue(), 0);

			if(index < weight) {
				return Optional.ofNullable(value);
			}

			index -= weight;
		}

		return Optional.empty();
	}

}
