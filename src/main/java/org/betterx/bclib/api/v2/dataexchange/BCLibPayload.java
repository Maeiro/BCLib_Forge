package org.betterx.bclib.api.v2.dataexchange;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import io.netty.buffer.Unpooled;

final class BCLibPayload {
    private final ResourceLocation id;
    private final byte[] data;

    private BCLibPayload(ResourceLocation id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    static BCLibPayload from(ResourceLocation id, FriendlyByteBuf buf) {
        int size = buf.readableBytes();
        byte[] payload = new byte[size];
        buf.getBytes(buf.readerIndex(), payload);
        return new BCLibPayload(id, payload);
    }

    static BCLibPayload decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        int size = buf.readVarInt();
        byte[] payload = new byte[size];
        buf.readBytes(payload);
        return new BCLibPayload(id, payload);
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeVarInt(data.length);
        buf.writeBytes(data);
    }

    ResourceLocation id() {
        return id;
    }

    FriendlyByteBuf toBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
    }
}
