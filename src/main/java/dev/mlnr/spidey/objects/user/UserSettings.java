package dev.mlnr.spidey.objects.user;

import dev.mlnr.spidey.DatabaseManager;

import java.util.List;

public class UserSettings
{
    private final long userId;

    private final List<String> musicFavorites;

    public UserSettings(final long userId, final List<String> musicFavorites)
    {
        this.userId = userId;

        this.musicFavorites = musicFavorites;
    }

    // getters

    public List<String> getMusicFavorites()
    {
        return this.musicFavorites;
    }

    // setters

    public void addMusicFavorite(final String query)
    {
        musicFavorites.add(query);
        DatabaseManager.addMusicFavorite(this.userId, query);
    }

    // removals

    public void removeMusicFavorite(final String query)
    {
        musicFavorites.remove(query);
        DatabaseManager.removeMusicFavorite(this.userId, query);
    }
}