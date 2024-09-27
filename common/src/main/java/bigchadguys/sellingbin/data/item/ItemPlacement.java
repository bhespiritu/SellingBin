package bigchadguys.sellingbin.data.item;

import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface ItemPlacement<T> extends ItemPredicate {

    boolean isSubsetOf(T other);

    boolean isSubsetOf(ItemStack stack);

    void fillInto(T other);

    Optional<ItemStack> generate(int count);

    T copy();

}
