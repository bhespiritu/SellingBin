package bigchadguys.sellingbin.mixin;

import bigchadguys.sellingbin.block.entity.BaseBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onBlockEntityUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
        ClientWorld world = this.client.world;
        if(world == null) return;
        BlockEntity entity = world.getBlockEntity(packet.getPos(), packet.getBlockEntityType()).orElse(null);

        if(entity instanceof BaseBlockEntity baseEntity) {
            NbtCompound nbt = packet.getNbt() != null ? packet.getNbt() : new NbtCompound();
            baseEntity.readNbt(nbt, BaseBlockEntity.UpdateType.UPDATE_PACKET);
            ci.cancel();
        }
    }

}
