package org.betterx.bclib.api.v2.dataexchange.handler;

import org.betterx.bclib.api.v2.dataexchange.ConnectorClientside;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = org.betterx.bclib.BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DataExchangeClientEvents {
    private static ClientPacketListener lastClientConnection;

    private DataExchangeClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        if (connection == lastClientConnection) {
            return;
        }
        DataExchange api = DataExchange.getInstance();
        if (connection != null) {
            api.initClientside();
            ConnectorClientside connector = (ConnectorClientside) api.client;
            connector.onPlayInit(connection, client);
            connector.onPlayReady(connection, client);
        } else if (lastClientConnection != null) {
            ConnectorClientside connector = (ConnectorClientside) api.client;
            if (connector != null) {
                connector.onPlayDisconnect(lastClientConnection, client);
            }
        }
        lastClientConnection = connection;
    }
}
