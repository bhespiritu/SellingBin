package bigchadguys.sellingbin.init;

import bigchadguys.sellingbin.SellingBinMod;
import bigchadguys.sellingbin.net.SellingBinEntriesS2CPacket;
import bigchadguys.sellingbin.net.ModPacket;
import bigchadguys.sellingbin.net.SellingBinTradesS2CPacket;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModNetwork extends ModRegistries {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(SellingBinMod.id("network"));

    public static void register() {
        if(Platform.getEnvironment() == Env.CLIENT) {
            Client.register();
        } else {
            Server.register();
        }
    }

    public static class Client {
        public static final Function<NetworkManager.PacketContext, ClientPlayNetworkHandler> CLIENT_PLAY = context -> MinecraftClient.getInstance().getNetworkHandler();
        public static final Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> SERVER_PLAY = context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;

        public static void register() {
            ModNetwork.register(SellingBinTradesS2CPacket.class, SellingBinTradesS2CPacket::new, CLIENT_PLAY);
            ModNetwork.register(SellingBinEntriesS2CPacket.class, SellingBinEntriesS2CPacket::new, CLIENT_PLAY);
        }
    }

    public static class Server {
        public static final Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> SERVER_PLAY = context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;

        public static void register() {
            ModNetwork.register(SellingBinTradesS2CPacket.class, SellingBinTradesS2CPacket::new, null);
            ModNetwork.register(SellingBinEntriesS2CPacket.class, SellingBinEntriesS2CPacket::new, null);
        }
    }

    public static <R extends PacketListener, T extends ModPacket<R>> void register(Class<T> type, Supplier<T> packetSupplier,
                                                                                   Function<NetworkManager.PacketContext, R> contextMapper) {
        CHANNEL.register(type, ModPacket::write, packetByteBuf -> {
            T packet = packetSupplier.get();
            packet.read(packetByteBuf);
            return packet;
        }, (packet, contextSupplier) -> {
            if(contextMapper != null) {
                packet.apply(contextMapper.apply(contextSupplier.get()));
            }
        });
    }

}
