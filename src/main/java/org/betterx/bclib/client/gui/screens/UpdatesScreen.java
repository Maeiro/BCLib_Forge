package org.betterx.bclib.client.gui.screens;

import de.ambertation.wunderlib.ui.ColorHelper;
import de.ambertation.wunderlib.ui.layout.components.HorizontalStack;
import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wunderlib.ui.layout.components.VerticalStack;
import de.ambertation.wunderlib.ui.layout.values.Size;
import de.ambertation.wunderlib.ui.layout.values.Value;
import org.betterx.bclib.BCLib;
import org.betterx.bclib.config.Configs;
import org.betterx.bclib.networking.VersionChecker;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class UpdatesScreen extends BCLibLayoutScreen {
    public static final String DONATION_URL = "https://www.buymeacoffee.com/quiqueck";
    static final ResourceLocation UPDATE_LOGO_LOCATION = new ResourceLocation(BCLib.MOD_ID, "icon_updater.png");

    public UpdatesScreen(Screen parent) {
        super(parent, Component.translatable("bclib.updates.title"), 10, 10, 10);
    }
    
    public static void showUpdateUI() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> Minecraft.getInstance()
                                                         .setScreen(new UpdatesScreen(Minecraft.getInstance().screen)));
        } else {
            Minecraft.getInstance().setScreen(new UpdatesScreen(Minecraft.getInstance().screen));
        }
    }

    public ResourceLocation getUpdaterIcon(String modID) {
        if (modID.equals(BCLib.MOD_ID)) {
            return UPDATE_LOGO_LOCATION;
        }
        IModInfo info = getModInfo(modID);
        Map<String, Object> props = getBclibProperties(info);
        if (props != null) {
            Object icon = props.get("updater_icon");
            if (icon instanceof String iconPath) {
                return new ResourceLocation(modID, iconPath);
            }
        }
        return null;
    }

    private static IModInfo getModInfo(String modID) {
        return ModList.get()
                      .getModContainerById(modID)
                      .map(ModContainer::getModInfo)
                      .orElse(null);
    }

    private static Map<String, Object> getBclibProperties(IModInfo info) {
        if (info == null) {
            return null;
        }
        return asStringMap(info.getModProperties().get("bclib"));
    }

    private static Map<String, Object> asStringMap(Object obj) {
        if (!(obj instanceof Map<?, ?> map)) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        map.forEach((key, value) -> {
            if (key instanceof String) {
                result.put((String) key, value);
            }
        });
        return result;
    }

    private record DownloadLink(String url, Component label) {
    }

    private static DownloadLink getDownloadLink(IModInfo info) {
        if (info == null) {
            return null;
        }
        Map<String, Object> props = getBclibProperties(info);
        Map<String, Object> downloads = props == null ? null : asStringMap(props.get("downloads"));
        if (downloads != null) {
            if (Configs.CLIENT_CONFIG.preferModrinthForUpdates()) {
                Object link = downloads.get("modrinth");
                if (link instanceof String url) {
                    return new DownloadLink(url, Component.translatable("bclib.updates.modrinth_link"));
                }
            }
            Object link = downloads.get("curseforge");
            if (link instanceof String url) {
                return new DownloadLink(url, Component.translatable("bclib.updates.curseforge_link"));
            }
        }

        Optional<URL> url = info.getModURL();
        return url.map(value -> new DownloadLink(value.toString(), Component.translatable("bclib.updates.download_link")))
                  .orElse(null);
    }

    @Override
    protected LayoutComponent<?, ?> initContent() {
        VerticalStack rows = new VerticalStack(relative(1), fit()).centerHorizontal();
        rows.addMultilineText(fill(), fit(), Component.translatable("bclib.updates.description"))
            .centerHorizontal();

        rows.addSpacer(8);

        VersionChecker.forEachUpdate((mod, cur, updated) -> {
            ModContainer nfo = ModList.get().getModContainerById(mod).orElse(null);
            IModInfo modInfo = nfo == null ? null : nfo.getModInfo();
            ResourceLocation icon = getUpdaterIcon(mod);
            HorizontalStack row = rows.addRow(fixed(320), fit()).centerHorizontal();
            if (icon != null) {
                row.addImage(Value.fit(), Value.fit(), icon, Size.of(32));
                row.addSpacer(4);
            } else {
                row.addSpacer(36);
            }
            if (modInfo != null) {
                row.addText(fit(), fit(), Component.literal(modInfo.getDisplayName()))
                   .setColor(ColorHelper.WHITE);
            } else {
                row.addText(fit(), fit(), Component.literal(mod)).setColor(ColorHelper.WHITE);
            }
            row.addSpacer(4);
            row.addText(fit(), fit(), Component.literal(cur));
            row.addText(fit(), fit(), Component.literal(" -> "));
            row.addText(fit(), fit(), Component.literal(updated)).setColor(ColorHelper.GREEN);
            row.addFiller();
            DownloadLink downloadLink = getDownloadLink(modInfo);
            if (downloadLink != null) {
                row.addButton(fit(), fit(), downloadLink.label())
                   .onPress((bt) -> {
                       this.openLink(downloadLink.url());
                   }).centerVertical();
            }
        });

        VerticalStack layout = new VerticalStack(relative(1), fill()).centerHorizontal();
        //layout.addSpacer(8);
        layout.addScrollable(rows);
        layout.addSpacer(8);


        HorizontalStack footer = layout.addRow(fill(), fit());
        if (Configs.CLIENT_CONFIG.isDonor()) {
            footer.addButton(
                          fit(),
                          fit(),
                          Component.translatable("bclib.updates.donate").setStyle(Style.EMPTY.withColor(ColorHelper.YELLOW))
                  )
                  .onPress((bt) -> openLink(DONATION_URL));
            footer.addSpacer(2);
            footer.addMultilineText(fit(), fit(), Component.translatable("bclib.updates.donate_pre"))
                  .alignBottom();
        }

        footer.addFiller();
        footer.addCheckbox(
                      fit(), fit(),
                      Component.translatable("Disable Check"),
                      !Configs.CLIENT_CONFIG.checkVersions()
              )
              .onChange((cb, state) -> {
                  Configs.CLIENT_CONFIG.setCheckVersions(!state);
                  Configs.CLIENT_CONFIG.saveChanges();
              });
        footer.addSpacer(4);
        footer.addButton(fit(), fit(), CommonComponents.GUI_DONE).onPress((bt -> {
            onClose();
        }));
        return layout;
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fill(0, 0, width, height, 0xBD343444);
    }
}
