package bigchadguys.sellingbin.init;

import bigchadguys.sellingbin.screen.SellingBinScreen;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.menu.MenuRegistry;

public class ModScreens extends ModRegistries {

    public static void register() {
        ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
            MenuRegistry.registerScreenFactory(ModScreenHandlers.SELLING_BIN.get(), SellingBinScreen::new);
        });
    }

}
