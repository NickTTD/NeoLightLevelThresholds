package com.nickttd.neolightlevels;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event) {
        if (event.phase != RenderTickEvent.Phase.END) return;
        // No-op: all custom heart rendering logic removed
    }
}
