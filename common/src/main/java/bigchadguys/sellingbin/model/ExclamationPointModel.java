package bigchadguys.sellingbin.model;

import bigchadguys.sellingbin.SellingBinMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ExclamationPointModel extends Model {

    public static final Identifier TEXTURE = SellingBinMod.id("textures/model/exclamation_point.png");

    private final ModelPart root;

    public ExclamationPointModel() {
        super(RenderLayer::getEntityTranslucent);
        this.root = createMesh().createModel();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public static TexturedModelData createMesh() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        root.addChild("alert", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-1.0F, -19.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -12.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(data, 16, 16);
    }

}
