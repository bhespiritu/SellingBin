package bigchadguys.sellingbin.data.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Optional;

public interface IJsonAdapter<T, J extends JsonElement, C> extends JsonSerializer<T>, JsonDeserializer<T> {

    Optional<J> writeJson(T value, C context);

    Optional<T> readJson(J json, C context);

    @Override
    default JsonElement serialize(T value, Type source, JsonSerializationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default T deserialize(JsonElement json, Type source, JsonDeserializationContext context) {
        throw new UnsupportedOperationException();
    }

}
