package bigchadguys.sellingbin.mixin;

import bigchadguys.sellingbin.init.ModCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class MixinCommandManager {

    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) {
        ModCommands.register(this.dispatcher, commandRegistryAccess, environment);
    }

}
