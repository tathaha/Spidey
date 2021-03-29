package dev.mlnr.spidey.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.ListUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static java.lang.Math.min;

public class StringUtils {
	private StringUtils() {}

	public static String getSimilarCommand(String command) {
		return CommandHandler.getCommands().keySet().stream().filter(invoke -> getSimilarity(invoke, command) > 0.5).findFirst().orElse(null);
	}

	private static double getSimilarity(String s1, String s2) // https://stackoverflow.com/a/16018452/9046789
	{
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
		for (int i = 0; i <= s1.length(); i++) {
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

	public static <T> void createSelection(EmbedBuilder selectionBuilder, List<T> elements, CommandContext ctx, String selectionType, Function<T, String> mapper,
	                                       EventWaiter eventWaiter, IntConsumer choiceConsumer) {
		var descriptionBuilder = selectionBuilder.getDescriptionBuilder();
		var size = min(elements.size(), 10);
		for (var i = 0; i < size; i++) {
			var elem = elements.get(i);
			descriptionBuilder.append(i + 1).append(". ").append(mapper.apply(elem)).append("\n");
		}
		var i18n = ctx.getI18n();
		var cancel = i18n.get("selection.cancel");
		descriptionBuilder.append("\n\n").append(i18n.get("selection.type_number", selectionType, cancel));
		selectionBuilder.setDescription(descriptionBuilder.toString());

		var channel = ctx.getTextChannel();
		channel.sendMessage(selectionBuilder.build()).queue(selectionMessage -> {
			var message = ctx.getMessage();
			eventWaiter.waitForEvent(GuildMessageReceivedEvent.class, event -> event.getChannel().equals(channel) && event.getAuthor().equals(ctx.getAuthor()),
					event -> {
						var choiceMessage = event.getMessage();
						var content = choiceMessage.getContentRaw();
						if (content.equalsIgnoreCase(cancel)) {
							channel.purgeMessages(message, selectionMessage, choiceMessage);
							return;
						}
						var choice = 0;
						try {
							choice = Integer.parseUnsignedInt(content);
						}
						catch (NumberFormatException ex) {
							ctx.replyErrorLocalized("number.invalid");
							return;
						}
						if (choice == 0 || choice > size) {
							ctx.replyErrorLocalized("number.range", size);
							return;
						}
						choiceConsumer.accept(choice - 1);
						Utils.deleteMessage(selectionMessage);
					}, 1, TimeUnit.MINUTES,
					() -> {
						channel.purgeMessages(message, selectionMessage);
						ctx.replyErrorLocalized("took_too_long");
					});
		});
	}

	public static void createQueuePaginator(CommandContext ctx, List<AudioTrack> queue) {
		var tracksChunks = ListUtils.partition(queue, 10);
		var descriptions = new HashMap<Integer, StringBuilder>();

		var currentTrack = 0;
		var chunk = 0;
		for (var tracks : tracksChunks) {
			var chunkBuilder = new StringBuilder();
			for (var track : tracks) {
				chunkBuilder.append(currentTrack + 1).append(". ").append(MusicUtils.formatTrack(track)).append(" [<@").append(track.getUserData(Long.class)).append(">]\n");
				currentTrack++;
			}
			descriptions.put(chunk, chunkBuilder);
			chunk++;
		}

		var length = queue.stream().mapToLong(track -> track.getInfo().length).sum();
		var size = queue.size();
		var i18n = ctx.getI18n();
		var pluralized = size == 1 ? i18n.get("commands.queue.other.text.one") : i18n.get("commands.queue.other.text.multiple", size);

		ctx.getCache().getPaginatorCache().createPaginator(ctx, descriptions.size(), (page, embedBuilder) -> {
			embedBuilder.setAuthor(i18n.get("paginator.queue", ctx.getGuild().getName()));
			embedBuilder.setDescription(descriptions.get(page));
			embedBuilder.appendDescription("\n\n").appendDescription(pluralized).appendDescription(" ")
					.appendDescription(i18n.get("commands.queue.other.text.length", MusicUtils.formatDuration(length)));
		});
	}
}