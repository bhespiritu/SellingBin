package bigchadguys.sellingbin.net;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.adapter.IAdapter;
import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import bigchadguys.sellingbin.data.serializable.ISerializable;
import bigchadguys.sellingbin.trade.Trade;
import bigchadguys.sellingbin.world.data.SellingBinData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellingBinTradesS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    private final List<Trade> trades;

    public SellingBinTradesS2CPacket() {
        this.trades = new ArrayList<>();
    }

    public SellingBinTradesS2CPacket(List<Trade> trades) {
        this.trades = trades;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        SellingBinData.CLIENT_TRADES = this.trades;
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_7.writeBits(this.trades.size(), buffer);

        this.trades.forEach(trade -> {
            JsonElement json = Adapters.TRADE.writeJson(trade).orElseThrow();
            Adapters.UTF_8.writeBits(json.toString(), buffer);
        });
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.trades.clear();
        int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            JsonElement json = JsonParser.parseString(Adapters.UTF_8.readBits(buffer).orElseThrow());
            this.trades.add(((ISimpleAdapter<Trade, ?, JsonElement>)Adapters.TRADE).readJson(json).orElseThrow());
        }
    }

}
