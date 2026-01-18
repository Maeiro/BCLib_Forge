package org.betterx.bclib.api.v2.dataexchange;

import org.betterx.bclib.BCLib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class BCLibNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final ResourceLocation CHANNEL_ID = BCLib.makeID("dataexchange");
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(CHANNEL_ID)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static boolean initialized;
    private static int nextId;

    private BCLibNetwork() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        CHANNEL.messageBuilder(BCLibPayload.class, nextId++)
               .encoder(BCLibPayload::encode)
               .decoder(BCLibPayload::decode)
               .consumerMainThread(BCLibNetwork::handle)
               .add();
    }

    public static void sendToServer(ResourceLocation id, FriendlyByteBuf buf) {
        CHANNEL.sendToServer(BCLibPayload.from(id, buf));
    }

    public static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), BCLibPayload.from(id, buf));
    }

    private static void handle(BCLibPayload message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context context = ctxSupplier.get();
        handleOnMain(message, context);
        context.setPacketHandled(true);
    }

    private static void handleOnMain(BCLibPayload message, NetworkEvent.Context context) {
        if (context.getDirection().getReceptionSide().isServer()) {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            DataHandlerDescriptor desc = DataExchangeAPI.getDescriptor(message.id());
            if (desc == null) {
                return;
            }
            BaseDataHandler handler = desc.INSTANCE.get();
            handler.receiveFromClient(
                    player.server,
                    player,
                    player.connection,
                    message.toBuffer(),
                    new ServerPacketSender(player)
            );
        } else {
            DataHandlerDescriptor desc = DataExchangeAPI.getDescriptor(message.id());
            if (desc == null) {
                return;
            }
            Minecraft client = Minecraft.getInstance();
            ClientPacketListener listener = client.getConnection();
            BaseDataHandler handler = desc.INSTANCE.get();
            handler.receiveFromServer(client, listener, message.toBuffer(), ClientPacketSender.INSTANCE);
        }
    }

    private static final class ClientPacketSender implements PacketSender {
        private static final ClientPacketSender INSTANCE = new ClientPacketSender();

        @Override
        public void send(ResourceLocation identifier, FriendlyByteBuf buf) {
            sendToServer(identifier, buf);
        }
    }

    private static final class ServerPacketSender implements PacketSender {
        private final ServerPlayer player;

        private ServerPacketSender(ServerPlayer player) {
            this.player = player;
        }

        @Override
        public void send(ResourceLocation identifier, FriendlyByteBuf buf) {
            sendToPlayer(player, identifier, buf);
        }
    }
}
