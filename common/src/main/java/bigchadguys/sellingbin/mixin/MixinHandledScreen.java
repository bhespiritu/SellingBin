package bigchadguys.sellingbin.mixin;

import bigchadguys.sellingbin.render.SellingBinTooltipComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

    @Shadow protected Slot focusedSlot;

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    protected void drawMouseoverTooltipHead(DrawContext context, int x, int y, CallbackInfo ci) {
        if(this.focusedSlot != null) {
            SellingBinTooltipComponent.STACK.set(this.focusedSlot.getStack());
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("RETURN"))
    protected void drawMouseoverTooltipReturn(DrawContext context, int x, int y, CallbackInfo ci) {
        SellingBinTooltipComponent.STACK.remove();
    }

}
