package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayerCache
{
    private static final Map<Long, MusicPlayer> MUSIC_PLAYER_CACHE = new HashMap<>();

    private MusicPlayerCache() {}

    public static MusicPlayer getMusicPlayer(final Guild guild)
    {
        return getMusicPlayer(guild, false);
    }

    public static MusicPlayer getMusicPlayer(final Guild guild, final boolean createIfAbsent)
    {
        final var guildId = guild.getIdLong();
        var musicPlayer = MUSIC_PLAYER_CACHE.get(guildId);
        if (musicPlayer == null && createIfAbsent)
        {
            musicPlayer = new MusicPlayer(guildId);
            final var audioManager = guild.getAudioManager();
            audioManager.setSendingHandler(musicPlayer.getAudioSendHandler());
            audioManager.setSelfDeafened(true);
            MUSIC_PLAYER_CACHE.put(guildId, musicPlayer);
        }
        return musicPlayer;
    }

    public static void destroyMusicPlayer(final Guild guild)
    {
        final var musicPlayer = getMusicPlayer(guild);
        if (musicPlayer == null)
            return;
        final var guildId = guild.getIdLong();
        musicPlayer.destroyAudioPlayer();
        MUSIC_PLAYER_CACHE.remove(guildId);
        if (Core.getJDA().getGuildById(guildId) == null) // bot has left the guild
            return;
        final var audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
        audioManager.setSendingHandler(null);
    }
}