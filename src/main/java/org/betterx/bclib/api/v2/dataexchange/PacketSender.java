package org.betterx.bclib.api.v2.dataexchange;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface PacketSender {
    void send(ResourceLocation identifier, FriendlyByteBuf buf);
}
