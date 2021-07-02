package dev.mlnr.spidey.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;

public class StringUtils {
	private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private StringUtils() {}

	public static String getSimilarCommand(String command) {
		return CommandHandler.getCommands().keySet().stream().filter(invoke -> getSimilarity(invoke, command) > 0.5).findFirst().orElse(null);
	}

	private static double getSimilarity(String s1, String s2) { // https://stackoverflow.com/a/16018452/9046789
		var longer = s1;
		var shorter = s2;
		if (s1.length() < s2.length()) {
			longer = s2;
			shorter = s1;
		}
		var longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	}

	private static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		var costs = new int[s2.length() + 1];
		for (var i = 0; i <= s1.length(); i++) {
			var lastValue = i;
			for (var j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					costs[j] = j;
					continue;
				}
                if (j <= 0) {
                    continue;
                }
				var newValue = costs[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                    newValue = min(min(newValue, lastValue), costs[j]) + 1;
                }
				costs[j - 1] = lastValue;
				lastValue = newValue;
			}
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
		}
		return costs[s2.length()];
	}

	public static void createQueuePaginator(CommandContext ctx, List<AudioTrack> queue) {
		var trackChunks = ListUtils.partition(queue, 10);
		var pages = new ArrayList<String>(trackChunks.size());

		var currentTrack = 0;
		for (var tracks : trackChunks) {
			var pageBuilder = new StringBuilder();
			for (var track : tracks) {
				pageBuilder.append(currentTrack + 1).append(". ").append(MusicUtils.formatTrack(track)).append(" [<@").append(track.getUserData(Long.class)).append(">]\n");
				currentTrack++;
			}
			pages.add(pageBuilder.toString());
		}

		var length = queue.stream().mapToLong(track -> track.getInfo().length).sum();
		var size = queue.size();
		var i18n = ctx.getI18n();
		var pluralized = size == 1 ? i18n.get("commands.queue.text.one") : i18n.get("commands.queue.text.multiple", size);

		ctx.getCache().getInteractionCache().createPaginator(ctx, pages.size(), (page, embedBuilder) -> {
			embedBuilder.setAuthor(i18n.get("paginator.queue", ctx.getGuild().getName()));
			embedBuilder.setDescription(pages.get(page));
			embedBuilder.appendDescription("\n\n").appendDescription(pluralized).appendDescription(" ")
					.appendDescription(i18n.get("commands.queue.text.length", MusicUtils.formatDuration(length)));
		});
	}

	public static void createTrackSelection(CommandContext ctx, MusicPlayer musicPlayer, List<AudioTrack> tracks) {
		var size = min(tracks.size(), 25);
		var options = new SelectOption[size];
		for (var i = 0; i < size; i++) {
			var trackInfo = tracks.get(i).getInfo();
			options[i] = SelectOption.of(trimString(trackInfo.author, 25), trackInfo.uri)
					.withDescription(trimString(trackInfo.title, 50));
		}
		ctx.getCache().getInteractionCache().createYouTubeSearchDropdown(ctx, musicPlayer, options);
	}

	public static String randomString(int length) {
		var stringBuilder = new StringBuilder(length);
		var random = ThreadLocalRandom.current();
		for (var i = 0; i < length; i++) {
			var randomInt = random.nextInt(CHARACTERS.length());
			stringBuilder.append(CHARACTERS.charAt(randomInt));
		}
		return stringBuilder.toString();
	}

	public static String trimString(String string, int maxLength) {
		if (string.length() > maxLength) {
			return string.substring(0, maxLength - 1) + "…";
		}
		return string;
	}
}