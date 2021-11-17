package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;

import static dev.mlnr.spidey.utils.MusicUtils.LoadFailureReason.FAIR_QUEUE;
import static dev.mlnr.spidey.utils.MusicUtils.LoadFailureReason.QUEUE_FULL;
import static dev.mlnr.spidey.utils.MusicUtils.createMusicResponseBuilder;
import static dev.mlnr.spidey.utils.MusicUtils.formatLength;

public class AudioLoader implements AudioLoadResultHandler {
	private final MusicPlayer musicPlayer;
	private String query;
	private final CommandContext ctx;

	private boolean searched;

	public AudioLoader(MusicPlayer musicPlayer, String query, CommandContext ctx) {
		this.musicPlayer = musicPlayer;
		this.query = query;
		this.ctx = ctx;
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		loadSingle(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		var tracks = playlist.getTracks();
		if (playlist.isSearchResult()) {
			loadSingle(tracks.get(0));
			return;
		}
		var guildId = ctx.getGuild().getIdLong();
		var i18n = ctx.getI18n();
		var originalLength = 0L;
		var lengthWithoutSegments = 0L;
		var tracksLoaded = 0;
		for (var track : tracks) {
			var loadFailure = loadSingle(track, true);
			if (loadFailure == null) {
				originalLength += track.getInfo().length;
				lengthWithoutSegments += MusicUtils.getLengthWithoutSegments(track, guildId);
				tracksLoaded++;
				continue;
			}
            if (loadFailure == FAIR_QUEUE) {
                continue;
            }
			if (loadFailure == QUEUE_FULL) {
				ctx.replyError(i18n.get("music.messages.failure.add") + " " + i18n.get("music.messages.failure.load.queue_full", MusicUtils.MAX_QUEUE_SIZE) + ".");
				break;
			}
		}
		if (tracksLoaded == 0) {
			ctx.replyErrorLocalized("music.messages.failure.load.no_tracks");
			return;
		}
		var responseEmbedBuilder = createMusicResponseBuilder();
		var responseDescriptionBuilder = responseEmbedBuilder.getDescriptionBuilder();
		responseDescriptionBuilder.append(i18n.get("music.messages.queued")).append(" **").append(tracksLoaded).append("** ").append(i18n.get("music.messages.tracks")).append(" ")
				.append(formatLength(originalLength, lengthWithoutSegments, i18n)).append(" [").append(ctx.getUser().getAsMention()).append("]");
		ctx.reply(responseEmbedBuilder);
	}

	@Override
	public void noMatches() {
		if (!searched) {
			query = "ytsearch:" + query;
			MusicUtils.loadQuery(musicPlayer, query, this);
			searched = true;
			return;
		}
		ctx.replyErrorLocalized("music.messages.failure.no_matches", query.substring(9));
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		ctx.replyErrorLocalized("music.messages.failure.load.error");
	}

	private void loadSingle(AudioTrack track) {
		loadSingle(track, false);
	}

	private MusicUtils.LoadFailureReason loadSingle(AudioTrack track, boolean playlist) {
		var trackScheduler = musicPlayer.getTrackScheduler();
		var queue = trackScheduler.getQueue();
		var trackInfo = track.getInfo();
		var guildId = ctx.getGuild().getIdLong();
		var i18n = ctx.getI18n();

		var loadFailure = MusicUtils.checkTrack(track, this.musicPlayer, guildId);
		if (loadFailure != null) {
            if (!playlist) {
                ctx.replyError(MusicUtils.formatLoadError(loadFailure, ctx));
            }
			return loadFailure;
		}

		var requester = ctx.getUser();
		var title = "[" + trackInfo.title + "](" + trackInfo.uri + ")";
		var originalLength = trackInfo.length;
		var stream = trackInfo.isStream;

		MusicUtils.handleMarkers(track, guildId);
		track.setUserData(requester.getIdLong());
		trackScheduler.queue(track);

        if (playlist) {
            return null;
        }
		var lengthWithoutSegments = MusicUtils.getLengthWithoutSegments(track, guildId);

		var responseEmbedBuilder = createMusicResponseBuilder();
		var responseDescriptionBuilder = responseEmbedBuilder.getDescriptionBuilder();
		responseDescriptionBuilder.append(queue.isEmpty() ? i18n.get("music.messages.playing") : i18n.get("music.messages.queued")).append(" ").append(title)
				.append(stream ? "" : " " + formatLength(originalLength, lengthWithoutSegments, i18n)).append(" [").append(requester.getAsMention()).append("]");
		ctx.sendFollowup(responseEmbedBuilder);
		return null;
	}
}