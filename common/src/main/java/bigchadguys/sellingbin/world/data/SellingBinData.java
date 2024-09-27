package bigchadguys.sellingbin.world.data;

import bigchadguys.sellingbin.SellingBinInventory;
import bigchadguys.sellingbin.block.BinMaterial;
import bigchadguys.sellingbin.data.adapter.Adapters;
import bigchadguys.sellingbin.data.serializable.ISerializable;
import bigchadguys.sellingbin.init.ModConfigs;
import bigchadguys.sellingbin.init.ModNetwork;
import bigchadguys.sellingbin.init.ModWorldData;
import bigchadguys.sellingbin.net.SellingBinEntriesS2CPacket;
import bigchadguys.sellingbin.net.SellingBinTradesS2CPacket;
import bigchadguys.sellingbin.trade.Trade;
import com.google.gson.JsonObject;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class SellingBinData extends WorldData {

    public static List<Trade> CLIENT_TRADES;
    public static Map<String, Integer> CLIENT_TIME_LEFT = new HashMap<>();

    private List<Trade> lastTrades;
    private final Map<UUID, Map<String, Entry>> entries;

    public SellingBinData() {
        this.entries = new HashMap<>();
    }

    public Entry getOrCreate(PlayerEntity player, BinMaterial material) {
        return this.entries.computeIfAbsent(player.getUuid(), unused -> new HashMap<>())
                .computeIfAbsent(material.getId(), unused -> new Entry());
    }

    public void onTick(MinecraftServer server) {
        if(this.lastTrades == null) {
            this.lastTrades = ModConfigs.SELLING_BIN.getTrades();
        }

        if(this.lastTrades != ModConfigs.SELLING_BIN.getTrades()) {
            this.lastTrades = ModConfigs.SELLING_BIN.getTrades();

            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                this.sendTradesToClient(player);
            }
        }

        if(server.getTicks() % 10 == 0) {
            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                this.sendEntriesToClient(player);
            }
        }

        this.entries.forEach((uuid, entries) -> {
            entries.forEach((id, entry) -> {
                BinMaterial material = BinMaterial.of(id);
                if(material == null) return;

                if(entry.getTimeLeft() > material.getUpdateDelayTicks()) {
                    entry.setTimeLeft(material.getUpdateDelayTicks());
                }

                if(entry.getTimeLeft() < 0) {
                    entry.inventory.executeTrades();
                    entry.setTimeLeft(material.getUpdateDelayTicks());
                }

                entry.setTimeLeft(entry.getTimeLeft() - 1);
            });
        });
    }

    public void sendTradesToClient(ServerPlayerEntity player) {
        ModNetwork.CHANNEL.sendToPlayer(player, new SellingBinTradesS2CPacket(ModConfigs.SELLING_BIN.getTrades()));
    }

    public void sendEntriesToClient(ServerPlayerEntity player) {
        Map<String, Integer> timeLeft = new HashMap<>();

        this.entries.getOrDefault(player.getUuid(), new HashMap<>())
                .forEach((key, value) -> timeLeft.put(key, (int)value.getTimeLeft()));

        if(!timeLeft.isEmpty()) {
            ModNetwork.CHANNEL.sendToPlayer(player, new SellingBinEntriesS2CPacket(timeLeft));
        }
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtCompound entriesNbt = new NbtCompound();

        this.entries.forEach((uuid, entries) -> {
            NbtCompound entryNbt = new NbtCompound();

            entries.forEach((id, entry) -> {
                entry.writeNbt().ifPresent(tag -> entryNbt.put(id, tag));
            });

            entriesNbt.put(uuid.toString(), entryNbt);
        });

        nbt.put("entries", entriesNbt);
        return Optional.of(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtCompound entriesNbt = nbt.getCompound("entries");
        this.entries.clear();

        for(String key : entriesNbt.getKeys()) {
            UUID uuid = UUID.fromString(key);
            Map<String, Entry> map = new HashMap<>();

            NbtCompound entryNbt = entriesNbt.getCompound(key);

            for(String key2 : entryNbt.getKeys()) {
                Entry entry = new Entry();
                entry.readNbt(entryNbt.getCompound(key2));
                map.put(key2, entry);
            }

            this.entries.put(uuid, map);
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static void initCommon() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            SellingBinData data = ModWorldData.SELLING_BIN.getGlobal(player.getServerWorld());
            data.sendTradesToClient(player);
            data.sendEntriesToClient(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            SellingBinData data = ModWorldData.SELLING_BIN.getGlobal(server);
            data.onTick(server);
        });
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private final SellingBinInventory inventory;
        private long timeLeft;

        public Entry() {
            this.inventory = new SellingBinInventory(null, 0);
            this.timeLeft = 0L;
        }

        public SellingBinInventory getInventory() {
            return this.inventory;
        }

        public long getTimeLeft() {
            return this.timeLeft;
        }

        public void setTimeLeft(long timeLeft) {
            this.timeLeft = timeLeft;
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                this.inventory.writeNbt().ifPresent(tag -> nbt.put("inventory", tag));
                Adapters.LONG.writeNbt(this.timeLeft).ifPresent(tag -> nbt.put("timeLeft", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.inventory.readNbt(nbt.getCompound("inventory"));
            this.timeLeft = Adapters.LONG.readNbt(nbt.get("timeLeft")).orElse(0L);
        }
    }

}
