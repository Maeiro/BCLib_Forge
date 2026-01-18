package org.betterx.worlds.together.entrypoints;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EntrypointUtil {
    private static <T extends WorldsTogetherEntrypoint> List<T> getEntryPoints(boolean client, Class<T> select) {
        return List.of();
    }

    @ApiStatus.Internal
    public static <T extends WorldsTogetherEntrypoint> List<T> getCommon(Class<T> select) {
        return getEntryPoints(false, select);
    }

    @ApiStatus.Internal
    public static <T extends WorldsTogetherEntrypoint> List<T> getClient(Class<T> select) {
        return getEntryPoints(true, select);
    }
}
