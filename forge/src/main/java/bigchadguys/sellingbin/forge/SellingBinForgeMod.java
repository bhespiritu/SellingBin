package bigchadguys.sellingbin.forge;

import dev.architectury.platform.forge.EventBuses;
import bigchadguys.sellingbin.SellingBinMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SellingBinMod.ID)
public class SellingBinForgeMod {

    public SellingBinForgeMod() {
        EventBuses.registerModEventBus(SellingBinMod.ID, FMLJavaModLoadingContext.get().getModEventBus());
        SellingBinMod.onInitialize();
    }

}
