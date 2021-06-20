package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.mlnr.spidey.objects.command.ChoicesEnum;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static dev.mlnr.spidey.utils.MusicUtils.handleMarkers;

public class TrackScheduler extends AudioEventAdapter {
	private final AudioPlayer audioPlayer;
	private final long guildId;

	private final Queue<AudioTrack> queue = new LinkedList<>();
	private final List<Long> skipVotes = new ArrayList<>();
	private RepeatMode repeatMode;
	private AudioTrack previousTrack;
	private AudioTrack currentTrack;

	private final JDA jda;

	public TrackScheduler(AudioPlayer audioPlayer, long guildId, JDA jda) {
		this.audioPlayer = audioPlayer;
		audioPlayer.addListener(this);
		this.guildId = guildId;

		this.jda = jda;
	}

	public void queue(AudioTrack track) {
		if (audioPlayer.getPlayingTrack() == null) {
			audioPlayer.playTrack(track);
			currentTrack = track;
			return;
		}
		queue.offer(track);
	}

	public void nextTrack() {
		skipVotes.clear();
		if (repeatMode == RepeatMode.SONG && currentTrack != null) {
			var currentCloned = currentTrack.makeClone();
			handleMarkers(currentCloned, this.guildId);
			queue(currentCloned);
			return;
		}

		if (currentTrack != null) {
			previousTrack = currentTrack;
		}

		var nextTrack = queue.poll();
		currentTrack = nextTrack;

		var trackToPlay = nextTrack != null ? nextTrack : (repeatMode == RepeatMode.QUEUE ? previousTrack.makeClone() : null);
		if (trackToPlay != null) {
			handleMarkers(trackToPlay, this.guildId);
		}
		audioPlayer.playTrack(trackToPlay);
	}

	public Queue<AudioTrack> getQueue() {
		return this.queue;
	}

	public List<AudioTrack> getQueueAsList() {
		return (List<AudioTrack>) this.queue;
	}

	public void setRepeatMode(RepeatMode repeatMode) {
		this.repeatMode = repeatMode;
	}

	public Guild getGuild() {
		return jda.getGuildById(this.guildId);
	}

	// skip voting

	public int getRequiredSkipVotes() {
		var listeners = MusicUtils.getConnectedChannel(getGuild()).getMembers().stream()
				.filter(member -> !member.getUser().isBot() && !member.getVoiceState().isDeafened())
				.count();
		return (int) Math.ceil(listeners * 0.55);
	}

	public int getSkipVotes() {
		return skipVotes.size();
	}

	public void addSkipVote(User user) {
		skipVotes.add(user.getIdLong());
	}

	public void removeSkipVote(User user) {
		skipVotes.remove(user.getIdLong());
	}

	public boolean hasSkipVoted(User user) {
		return skipVotes.contains(user.getIdLong());
	}

	// events

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			nextTrack();
		}
	}

	// other

	public enum RepeatMode implements ChoicesEnum {
		SONG("Song"),
		QUEUE("Queue"),
		NONE("None");

		private final String friendlyName;

		RepeatMode(String friendlyName) {
			this.friendlyName = friendlyName;
		}

		public String getFriendlyName() {
			return friendlyName;
		}
	}
}