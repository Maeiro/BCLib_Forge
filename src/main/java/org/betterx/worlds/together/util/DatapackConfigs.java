package org.betterx.worlds.together.util;

import org.betterx.bclib.BCLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collection;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DatapackConfigs {
    @FunctionalInterface
    public interface DatapackConfigReloadHandler {
        void onReload(ResourceLocation id, JsonObject root);
    }

    @FunctionalInterface
    public interface DatapackConfigReloadPrepare {
        void onReload();
    }

    private static final DatapackConfigs INSTANCE = new DatapackConfigs();
    private static final ReloadListener RELOAD_LISTENER = new ReloadListener();
    private static boolean reloadListenerRegistered = false;

    public static DatapackConfigs instance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, HandlerEntry> handlers = new HashMap<>();

    public void register(
            String modID,
            String fileName,
            DatapackConfigReloadHandler handler
    ) {
        register(
                modID, fileName, () -> {
                    //nothing to do
                }, handler
        );
    }

    public void register(
            String modID,
            String fileName,
            DatapackConfigReloadPrepare prepare,
            DatapackConfigReloadHandler handler
    ) {
        final ResourceLocation handlerID = new ResourceLocation(
                modID,
                "config_manager_" + fileName.replaceAll("/", "_").replaceAll(".", "_")
        );
        handlers.put(handlerID, new HandlerEntry(modID, fileName, prepare, handler));
    }

    public void runForResources(
            ResourceManager manager,
            String modID,
            String fileName,
            DatapackConfigReloadHandler handler
    ) {
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources(
                "config",
                id -> id.getNamespace().equals(modID) &&
                        id.getPath().endsWith(fileName)
        ).entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                final JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                if (obj != null)
                    handler.onReload(entry.getKey(), obj);
            } catch (Exception e) {
                BCLib.LOGGER.error(
                        "Error occurred while loading resource json " + entry.getKey(),
                        e
                );
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        if (reloadListenerRegistered) {
            return;
        }
        reloadListenerRegistered = true;
        event.addListener(RELOAD_LISTENER);
    }

    private static final class HandlerEntry {
        private final String modID;
        private final String fileName;
        private final DatapackConfigReloadPrepare prepare;
        private final DatapackConfigReloadHandler handler;

        private HandlerEntry(
                String modID,
                String fileName,
                DatapackConfigReloadPrepare prepare,
                DatapackConfigReloadHandler handler
        ) {
            this.modID = modID;
            this.fileName = fileName;
            this.prepare = prepare;
            this.handler = handler;
        }
    }

    private static final class ReloadListener extends SimplePreparableReloadListener<Void> {
        @Override
        protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            return null;
        }

        @Override
        protected void apply(Void payload, ResourceManager resourceManager, ProfilerFiller profiler) {
            Collection<HandlerEntry> entries = DatapackConfigs.instance().handlers.values();
            for (HandlerEntry entry : entries) {
                entry.prepare.onReload();
                DatapackConfigs.instance().runForResources(resourceManager, entry.modID, entry.fileName, entry.handler);
            }
        }
    }
}
