package bigchadguys.sellingbin.screen;

import bigchadguys.sellingbin.screen.handler.SellingBinScreenHandler;
import bigchadguys.sellingbin.world.data.SellingBinData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class SellingBinScreen extends HandledScreen<SellingBinScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final int rows;

    public SellingBinScreen(SellingBinScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.rows = handler.getRows();
        this.backgroundHeight = 114 + this.rows * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        this.getScreenHandler().getBlockEntity().ifPresent(bin -> {
            long timeLeft = switch(bin.getMode()) {
                case BLOCK_BOUND -> bin.getTimeLeft();
                case PLAYER_BOUND -> SellingBinData.CLIENT_TIME_LEFT.get(bin.getMaterial().getId());
            };

            if(timeLeft < 0) timeLeft = 0;
            long seconds = (timeLeft / 20) % 60;
            long minutes = (timeLeft / (20 * 60)) % 60;
            long hours = timeLeft / (20 * 60 * 60);
            String text = "";
            if(hours > 0) text += hours + ":";
            text += (minutes <= 9 ? "0" : "") + minutes + ":";
            text += (seconds <= 9 ? "0" : "") + seconds;
            context.drawText(this.textRenderer, text, this.titleX - 16 + this.backgroundWidth - this.textRenderer.getWidth(text), this.titleY, 0x404040, false);
        });
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        context.drawTexture(TEXTURE, i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }

}
