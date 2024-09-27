package bigchadguys.sellingbin.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public abstract class Command {

    public abstract void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment);

}
