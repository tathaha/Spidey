package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.handlers.music.AudioPlayerSendHandler;
import dev.mlnr.spidey.utils.ConcurrentUtils;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MusicPlayer {

	private final TrackScheduler trackScheduler;
	private final AudioPlayer audioPlayer;

	private ScheduledFuture<?> leaveTask;

	public MusicPlayer(long guildId, JDA jda) {
		this.audioPlayer = MusicUtils.getAudioPlayerManager().createPlayer();

		audioPlayer.setVolume(GuildSettingsCache.getInstance().getMusicSettings(guildId).getDefaultVolume());
		this.trackScheduler = new TrackScheduler(this.audioPlayer, guildId, jda);
	}

	public TrackScheduler getTrackScheduler() {
		return this.trackScheduler;
	}

	// leave task

	public void scheduleLeave() {
		cancelLeave();
		leaveTask = ConcurrentUtils.getScheduler().schedule(() -> MusicPlayerCache.getInstance().disconnectFromChannel(trackScheduler.getGuild()), 2, TimeUnit.MINUTES);
	}

	public void cancelLeave() {
		if (leaveTask == null) {
			return;
		}
		leaveTask.cancel(true);
		leaveTask = null;
	}

	// AudioPlayer wrapper methods

	public AudioTrack getPlayingTrack() {
		return audioPlayer.getPlayingTrack();
	}

	public int getVolume() {
		return audioPlayer.getVolume();
	}

	public void setVolume(int volume) {
		audioPlayer.setVolume(volume);
	}

	public void pause() {
		audioPlayer.setPaused(true);
	}

	public void unpause() {
		audioPlayer.setPaused(false);
	}

	public boolean pauseOrUnpause() {
		var state = !isPaused();
		audioPlayer.setPaused(state);
		return state;
	}

	public boolean isPaused() {
		return audioPlayer.isPaused();
	}

	public void destroyAudioPlayer() {
		audioPlayer.destroy();
	}

	// track scheduler wrapper methods

	public void skip() {
		trackScheduler.nextTrack();
	}

	// other

	public AudioSendHandler getAudioSendHandler() {
		return new AudioPlayerSendHandler(this.audioPlayer);
	}
}