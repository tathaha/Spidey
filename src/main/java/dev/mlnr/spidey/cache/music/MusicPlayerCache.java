package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.objects.music.MusicPlayer;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayerCache
{
    private static final Map<Long, MusicPlayer> MUSIC_PLAYER_CACHE = new HashMap<>();

    private MusicPlayerCache() {}

    public static MusicPlayer getMusicPlayer(Guild guild)
    {
        return getMusicPlayer(guild, false);
    }

    public static MusicPlayer getMusicPlayer(Guild guild, boolean createIfAbsent)
    {
        var guildId = guild.getIdLong();
        var musicPlayer = MUSIC_PLAYER_CACHE.get(guildId);
        if (musicPlayer == null && createIfAbsent)
        {
            musicPlayer = new MusicPlayer(guildId);
            var audioManager = guild.getAudioManager();
            audioManager.setSendingHandler(musicPlayer.getAudioSendHandler());
            audioManager.setSelfDeafened(true);
            MUSIC_PLAYER_CACHE.put(guildId, musicPlayer);
        }
        return musicPlayer;
    }

    public static void disconnectFromChannel(Guild guild)
    {
        var audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
        audioManager.setSendingHandler(null);
    }

    public static void destroyMusicPlayer(Guild guild)
    {
        var musicPlayer = getMusicPlayer(guild);
        if (musicPlayer == null)
            return;
        var guildId = guild.getIdLong();
        musicPlayer.destroyAudioPlayer();
        MUSIC_PLAYER_CACHE.remove(guildId);
    }
}