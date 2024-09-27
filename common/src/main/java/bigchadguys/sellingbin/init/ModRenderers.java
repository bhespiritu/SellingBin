package bigchadguys.sellingbin.init;

import bigchadguys.sellingbin.block.BinMaterial;
import bigchadguys.sellingbin.block.entity.SellingBinBlockEntity;
import bigchadguys.sellingbin.block.entity.renderer.SellingBinBlockEntityRenderer;
import bigchadguys.sellingbin.item.SellingBinItem;
import bigchadguys.sellingbin.mixin.ProxyModelPredicateProviderRegistry;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModRenderers {

    public static class BlockEntities extends ModRenderers {
        public static BlockEntityRendererFactory<SellingBinBlockEntity> SELLING_BIN;

        public static void register(Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> registry) {
            try {
                ProxyModelPredicateProviderRegistry.register(ModBlocks.SELLING_BIN.get().asItem(), new Identifier("material"), (stack, world, entity, seed) -> {
                    return (float)SellingBinItem.getMaterial(stack).orElse(BinMaterial.WOOD).ordinal() / 10.0F;
                });

                SELLING_BIN = register(registry, ModBlocks.Entities.SELLING_BIN.get(), SellingBinBlockEntityRenderer::new);
            } catch(Exception e) {
                ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
                    ProxyModelPredicateProviderRegistry.register(ModBlocks.SELLING_BIN.get().asItem(), new Identifier("material"), (stack, world, entity, seed) -> {
                        return (float)SellingBinItem.getMaterial(stack).orElse(BinMaterial.WOOD).ordinal() / 10.0F;
                    });

                    SELLING_BIN = register(registry, ModBlocks.Entities.SELLING_BIN.get(), SellingBinBlockEntityRenderer::new);
                });
            }
        }
    }

    public static <T extends BlockEntity> BlockEntityRendererFactory<T> register(
            Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> registry,
            BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> renderer) {
        registry.put(type, renderer);
        return renderer;
    }

}
