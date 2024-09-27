package bigchadguys.sellingbin.quilt;

import bigchadguys.sellingbin.SellingBinMod;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class SellingBinQuiltMod implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        SellingBinMod.onInitialize();
    }

}
