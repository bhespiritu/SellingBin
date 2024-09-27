package bigchadguys.sellingbin.net;

import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import bigchadguys.sellingbin.world.data.SellingBinData;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.HashMap;
import java.util.Map;

public class SellingBinEntriesS2CPacket extends ModPacket<ClientPlayNetworkHandler> {

    private final Map<String, Integer> timeLeft;

    public SellingBinEntriesS2CPacket() {
        this.timeLeft = new HashMap<>();
    }

    public SellingBinEntriesS2CPacket(Map<String, Integer> timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public void onReceive(ClientPlayNetworkHandler listener) {
        SellingBinData.CLIENT_TIME_LEFT = this.timeLeft;
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(this.timeLeft.size(), buffer);

        this.timeLeft.forEach((id, time) -> {
            Adapters.UTF_8.writeBits(id, buffer);
            Adapters.INT.writeBits(time, buffer);
        });
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.timeLeft.clear();
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
           this.timeLeft.put(
               Adapters.UTF_8.readBits(buffer).orElseThrow(),
               Adapters.INT.readBits(buffer).orElseThrow()
           );
        }
    }

}
