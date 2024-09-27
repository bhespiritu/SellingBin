package bigchadguys.sellingbin.mixin;

import bigchadguys.sellingbin.render.SellingBinTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public class MixinDrawContext {

    @Inject(method = "drawItemTooltip", at = @At("HEAD"))
    public void drawItemTooltipHead(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        SellingBinTooltipComponent.STACK.set(stack);
    }

    @Inject(method = "drawItemTooltip", at = @At("RETURN"))
    public void drawItemTooltipReturn(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        SellingBinTooltipComponent.STACK.remove();
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci, List<TooltipComponent> list) {
        if(SellingBinTooltipComponent.STACK.get() != null) {
            SellingBinTooltipComponent component = new SellingBinTooltipComponent(SellingBinTooltipComponent.STACK.get());

            if(component.isActive()) {
                list.add(component);
            }
        }
    }

}
