package bigchadguys.sellingbin.init;

import bigchadguys.sellingbin.world.data.SellingBinData;
import bigchadguys.sellingbin.world.data.WorldDataType;

public class ModWorldData extends ModRegistries {

    public static WorldDataType<SellingBinData> SELLING_BIN;

    public static void register() {
        SELLING_BIN = new WorldDataType<>("sellingbin.selling_bin", SellingBinData::new);
        SellingBinData.initCommon();
    }

}
