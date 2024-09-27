package bigchadguys.sellingbin.config;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.entity.EntityPredicate;
import bigchadguys.sellingbin.data.item.ItemPredicate;
import bigchadguys.sellingbin.data.tile.TilePredicate;
import bigchadguys.sellingbin.trade.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Config {

    protected static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().setLenient()
            .registerTypeHierarchyAdapter(Trade.class, Adapters.TRADE)
            .registerTypeHierarchyAdapter(TilePredicate.class, Adapters.TILE_PREDICATE)
            .registerTypeHierarchyAdapter(EntityPredicate.class, Adapters.ENTITY_PREDICATE)
            .registerTypeHierarchyAdapter(ItemPredicate.class, Adapters.ITEM_PREDICATE)
            .create();

    public abstract void write() throws IOException;

    public abstract  <C extends Config> C read() throws IOException;

    protected abstract void reset();

    protected final void writeFile(Path path, Object file) throws IOException {
        Files.createDirectories(path.getParent());
        FileWriter writer = new FileWriter(path.toFile());
        GSON.toJson(file, writer);
        writer.flush();
        writer.close();
    }

    protected final <C> C readFile(Path path, Type type) throws FileNotFoundException {
        return GSON.fromJson(new FileReader(path.toFile()), type);
    }

}
