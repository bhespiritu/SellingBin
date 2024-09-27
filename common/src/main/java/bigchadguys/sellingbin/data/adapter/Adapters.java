package bigchadguys.sellingbin.data.adapter;

import bigchadguys.sellingbin.data.adapter.array.ArrayAdapter;
import bigchadguys.sellingbin.data.adapter.array.ByteArrayAdapter;
import bigchadguys.sellingbin.data.adapter.array.IntArrayAdapter;
import bigchadguys.sellingbin.data.adapter.array.LongArrayAdapter;
import bigchadguys.sellingbin.data.adapter.basic.*;
import bigchadguys.sellingbin.data.adapter.nbt.*;
import bigchadguys.sellingbin.data.adapter.number.*;
import bigchadguys.sellingbin.data.adapter.util.BlockPosAdapter;
import bigchadguys.sellingbin.data.adapter.util.IdentifierAdapter;
import bigchadguys.sellingbin.data.adapter.util.ItemStackAdapter;
import bigchadguys.sellingbin.data.adapter.util.RegistryAdapter;
import bigchadguys.sellingbin.data.entity.EntityPredicate;
import bigchadguys.sellingbin.data.item.ItemPredicate;
import bigchadguys.sellingbin.data.item.PartialItem;
import bigchadguys.sellingbin.data.item.PartialStack;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import bigchadguys.sellingbin.data.tile.*;
import bigchadguys.sellingbin.trade.Trade;
import bigchadguys.sellingbin.world.random.ChunkRandom;
import bigchadguys.sellingbin.world.random.JavaRandom;
import bigchadguys.sellingbin.world.random.LcgRandom;
import bigchadguys.sellingbin.world.random.RandomSource;
import bigchadguys.sellingbin.world.random.lcg.Lcg;
import bigchadguys.sellingbin.world.roll.IntRoll;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.nio.charset.StandardCharsets;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class Adapters {

    public static final BooleanAdapter BOOLEAN = new BooleanAdapter(false);
    public static final NumericAdapter NUMERIC = new NumericAdapter(false);

    public static final ByteAdapter BYTE = new ByteAdapter(false);
    public static final ByteArrayAdapter BYTE_ARRAY = new ByteArrayAdapter(BYTE, false);

    public static final ShortAdapter SHORT = new ShortAdapter(false);

    public static final CharAdapter CHAR = new CharAdapter(false);

    public static final IntAdapter INT = new IntAdapter(false);
    public static final IntArrayAdapter INT_ARRAY = new IntArrayAdapter(INT, false);
    public static final SegmentedIntAdapter INT_SEGMENTED_3 = new SegmentedIntAdapter(3, false);
    public static final SegmentedIntAdapter INT_SEGMENTED_7 = new SegmentedIntAdapter(7, false);
    public static final IntRoll.Adapter INT_ROLL = new IntRoll.Adapter();

    public static final FloatAdapter FLOAT = new FloatAdapter(false);

    public static final LongAdapter LONG = new LongAdapter(false);
    public static final SegmentedLongAdapter LONG_SEGMENTED_3 = new SegmentedLongAdapter(3, false);
    public static final SegmentedLongAdapter LONG_SEGMENTED_7 = new SegmentedLongAdapter(7, false);
    public static final SegmentedLongAdapter LONG_SEGMENTED_15 = new SegmentedLongAdapter(15, false);
    public static final LongArrayAdapter LONG_ARRAY = new LongArrayAdapter(LONG, false);

    public static final DoubleAdapter DOUBLE = new DoubleAdapter(false);

    public static final BigIntegerAdapter BIG_INTEGER = new BigIntegerAdapter(false);

    public static final BigDecimalAdapter BIG_DECIMAL = new BigDecimalAdapter(false);

    public static final VoidAdapter<?> VOID = new VoidAdapter<>();
    public static final StringAdapter UTF_8 = new StringAdapter(StandardCharsets.UTF_8, false);
    public static final UuidAdapter UUID = new UuidAdapter(false);

    public static final NumericNbtAdapter NUMERIC_NBT = new NumericNbtAdapter(false);
    public static final CollectionNbtAdapter COLLECTION_NBT = new CollectionNbtAdapter(false);

    public static final EndNbtAdapter END_NBT = new EndNbtAdapter(false);
    public static final ByteNbtAdapter BYTE_NBT = new ByteNbtAdapter(false);
    public static final ShortNbtAdapter SHORT_NBT = new ShortNbtAdapter(false);
    public static final IntNbtAdapter INT_NBT = new IntNbtAdapter(false);
    public static final LongNbtAdapter LONG_NBT = new LongNbtAdapter(false);
    public static final FloatNbtAdapter FLOAT_NBT = new FloatNbtAdapter(false);
    public static final DoubleNbtAdapter DOUBLE_NBT = new DoubleNbtAdapter(false);
    public static final ByteArrayNbtAdapter BYTE_ARRAY_NBT = new ByteArrayNbtAdapter(false);
    public static final StringNbtAdapter STRING_NBT = new StringNbtAdapter(false);
    public static final ListNbtAdapter LIST_NBT = new ListNbtAdapter(false);
    public static final CompoundNbtAdapter COMPOUND_NBT = new CompoundNbtAdapter(false);
    public static final IntArrayNbtAdapter INT_ARRAY_NBT = new IntArrayNbtAdapter(false);
    public static final LongArrayNbtAdapter LONG_ARRAY_NBT = new LongArrayNbtAdapter(false);

    public static final NbtAdapter[] NBT = new NbtAdapter[] {
            Adapters.END_NBT,
            Adapters.BYTE_NBT,
            Adapters.SHORT_NBT,
            Adapters.INT_NBT,
            Adapters.LONG_NBT,
            Adapters.FLOAT_NBT,
            Adapters.DOUBLE_NBT,
            Adapters.BYTE_ARRAY_NBT,
            Adapters.STRING_NBT,
            Adapters.LIST_NBT,
            Adapters.COMPOUND_NBT,
            Adapters.INT_ARRAY_NBT,
            Adapters.LONG_ARRAY_NBT
    };
    public static final GenericNbtAdapter GENERIC_NBT = new GenericNbtAdapter(false);

    public static final IdentifierAdapter IDENTIFIER = new IdentifierAdapter(false);
    public static final ItemStackAdapter ITEM_STACK = new ItemStackAdapter(false);

    public static final PartialBlock.Adapter PARTIAL_BLOCK = new PartialBlock.Adapter();
    public static final PartialBlockProperties.Adapter PARTIAL_BLOCK_PROPERTIES = new PartialBlockProperties.Adapter();
    public static final PartialBlockState.Adapter PARTIAL_BLOCK_STATE = new PartialBlockState.Adapter();
    public static final PartialCompoundNbt.Adapter PARTIAL_NBT = new PartialCompoundNbt.Adapter();
    public static final PartialTile.Adapter PARTIAL_TILE = new PartialTile.Adapter();
    public static final PartialItem.Adapter PARTIAL_ITEM = new PartialItem.Adapter();
    public static final PartialStack.Adapter PARTIAL_STACK = new PartialStack.Adapter();
    public static final TilePredicate.Adapter TILE_PREDICATE = new TilePredicate.Adapter();
    public static final EntityPredicate.Adapter ENTITY_PREDICATE = new EntityPredicate.Adapter();
    public static final ItemPredicate.Adapter ITEM_PREDICATE = new ItemPredicate.Adapter();

    public static final RegistryAdapter<Block> BLOCK = new RegistryAdapter<>(() -> Registries.BLOCK, false);
    public static final RegistryAdapter<Item> ITEM = new RegistryAdapter<>(() -> Registries.ITEM, false);
    public static final BlockPosAdapter BLOCK_POS = new BlockPosAdapter(false);
    public static final SerializableAdapter<Trade, ?, ?> TRADE = of(Trade::new, true);

    public static Lcg.Adapter LCG = new Lcg.Adapter(false);

    public static TypeSupplierAdapter<RandomSource> RANDOM = new TypeSupplierAdapter<RandomSource>("type", false)
        .register("lcg", LcgRandom.class, () -> LcgRandom.of(Lcg.JAVA, 0L))
        .register("java", JavaRandom.class, () -> JavaRandom.ofInternal(0L))
        .register("chunk", ChunkRandom.class, ChunkRandom::any);

    public static BoundedIntAdapter ofBoundedInt(int bound) {
        return new BoundedIntAdapter(0, bound - 1, false);
    }

    public static BoundedIntAdapter ofBoundedInt(int min, int max) {
        return new BoundedIntAdapter(min, max, false);
    }

    public static BoundedLongAdapter ofBoundedLong(long bound) {
        return new BoundedLongAdapter(0, bound - 1, false);
    }

    public static BoundedLongAdapter ofBoundedLong(long min, long max) {
        return new BoundedLongAdapter(min, max, false);
    }

    public static <T> ArrayAdapter<T> ofArray(IntFunction<T[]> constructor, Object elementAdapter) {
        return new ArrayAdapter<>(constructor, elementAdapter, () -> null, false);
    }

    public static <T> VoidAdapter<T> ofVoid() {
        return new VoidAdapter<>();
    }

    public static <E extends Enum<E>> EnumAdapter<E> ofEnum(Class<E> type, EnumAdapter.Mode mode) {
        return new EnumAdapter<>(type, mode, false);
    }

    public static <T> OrdinalAdapter<T> ofOrdinal(ToIntFunction<T> mapper, T... array) {
        return new OrdinalAdapter<>(mapper, false, array);
    }

    public static <T> SerializableAdapter<T, ?, ?> of(Supplier<T> constructor, boolean nullable) {
        return new SerializableAdapter<>(constructor, nullable);
    }

}
