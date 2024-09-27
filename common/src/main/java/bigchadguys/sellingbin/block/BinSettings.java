package bigchadguys.sellingbin.block;

import com.google.gson.annotations.Expose;

public class BinSettings {

    @Expose private long updateDelayTicks;
    @Expose private int inventoryRows;

    public BinSettings(long updateDelayTicks, int inventoryRows) {
        this.updateDelayTicks = updateDelayTicks;
        this.inventoryRows = inventoryRows;
    }

    public long getUpdateDelayTicks() {
        return this.updateDelayTicks;
    }

    public int getInventoryRows() {
        return this.inventoryRows;
    }

}
