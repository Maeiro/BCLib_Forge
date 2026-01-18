package org.betterx.worlds.together.util;

import org.betterx.worlds.together.WorldsTogether;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModUtil {
    private static Map<String, ModInfo> mods;

    /**
     * Unloads the cache of available mods created from {@link #getMods()}
     */
    public static void invalidateCachedMods() {
        mods = null;
    }

    /**
     * return a map of all mods that were found in the 'mods'-folder.
     * <p>
     * The method will cache the results. You can clear that cache (and free the memory) by
     * calling {@link #invalidateCachedMods()}
     * <p>
     * An error message is printed if a mod fails to load, but the parsing will continue.
     *
     * @return A map of all found mods. (key=ModID, value={@link ModInfo})
     */
    public static Map<String, ModInfo> getMods() {
        if (mods != null) return mods;

        mods = new HashMap<>();
        ModList.get()
               .getMods()
               .forEach(info -> {
                   ModInfo modInfo = new ModInfo(info);
                   mods.put(modInfo.getId(), modInfo);
               });

        return mods;
    }

    /**
     * Returns the {@link ModInfo} or {@code null} if the mod was not found.
     * <p>
     * The call will also return null if the mode-Version in the jar-File is not the same
     * as the version of the loaded Mod.
     *
     * @param modID The mod ID to query
     * @return A {@link ModInfo}-Object for the querried Mod.
     */
    public static ModInfo getModInfo(String modID) {
        return getModInfo(modID, true);
    }

    public static ModInfo getModInfo(String modID, boolean matchVersion) {
        getMods();
        final ModInfo mi = mods.get(modID);
        if (mi == null || (matchVersion && !getModVersion(modID).equals(mi.getVersion()))) return null;
        return mi;
    }

    /**
     * Local Mod Version for the queried Mod
     *
     * @param modID The mod ID to query
     * @return The version of the locally installed Mod
     */
    public static String getModVersion(String modID) {
        Optional<? extends ModContainer> optional = ModList.get()
                                                           .getModContainerById(modID);
        if (optional.isPresent()) {
            ModContainer modContainer = optional.get();
            return ModInfo.versionToString(modContainer.getModInfo()
                                                       .getVersion());
        }

        return getModVersionFromJar(modID);
    }

    /**
     * Local Mod Version for the queried Mod from the Jar-File in the games mod-directory
     *
     * @param modID The mod ID to query
     * @return The version of the locally installed Mod
     */
    public static String getModVersionFromJar(String modID) {
        final ModInfo mi = getModInfo(modID, false);
        if (mi != null) return mi.getVersion();

        return "0.0.0";
    }

    /**
     * Get mod version from string. String should be in format: %d.%d.%d
     *
     * @param version - {@link String} mod version.
     * @return int mod version.
     */
    public static int convertModVersion(String version) {
        if (version.isEmpty()) {
            return 0;
        }
        try {
            int res = 0;
            final String semanticVersionPattern = "(\\d+)\\.(\\d+)(\\.(\\d+))?\\D*";
            final Matcher matcher = Pattern.compile(semanticVersionPattern)
                                           .matcher(version);
            if (matcher.find()) {
                if (matcher.groupCount() > 0)
                    res = matcher.group(1) == null ? 0 : ((Integer.parseInt(matcher.group(1)) & 0xFF) << 22);
                if (matcher.groupCount() > 1)
                    res |= matcher.group(2) == null ? 0 : ((Integer.parseInt(matcher.group(2)) & 0xFF) << 14);
                if (matcher.groupCount() > 3)
                    res |= matcher.group(4) == null ? 0 : Integer.parseInt(matcher.group(4)) & 0x3FFF;
            }

            return res;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get mod version from integer. String will be in format %d.%d.%d
     *
     * @param version - mod version in integer form.
     * @return {@link String} mod version.
     */
    public static String convertModVersion(int version) {
        int a = (version >> 22) & 0xFF;
        int b = (version >> 14) & 0xFF;
        int c = version & 0x3FFF;
        return String.format(Locale.ROOT, "%d.%d.%d", a, b, c);
    }

    /**
     * {@code true} if the version v1 is larger than v2
     *
     * @param v1 A Version string
     * @param v2 Another Version string
     * @return v1 &gt; v2
     */
    public static boolean isLargerVersion(String v1, String v2) {
        return convertModVersion(v1) > convertModVersion(v2);
    }

    /**
     * {@code true} if the version v1 is larger or equal v2
     *
     * @param v1 A Version string
     * @param v2 Another Version string
     * @return v1 &ge; v2
     */
    public static boolean isLargerOrEqualVersion(String v1, String v2) {
        return convertModVersion(v1) >= convertModVersion(v2);
    }

    public static class ModInfo {
        public final IModInfo metadata;
        public final Path jarPath;
        private final ModEnvironment environment;

        ModInfo(IModInfo metadata) {
            this.metadata = metadata;
            this.environment = detectEnvironment(metadata);
            Path modPath = null;
            try {
                modPath = metadata.getOwningFile().getFile().getFilePath();
            } catch (Exception e) {
                WorldsTogether.LOGGER.warning("Unable to resolve mod file for " + metadata.getModId() + ": " + e.getMessage());
            }
            this.jarPath = modPath;
        }

        public ModEnvironment getEnvironment() {
            return environment;
        }

        public String getId() {
            return metadata.getModId();
        }

        public String getName() {
            return metadata.getDisplayName();
        }

        public String getVersion() {
            return versionToString(metadata.getVersion());
        }

        public static String versionToString(ArtifactVersion v) {
            if (v == null) {
                return "0.0.0";
            }
            return v.toString();
        }

        @Override
        public String toString() {
            return "ModInfo{" + "id=" + metadata.getModId() + ", version=" + metadata.getVersion() + ", jarPath=" + jarPath + '}';
        }
    }

    private static ModEnvironment detectEnvironment(IModInfo metadata) {
        Object env = metadata.getModProperties().get("environment");
        if (env instanceof String) {
            String value = ((String) env).trim().toLowerCase(Locale.ROOT);
            if (value.equals("client")) {
                return ModEnvironment.CLIENT;
            }
            if (value.equals("server")) {
                return ModEnvironment.SERVER;
            }
            if (value.isEmpty() || value.equals("*") || value.equals("both") || value.equals("common") || value.equals("universal")) {
                return ModEnvironment.UNIVERSAL;
            }
            WorldsTogether.LOGGER.warning("Unknown environment '" + env + "' for mod " + metadata.getModId());
        }
        return ModEnvironment.UNIVERSAL;
    }
}
