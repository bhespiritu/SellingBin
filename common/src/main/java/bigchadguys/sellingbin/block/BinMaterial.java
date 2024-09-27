package bigchadguys.sellingbin.block;

import bigchadguys.sellingbin.init.ModConfigs;
import net.minecraft.util.StringIdentifiable;

public enum BinMaterial implements StringIdentifiable {
    WOOD("wood"),
    REDSTONE("redstone"),
    IRON("iron"),
    DIAMOND("diamond"),
    NETHERITE("netherite");

    private final String id;

    BinMaterial(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public BinSettings getSettings() {
        return ModConfigs.SELLING_BIN.getSettings(this);
    }

    public long getUpdateDelayTicks() {
        return this.getSettings().getUpdateDelayTicks();
    }

    public int getRows() {
        return this.getSettings().getInventoryRows();
    }

    @Override
    public String asString() {
        return this.id;
    }

    public static BinMaterial of(String id) {
        for(BinMaterial value : values()) {
            if(value.id.equals(id)) {
                return value;
            }
        }

        return null;
    }

}
