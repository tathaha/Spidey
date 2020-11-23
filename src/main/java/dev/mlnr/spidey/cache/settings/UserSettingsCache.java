package dev.mlnr.spidey.cache.settings;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.user.UserSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserSettingsCache
{
    private static final Map<Long, UserSettings> USER_SETTINGS_CACHE = new HashMap<>();

    private UserSettingsCache() {}

    // getters

    public static List<String> getMusicFavorites(final long userId)
    {
        return getUserSettings(userId).getMusicFavorites();
    }

    public static boolean isSegmentSkippingEnabled(final long userId)
    {
        return getUserSettings(userId).isSegmentSkippingEnabled();
    }

    // setters

    public static void addMusicFavorite(final long userId, final String videoId)
    {
        getUserSettings(userId).addMusicFavorite(videoId);
    }

    public static void setSegmentSkippingEnabled(final long userId, final boolean enabled)
    {
        getUserSettings(userId).setSegmentSkippingEnabled(enabled);
    }

    // removals

    public static void removeMusicFavorite(final long userId, final String videoId)
    {
        getUserSettings(userId).removeMusicFavorite(videoId);
    }

    // other

    private static UserSettings getUserSettings(final long userId)
    {
        return Objects.requireNonNullElseGet(USER_SETTINGS_CACHE.get(userId), () ->
        {
           final var settings = DatabaseManager.retrieveUserSettings(userId);
           USER_SETTINGS_CACHE.put(userId, settings);
           return settings;
        });
    }
}