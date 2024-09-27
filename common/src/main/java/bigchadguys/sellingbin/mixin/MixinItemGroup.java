package bigchadguys.sellingbin.mixin;

import bigchadguys.sellingbin.block.BinMaterial;
import bigchadguys.sellingbin.block.BinMode;
import bigchadguys.sellingbin.item.SellingBinItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mixin(value = ItemGroup.class, priority = 1001)
public class MixinItemGroup {

    @Shadow private Collection<ItemStack> displayStacks;
    @Shadow private Set<ItemStack> searchTabStacks;

    @Inject(method = "updateEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;reloadSearchProvider()V"))
    public void updateEntries(ItemGroup.DisplayContext displayContext, CallbackInfo ci) {
        final ItemGroup self = (ItemGroup)(Object)this;
        final RegistryKey<ItemGroup> registryKey = Registries.ITEM_GROUP.getKey(self).orElse(null);

        if(ItemGroups.REDSTONE.equals(registryKey)) {
            List<ItemStack> bins = new ArrayList<>();
            bins.add(SellingBinItem.create(BinMaterial.REDSTONE, BinMode.BLOCK_BOUND));
            this.displayStacks.addAll(bins);
            this.searchTabStacks.addAll(bins);
        } else if(ItemGroups.FUNCTIONAL.equals(registryKey)) {
            List<ItemStack> bins = new ArrayList<>();
            bins.add(SellingBinItem.create(BinMaterial.WOOD, BinMode.PLAYER_BOUND));
            bins.add(SellingBinItem.create(BinMaterial.REDSTONE, BinMode.BLOCK_BOUND));
            bins.add(SellingBinItem.create(BinMaterial.IRON, BinMode.PLAYER_BOUND));
            bins.add(SellingBinItem.create(BinMaterial.DIAMOND, BinMode.PLAYER_BOUND));
            bins.add(SellingBinItem.create(BinMaterial.NETHERITE, BinMode.PLAYER_BOUND));
            this.displayStacks.addAll(bins);
            this.searchTabStacks.addAll(bins);
        }
    }

}
