package bigchadguys.sellingbin.config;

import bigchadguys.sellingbin.block.BinMaterial;
import bigchadguys.sellingbin.block.BinSettings;
import bigchadguys.sellingbin.data.item.ItemPredicate;
import bigchadguys.sellingbin.data.item.PartialStack;
import bigchadguys.sellingbin.init.ModConfigs;
import bigchadguys.sellingbin.trade.Trade;
import com.google.gson.annotations.Expose;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

public class SellingBinConfig extends FileConfig {

    @Expose private Map<BinMaterial, BinSettings> settings;
    @Expose private List<Trade> trades;

    public void validate(String path) {
        for(int i = 0; i < this.trades.size(); i++) {
            Trade trade = this.trades.get(i);
            trade.validate(path + ".trades[" + i + "]");
        }
    }

    public BinSettings getSettings(BinMaterial material) {
        return this.settings.get(material);
    }

    public List<Trade> getTrades() {
        return this.trades;
    }

    @Override
    public String getPath() {
        return "selling_bin";
    }

    @Override
    protected void reset() {
        this.settings = new LinkedHashMap<>();
        this.settings.put(BinMaterial.WOOD, new BinSettings(20 * 30, 1));
        this.settings.put(BinMaterial.REDSTONE, new BinSettings(20 * 30, 2));
        this.settings.put(BinMaterial.IRON, new BinSettings(20 * 30, 2));
        this.settings.put(BinMaterial.DIAMOND, new BinSettings(20 * 30, 3));
        this.settings.put(BinMaterial.NETHERITE, new BinSettings(20 * 30, 6));

        this.trades = new ArrayList<>();

        this.trades.add(new Trade(new Trade.Input(
            ItemPredicate.of("minecraft:carrot", true).orElseThrow(), 4
        ), PartialStack.of(new ItemStack(Items.EMERALD))));

        this.trades.add(new Trade(new Trade.Input(
            ItemPredicate.of("minecraft:potato", true).orElseThrow(), 4
        ), PartialStack.of(new ItemStack(Items.IRON_INGOT))));
    }

    @Override
    public <T extends Config> T read() {
        T config = super.read();
        ModConfigs.POST_LOAD.add(() -> ((SellingBinConfig)config).validate(this.getPath()));
        return config;
    }

}
