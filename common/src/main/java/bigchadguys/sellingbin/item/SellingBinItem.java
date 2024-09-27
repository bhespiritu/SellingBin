package bigchadguys.sellingbin.item;

import bigchadguys.sellingbin.block.BinMaterial;
import bigchadguys.sellingbin.block.BinMode;
import bigchadguys.sellingbin.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Optional;

public class SellingBinItem extends BlockItem {

    public SellingBinItem(Block block) {
        super(block, new Settings());
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        BinMaterial material = SellingBinItem.getMaterial(stack).orElse(null);
        return super.getTranslationKey(stack) + (material == null ? "" : "." + material.getId());
    }

    public static ItemStack create(BinMaterial material, BinMode mode) {
        ItemStack stack = new ItemStack(ModBlocks.SELLING_BIN.get());
        SellingBinItem.setMaterial(stack, material);
        SellingBinItem.setMode(stack, mode);
        return stack;
    }

    public static Optional<BinMaterial> getMaterial(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null) return Optional.empty();
        String id = nbt.getString("material");

        for(BinMaterial value : BinMaterial.values()) {
            if(value.getId().equals(id)) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    public static void setMaterial(ItemStack stack, BinMaterial material) {
        if(material == null) {
            if(stack.getNbt() != null) {
                stack.getNbt().remove("material");
            }
        } else {
            stack.getOrCreateNbt().putString("material", material.getId());
        }
    }

    public static Optional<BinMode> getMode(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null) return Optional.empty();
        String id = nbt.getString("mode");

        for(BinMode value : BinMode.values()) {
            if(value.getId().equals(id)) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    public static void setMode(ItemStack stack, BinMode mode) {
        if(mode == null) {
            if(stack.getNbt() != null) {
                stack.getNbt().remove("mode");
            }
        } else {
            stack.getOrCreateNbt().putString("mode", mode.getId());
        }
    }

}
