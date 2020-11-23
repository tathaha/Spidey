package dev.mlnr.spidey.objects.user;

import dev.mlnr.spidey.DatabaseManager;

import java.util.List;

public class UserSettings
{
    private final long userId;

    private final List<String> musicFavorites;
    private boolean segmentSkippingEnabled;

    public UserSettings(final long userId, final List<String> musicFavorites, final boolean segmentSkippingEnabled)
    {
        this.userId = userId;

        this.musicFavorites = musicFavorites;
        this.segmentSkippingEnabled = segmentSkippingEnabled;
    }

    // getters

    public List<String> getMusicFavorites()
    {
        return this.musicFavorites;
    }

    public boolean isSegmentSkippingEnabled()
    {
        return this.segmentSkippingEnabled;
    }

    // setters

    public void addMusicFavorite(final String query)
    {
        musicFavorites.add(query);
        DatabaseManager.addMusicFavorite(this.userId, query);
    }

    public void setSegmentSkippingEnabled(final boolean enabled)
    {
        this.segmentSkippingEnabled = enabled;
        DatabaseManager.setUserSegmentSkippingEnabled(this.userId, enabled);
    }

    // removals

    public void removeMusicFavorite(final String query)
    {
        musicFavorites.remove(query);
        DatabaseManager.removeMusicFavorite(this.userId, query);
    }
}