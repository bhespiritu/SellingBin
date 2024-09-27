package bigchadguys.sellingbin.render;

import bigchadguys.sellingbin.SellingBinMod;
import bigchadguys.sellingbin.init.ModBlocks;
import bigchadguys.sellingbin.trade.Trade;
import bigchadguys.sellingbin.world.data.SellingBinData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class SellingBinTooltipComponent implements TooltipComponent {

    private static final Identifier TEXTURE = SellingBinMod.id("textures/gui/selling_bin.png");
    public static final ThreadLocal<ItemStack> STACK = new ThreadLocal<>();

    private final ItemStack stack;
    private Trade trade;

    public SellingBinTooltipComponent(ItemStack stack) {
        this.stack = stack;

        for(Trade trade : SellingBinData.CLIENT_TRADES) {
           if(trade.getInput().getFilter().test(stack)) {
               this.trade = trade;
               break;
           }
        }
    }

    public boolean isActive() {
        return this.trade != null;
    }

    @Override
    public int getHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight + 17 + 8;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        ItemStack bin = new ItemStack(ModBlocks.SELLING_BIN.get());
        MutableText prefix = Text.empty().append(bin.getName()).formatted(Formatting.GRAY);
        int textWidth = textRenderer.getWidth(prefix);
        int graphicWidth = 31 + 16;
        return Math.max(graphicWidth, textWidth) + 6;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {

    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        x += 3;
        y += 3;

        ItemStack bin = new ItemStack(ModBlocks.SELLING_BIN.get());
        MutableText prefix = Text.empty().append(bin.getName()).setStyle(Style.EMPTY.withColor(0xFFD3D3D3));
        int textWidth = textRenderer.getWidth(prefix);
        int textHeight = textRenderer.fontHeight;
        int graphicWidth = 31 + 16;

        int offset = (textWidth - graphicWidth) / 2;
        int textOffset = offset < 0 ? -offset : 0;
        int graphicOffset = offset >= 0 ? offset : 0;

        this.draw(context, x - 3.5F, y - 3.5F, Math.max(graphicWidth, textWidth) + 6, textHeight + 17 + 6, 0xFF000000);
        this.drawOutline(x - 3.5F, y - 3.5F, Math.max(graphicWidth, textWidth) + 6, textHeight + 17 + 6, 0xFFD3D3D3, context);
        context.drawText(textRenderer, prefix, x + textOffset, y, 0xFFFFFF, false);

        ItemStack input = this.stack.copy();
        input.setCount(this.trade.getInput().getCount());
        context.drawItemWithoutEntity(input, x + graphicOffset, y + textHeight);
        context.drawItemInSlot(textRenderer, input, x + graphicOffset, y + textHeight);

        ItemStack output = this.trade.getOutput().generate().orElse(ItemStack.EMPTY);
        context.drawItemWithoutEntity(output, x + graphicOffset + 31, y + textHeight);
        context.drawItemInSlot(textRenderer, output, x + graphicOffset + 31, y + textHeight);
        context.drawTexture(TEXTURE, x + graphicOffset + 19, y + 3 + textHeight, 0, 15.0f, 171.0f, 10, 9, 512, 256);
    }

    private void drawOutline(float x, float y, float width, float height, int color, DrawContext context) {
        this.draw(context, x, y, width, 0.5F, color);
        this.draw(context, x, y + height, width + 0.5F, 0.5F, color);
        this.draw(context, x, y, 0.5F, height, color);
        this.draw(context, x + width, y, 0.5F, height, color);
    }

    private void draw(DrawContext context, float x, float y, float width, float height, int color) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, x, y, 0).color(color).next();
        bufferBuilder.vertex(matrix4f, x, y + height, 0).color(color).next();
        bufferBuilder.vertex(matrix4f, x + width, y + height, 0).color(color).next();
        bufferBuilder.vertex(matrix4f, x + width, y, 0).color(color).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    @Environment(value=EnvType.CLIENT)
    enum Sprite {
        SLOT(0, 0, 17, 20),
        BLOCKED_SLOT(0, 40, 17, 20),
        BORDER_VERTICAL(0, 18, 1, 20),
        BORDER_HORIZONTAL_TOP(1, 20, 17, 1),
        BORDER_HORIZONTAL_BOTTOM(1, 60, 17, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int u;
        public final int v;
        public final int width;
        public final int height;

        Sprite(int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }
    }

}
