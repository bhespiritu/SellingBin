package bigchadguys.sellingbin.init;

import bigchadguys.sellingbin.command.Command;
import bigchadguys.sellingbin.command.ReloadCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Supplier;

public class ModCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        new ReloadCommand().register(dispatcher, access, environment);
    }

    private static <T extends Command> T register(Supplier<T> supplier, CommandDispatcher<ServerCommandSource> dispatcher,
                                                  CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        T command = supplier.get();
        command.register(dispatcher, access, environment);
        return command;
    }

}
