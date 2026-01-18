package org.betterx.bclib.api.v2.dataexchange.handler;

import org.betterx.bclib.api.v2.dataexchange.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = org.betterx.bclib.BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
abstract public class DataExchange {


    private static DataExchangeAPI instance;

    protected static DataExchangeAPI getInstance() {
        if (instance == null) {
            instance = new DataExchangeAPI();
        }
        return instance;
    }

    protected ConnectorServerside server;
    protected ConnectorClientside client;
    protected final Set<DataHandlerDescriptor> descriptors;


    private final boolean didLoadSyncFolder = false;

    abstract protected ConnectorClientside clientSupplier(DataExchange api);

    abstract protected ConnectorServerside serverSupplier(DataExchange api);

    protected DataExchange() {
        descriptors = new HashSet<>();
    }

    public Set<DataHandlerDescriptor> getDescriptors() {
        return descriptors;
    }

    public static DataHandlerDescriptor getDescriptor(ResourceLocation identifier) {
        return getInstance().descriptors.stream().filter(d -> d.equals(identifier)).findFirst().orElse(null);
    }

    @OnlyIn(Dist.CLIENT)
    protected void initClientside() {
        if (client != null) return;
        client = clientSupplier(this);
    }

    protected void initServerSide() {
        if (server != null) return;
        server = serverSupplier(this);
    }

    /**
     * Initializes all datastructures that need to exist in the client component.
     * <p>
     * This is automatically called by BCLib. You can register {@link DataHandler}-Objects before this Method is called
     */
    @OnlyIn(Dist.CLIENT)
    public static void prepareClientside() {
        DataExchange api = DataExchange.getInstance();
        BCLibNetwork.init();
        api.initClientside();

    }

    /**
     * Initializes all datastructures that need to exist in the server component.
     * <p>
     * This is automatically called by BCLib. You can register {@link DataHandler}-Objects before this Method is called
     */
    public static void prepareServerside() {
        DataExchange api = DataExchange.getInstance();
        BCLibNetwork.init();
        api.initServerSide();
    }


    /**
     * Automatically called before the player enters the world.
     * <p>
     * This is automatically called by BCLib. It will send all {@link DataHandler}-Objects that have {@link DataHandlerDescriptor#sendBeforeEnter} set to*
     * {@code true},
     */
    @OnlyIn(Dist.CLIENT)
    public static void sendOnEnter() {
        getInstance().descriptors.forEach((desc) -> {
            if (desc.sendBeforeEnter) {
                BaseDataHandler h = desc.JOIN_INSTANCE.get();
                if (!h.getOriginatesOnServer()) {
                    getInstance().client.sendToServer(h);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        DataExchange api = getInstance();
        api.initServerSide();
        ServerPlayer player = (ServerPlayer) event.getEntity();
        api.server.onPlayInit(player.connection, player.server);
        api.server.onPlayReady(player.connection, player.server);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        DataExchange api = getInstance();
        ServerPlayer player = (ServerPlayer) event.getEntity();
        api.server.onPlayDisconnect(player.connection, player.server);
    }


}
