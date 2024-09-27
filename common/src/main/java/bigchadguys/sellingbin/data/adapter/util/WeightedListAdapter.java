package bigchadguys.sellingbin.data.adapter.util;

import bigchadguys.sellingbin.util.WeightedList;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

public class WeightedListAdapter<E> extends TypeAdapter<WeightedList<E>> {

	private final TypeAdapter<E> elementTypeAdapter;
	private final ObjectConstructor<? extends WeightedList<E>> constructor;

	public WeightedListAdapter(Gson context, Type elementType,
							   TypeAdapter<E> elementTypeAdapter,
							   ObjectConstructor<? extends WeightedList<E>> constructor) {
		this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(context, elementTypeAdapter, elementType);
		this.constructor = constructor;
	}

	@Override
	public void write(JsonWriter out, WeightedList<E> value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}

		out.beginArray();

		for(Map.Entry<E, Double> e : value.entrySet()) {
			out.beginObject();
			out.name("value");
			this.elementTypeAdapter.write(out, e.getKey());
			out.name("weight");
			out.value(e.getValue());
			out.endObject();
		}

		out.endArray();
	}

	@Override
	public WeightedList<E> read(JsonReader in) throws IOException {
		if(in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}

		WeightedList<E> collection = this.constructor.construct();
		in.beginArray();

		while(in.hasNext()) {
			in.beginObject();
			E instance = null;
			int weight = 1;

			while(in.peek() == JsonToken.NAME) {
				switch(in.nextName()) {
					case "value" -> instance = this.elementTypeAdapter.read(in);
					case "weight" -> weight = in.nextInt();
				}
			}

			collection.put(instance, weight);
			in.endObject();
		}

		in.endArray();
		return collection;
	}

	public static class Factory implements TypeAdapterFactory {
		public static final Factory INSTANCE = new Factory();

		private final ConstructorConstructor constructorConstructor = new ConstructorConstructor(Collections.emptyMap(), true, Collections.emptyList());

		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
			Type type = typeToken.getType();

			Class<? super T> rawType = typeToken.getRawType();

			if(!WeightedList.class.isAssignableFrom(rawType)) {
				return null;
			}

			Type elementType = getElementType(type, rawType);
			TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
			ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);

			@SuppressWarnings({"unchecked", "rawtypes"}) // create() doesn't define a type parameter
			TypeAdapter<T> result = new WeightedListAdapter<>(gson, elementType, elementTypeAdapter, (ObjectConstructor)constructor);
			return result;
		}

		public Type getElementType(Type context, Class<?> contextRawType) {
			Type collectionType = getSupertype(context, contextRawType, WeightedList.class);

			if (collectionType instanceof WildcardType) {
				collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
			}
			if (collectionType instanceof ParameterizedType) {
				return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
			}
			return Object.class;
		}

		public Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
			if (context instanceof WildcardType) {
				// wildcards are useless for resolving supertypes. As the upper bound has the same raw type, use it instead
				context = ((WildcardType)context).getUpperBounds()[0];
			}
			checkArgument(supertype.isAssignableFrom(contextRawType));

			return $Gson$Types.resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
		}

		public Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
			if (toResolve == rawType) {
				return context;
			}

			// we skip searching through interfaces if unknown is an interface
			if (toResolve.isInterface()) {
				Class<?>[] interfaces = rawType.getInterfaces();
				for (int i = 0, length = interfaces.length; i < length; i++) {
					if (interfaces[i] == toResolve) {
						return rawType.getGenericInterfaces()[i];
					} else if (toResolve.isAssignableFrom(interfaces[i])) {
						return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
					}
				}
			}

			// check our supertypes
			if (!rawType.isInterface()) {
				while (rawType != Object.class) {
					Class<?> rawSupertype = rawType.getSuperclass();
					if (rawSupertype == toResolve) {
						return rawType.getGenericSuperclass();
					} else if (toResolve.isAssignableFrom(rawSupertype)) {
						return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
					}
					rawType = rawSupertype;
				}
			}

			// we can't resolve this further
			return toResolve;
		}
	}

	private static class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
		private final Gson context;
		private final TypeAdapter<T> delegate;
		private final Type type;

		TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
			this.context = context;
			this.delegate = delegate;
			this.type = type;
		}

		@Override
		public T read(JsonReader in) throws IOException {
			return delegate.read(in);
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			// Order of preference for choosing type adapters
			// First preference: a type adapter registered for the runtime type
			// Second preference: a type adapter registered for the declared type
			// Third preference: reflective type adapter for the runtime type (if it is a sub class of the declared type)
			// Fourth preference: reflective type adapter for the declared type

			TypeAdapter chosen = delegate;
			Type runtimeType = getRuntimeTypeIfMoreSpecific(type, value);
			if (runtimeType != type) {
				TypeAdapter runtimeTypeAdapter = context.getAdapter(TypeToken.get(runtimeType));
				if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
					// The user registered a type adapter for the runtime type, so we will use that
					chosen = runtimeTypeAdapter;
				} else if (!(delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
					// The user registered a type adapter for Base class, so we prefer it over the
					// reflective type adapter for the runtime type
					chosen = delegate;
				} else {
					// Use the type adapter for runtime type
					chosen = runtimeTypeAdapter;
				}
			}
			chosen.write(out, value);
		}

		/**
		 * Finds a compatible runtime type if it is more specific
		 */
		private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
			if (value != null
				&& (type == Object.class || type instanceof TypeVariable<?> || type instanceof Class<?>)) {
				type = value.getClass();
			}
			return type;
		}
	}


}
