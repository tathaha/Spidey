package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.objects.music.MusicPlayer;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayerCache {
	private final Map<Long, MusicPlayer> musicPlayerMap = new HashMap<>();

	private static MusicPlayerCache musicPlayerCache;

	public static synchronized MusicPlayerCache getInstance() {
		if (musicPlayerCache == null)
			musicPlayerCache = new MusicPlayerCache();
		return musicPlayerCache;
	}

	public MusicPlayer getMusicPlayer(Guild guild) {
		return getMusicPlayer(guild, false);
	}

	public MusicPlayer getMusicPlayer(Guild guild, boolean createIfAbsent) {
		var guildId = guild.getIdLong();
		var musicPlayer = musicPlayerMap.get(guildId);
		if (musicPlayer == null && createIfAbsent) {
			musicPlayer = new MusicPlayer(guildId, guild.getJDA());
			var audioManager = guild.getAudioManager();
			audioManager.setSendingHandler(musicPlayer.getAudioSendHandler());
			audioManager.setSelfDeafened(true);
			musicPlayerMap.put(guildId, musicPlayer);
		}
		return musicPlayer;
	}

	public void disconnectFromChannel(Guild guild) {
		var audioManager = guild.getAudioManager();
		audioManager.closeAudioConnection();
		audioManager.setSendingHandler(null);
	}

	public void destroyMusicPlayer(Guild guild) {
		var musicPlayer = getMusicPlayer(guild);
		if (musicPlayer == null) {
			return;
		}
		var guildId = guild.getIdLong();
		musicPlayer.destroyAudioPlayer();
		musicPlayerMap.remove(guildId);
	}
}