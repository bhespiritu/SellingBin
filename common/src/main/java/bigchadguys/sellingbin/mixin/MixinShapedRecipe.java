package bigchadguys.sellingbin.mixin;

import bigchadguys.sellingbin.data.adapter.Adapters;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe {

    @Inject(method = "outputFromJson", at = @At("HEAD"), cancellable = true)
    private static void outputFromJson(JsonObject json, CallbackInfoReturnable<ItemStack> ci) {
        if(json.has("nbt")) {
            Item item = ShapedRecipe.getItem(json);
            int i = JsonHelper.getInt(json, "count", 1);

            if(i < 1) {
                throw new JsonSyntaxException("Invalid output count: " + i);
            }

            ItemStack stack = new ItemStack(item, i);
            stack.setNbt(Adapters.COMPOUND_NBT.readJson(json.get("nbt")).orElse(null));
            ci.setReturnValue(stack);
        }
    }

}
