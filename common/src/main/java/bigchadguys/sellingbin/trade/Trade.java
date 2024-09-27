package bigchadguys.sellingbin.trade;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import bigchadguys.sellingbin.data.item.*;
import bigchadguys.sellingbin.data.serializable.ISerializable;
import bigchadguys.sellingbin.data.tile.OrItemPredicate;
import bigchadguys.sellingbin.init.ModBlocks;
import bigchadguys.sellingbin.init.ModConfigs;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Trade implements ISerializable<NbtCompound, JsonObject> {

    public static final Trade ERROR = new Trade(
            new Input(PartialItem.of(ModBlocks.ERROR.get().asItem()), 1),
            PartialStack.of(new ItemStack(ModBlocks.ERROR.get().asItem())));

    private final Input input;
    private PartialStack output;

    public Trade() {
        this.input = new Input();
    }

    public Trade(Input input, PartialStack output) {
        this.input = input;
        this.output = output;
    }

    public Input getInput() {
        return this.input;
    }

    public PartialStack getOutput() {
        return this.output;
    }
    @Override
    public void writeBits(BitBuffer buffer) {
        this.input.writeBits(buffer);
        Adapters.PARTIAL_STACK.writeBits(this.output, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.input.readBits(buffer);
        this.output = Adapters.PARTIAL_STACK.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        NbtCompound nbt = new NbtCompound();
        this.input.writeNbt().ifPresent(value -> nbt.put("input", value));
        Adapters.PARTIAL_STACK.writeNbt(this.output).ifPresent(value -> nbt.put("output", value));
        return Optional.of(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.input.readNbt(nbt.getCompound("input"));
        this.output = Adapters.PARTIAL_STACK.readNbt(nbt.get("output")).orElseThrow();
    }

    @Override
    public Optional<JsonObject> writeJson() {
        JsonObject json = new JsonObject();
        this.input.writeJson().ifPresent(value -> json.add("input", value));
        Adapters.PARTIAL_STACK.writeJson(this.output).ifPresent(value -> json.add("output", value));
        return Optional.of(json);
    }

    @Override
    public void readJson(JsonObject json) {
        this.input.readJson(json.getAsJsonObject("input"));
        this.output = Adapters.PARTIAL_STACK.readJson(json.get("output")).orElseThrow();
    }

    public void validate(String path) {
        this.input.filter.validate(path + ".input");
        this.output.validate(path + ".output");
    }

    public static class Input implements ISerializable<NbtCompound, JsonObject> {
        private ItemPredicate filter;
        private int count;

        private List<Entry> cache;

        public Input() {

        }

        public Input(ItemPredicate filter, int count) {
            this.filter = filter;
            this.count = count;
        }

        public ItemPredicate getFilter() {
            return this.filter;
        }

        public int getCount() {
            return this.count;
        }

        public ItemStack getDisplay(double time) {
            if(this.cache == null) {
                this.cache = new ArrayList<>();
                this.iterate(this.filter, new NbtCompound(), this.cache);
            }

            if(this.cache.isEmpty()) {
                return new ItemStack(Items.AIR, this.count);
            }

            int index = (int)(time / 30.0D) % this.cache.size();
            return this.cache.get(index).toStack(this.count);
        }

        private void iterate(ItemPredicate filter, NbtCompound nbt, List<Entry> entries) {
            if(filter instanceof OrItemPredicate or) {
                for(ItemPredicate predicate : or.getChildren()) {
                    this.iterate(predicate, nbt, entries);
                }
            } else if(filter instanceof PartialItem item) {
                entries.add(new Entry(item.asWhole().orElse(ModBlocks.ERROR.get().asItem()), null));
            } else if(filter instanceof PartialItemGroup group) {
                for(ItemPredicate child : ModConfigs.ITEM_GROUPS.getGroup(group.getId())) {
                    NbtCompound copy = nbt.copy();
                    group.getNbt().asWhole().ifPresent(copy::copyFrom);
                    this.iterate(child, copy, entries);
                }
            } else if(filter instanceof PartialItemTag tag) {
                NbtCompound copy = nbt.copy();
                tag.getNbt().asWhole().ifPresent(copy::copyFrom);

                for(Item item : Registries.ITEM) {
                   if(Registries.ITEM.getEntry(item).streamTags().anyMatch(itemTagKey -> tag.getId().equals(itemTagKey.id()))) {
                       entries.add(new Entry(item, copy));
                   }
                }
            } else if(filter instanceof PartialStack stack) {
                ItemStack _stack = stack.generate(this.count).orElseGet(() -> new ItemStack(ModBlocks.ERROR.get().asItem(), this.count));
                entries.add(new Entry(_stack.getItem(), _stack.getNbt()));
            }
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.ITEM_PREDICATE.writeBits(this.filter, buffer);
            Adapters.INT_SEGMENTED_3.writeBits(this.count, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            this.filter = Adapters.ITEM_PREDICATE.readBits(buffer).orElseThrow();
            this.count = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            NbtCompound nbt = new NbtCompound();
            Adapters.ITEM_PREDICATE.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            Adapters.INT_SEGMENTED_3.writeNbt(this.count).ifPresent(value -> nbt.put("count", value));
            return Optional.of(nbt);
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.filter = Adapters.ITEM_PREDICATE.readNbt(nbt.get("filter")).orElseThrow();
            this.count = Adapters.INT_SEGMENTED_3.readNbt(nbt.get("count")).orElseThrow();
        }

        @Override
        public Optional<JsonObject> writeJson() {
            JsonObject json = new JsonObject();
            Adapters.ITEM_PREDICATE.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            Adapters.INT_SEGMENTED_3.writeJson(this.count).ifPresent(value -> json.add("count", value));
            return Optional.of(json);
        }

        @Override
        public void readJson(JsonObject json) {
            this.filter = Adapters.ITEM_PREDICATE.readJson(json.get("filter")).orElseThrow();
            this.count = Adapters.INT_SEGMENTED_3.readJson(json.get("count")).orElseThrow();
        }

        private record Entry(Item item, NbtCompound nbt) {
            public ItemStack toStack(int count) {
                ItemStack stack = new ItemStack(this.item, count);
                stack.setNbt(this.nbt);
                return stack;
            }
        }
    }

}
