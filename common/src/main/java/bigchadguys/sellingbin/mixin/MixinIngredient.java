package bigchadguys.sellingbin.mixin;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ingredient.class)
public class MixinIngredient {

    @Unique private final ThreadLocal<ItemStack> self = new ThreadLocal<>();

    @Inject(method = "test(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"))
    public void testHead(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
        this.self.set(itemStack);
    }

    @Inject(method = "test(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    public void testReturn(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
        this.self.remove();
    }

    @Redirect(method = "test(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    public boolean test(ItemStack other, Item item) {
        if(!other.isOf(this.self.get().getItem())) {
            return false;
        }

        if(other.getNbt() != null) {
            PartialCompoundNbt a = PartialCompoundNbt.of(other.getNbt());
            PartialCompoundNbt b = PartialCompoundNbt.of(this.self.get().getNbt());
            return a.isSubsetOf(b);
        }

        return true;
    }

    @Inject(method = "entryFromJson", at = @At("RETURN"))
    private static void entryFromJson(JsonObject json, CallbackInfoReturnable<Ingredient.Entry> ci) {
        NbtCompound nbt = Adapters.COMPOUND_NBT.readJson(json.get("nbt")).orElse(null);

        if(nbt != null && ci.getReturnValue() instanceof Ingredient.StackEntry entry) {
            for(ItemStack stack : entry.getStacks()) {
                stack.setNbt(nbt);
            }
        }
    }

}
