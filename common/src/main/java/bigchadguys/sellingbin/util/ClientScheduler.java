package bigchadguys.sellingbin.util;

import dev.architectury.event.events.client.ClientTickEvent;

public class ClientScheduler {

    public static long TICK = 0L;

    static {
        ClientTickEvent.CLIENT_PRE.register(instance -> {
            TICK++;
        });
    }

    public static long getTick() {
        return TICK;
    }

    public static double getTick(double delta) {
        return TICK + delta;
    }

}
