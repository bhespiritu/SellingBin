package bigchadguys.sellingbin.data.item;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public class PartialStack implements ItemPlacement<PartialStack> {

    protected PartialItem item;
    protected PartialCompoundNbt nbt;
    protected int count;

    protected PartialStack(PartialItem item, PartialCompoundNbt nbt, int count) {
        this.item = item;
        this.nbt = nbt;
        this.count = count;
    }

    public static PartialStack of(PartialItem item, PartialCompoundNbt nbt) {
        return new PartialStack(item, nbt, 1);
    }

    public static PartialStack of(ItemStack stack) {
        return new PartialStack(PartialItem.of(stack), PartialCompoundNbt.of(stack), 1);
    }

    public PartialItem getItem() {
        return this.item;
    }

    public PartialCompoundNbt getNbt() {
        return this.nbt;
    }

    public int getCount() {
        return this.count;
    }

    public void setItem(PartialItem item) {
        this.item = item;
    }

    public void setNbt(PartialCompoundNbt nbt) {
        this.nbt = nbt;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean isSubsetOf(PartialStack other) {
        if(!this.item.isSubsetOf(other.item)) return false;
        if(!this.nbt.isSubsetOf(other.nbt)) return false;
        return true;
    }

    @Override
    public boolean isSubsetOf(ItemStack stack) {
        if(!this.item.isSubsetOf(stack)) return false;
        if(!this.nbt.isSubsetOf(stack)) return false;
        return true;
    }

    @Override
    public void fillInto(PartialStack other) {
        this.item.fillInto(other.item);
        this.nbt.fillInto(other.nbt);
    }

    @Override
    public Optional<ItemStack> generate(int count) {
        return this.item.generate(count).map(stack -> {
            stack.setNbt(this.nbt.asWhole().orElse(null));
            return stack;
        });
    }

    public Optional<ItemStack> generate() {
        return this.generate(this.count);
    }


    @Override
    public boolean test(PartialItem item, PartialCompoundNbt nbt) {
        return this.isSubsetOf(PartialStack.of(item, nbt));
    }

    @Override
    public void validate(String path) {
        this.item.validate(path + ".item");
        this.nbt.validate(path + ".nbt");
    }

    @Override
    public PartialStack copy() {
        return new PartialStack(this.item.copy(), this.nbt.copy(), this.count);
    }

    @Override
    public String toString() {
        return this.item.toString() + this.nbt.toString();
    }

    public static class Adapter implements ISimpleAdapter<PartialStack, NbtElement, JsonElement> {
        @Override
        public Optional<NbtElement> writeNbt(PartialStack value) {
            if(value == null) {
                return Optional.empty();
            }

            NbtCompound nbt = new NbtCompound();
            Adapters.PARTIAL_ITEM.writeNbt(value.item).ifPresent(tag -> nbt.put("item", tag));
            Adapters.PARTIAL_NBT.writeNbt(value.nbt).ifPresent(tag -> nbt.put("nbt", tag));

            if(value.count != 1) {
                Adapters.INT.writeNbt(value.count).ifPresent(tag -> nbt.put("count", tag));
            }

            return Optional.of(nbt);
        }

        @Override
        public Optional<PartialStack> readNbt(NbtElement nbt) {
            if(nbt instanceof NbtCompound compound) {
                PartialItem item = Adapters.PARTIAL_ITEM.readNbt(compound.get("item")).orElseThrow();
                PartialCompoundNbt tag = Adapters.PARTIAL_NBT.readNbt(compound.get("nbt")).orElseGet(PartialCompoundNbt::empty);
                int count = Adapters.INT.readNbt(compound.get("count")).orElse(1);
                return Optional.of(new PartialStack(item, tag, count));
            }

            return Optional.empty();
        }

        @Override
        public Optional<JsonElement> writeJson(PartialStack value) {
            if(value == null) {
                return Optional.empty();
            }

            JsonObject nbt = new JsonObject();
            Adapters.PARTIAL_ITEM.writeJson(value.item).ifPresent(tag -> nbt.add("item", tag));
            Adapters.PARTIAL_NBT.writeJson(value.nbt).ifPresent(tag -> nbt.add("nbt", tag));

            if(value.count != 1) {
                Adapters.INT.writeJson(value.count).ifPresent(tag -> nbt.add("count", tag));
            }

            return Optional.of(nbt);
        }

        @Override
        public Optional<PartialStack> readJson(JsonElement nbt) {
            if(nbt instanceof JsonObject object) {
                PartialItem item = Adapters.PARTIAL_ITEM.readJson(object.get("item")).orElseThrow();
                PartialCompoundNbt tag = Adapters.PARTIAL_NBT.readJson(object.get("nbt")).orElseGet(PartialCompoundNbt::empty);
                int count = Adapters.INT.readJson(object.get("count")).orElse(1);
                return Optional.of(new PartialStack(item, tag, count));
            }

            return Optional.empty();
        }
    }

    public static Optional<PartialStack> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialStack parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialStack parse(StringReader reader) throws CommandSyntaxException {
        return PartialStack.of(PartialItem.parse(reader), PartialCompoundNbt.parse(reader));
    }

}
