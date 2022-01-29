package dev.mlnr.spidey.objects.interactions.autocomplete;

import dev.mlnr.spidey.cache.music.MusicHistoryCache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.utils.CommandUtils;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum AutocompleteAction {
	MUSIC_HISTORY("query", false, (event, input) -> {
		var musicHistoryCache = MusicHistoryCache.getInstance();
		var userId = event.getUser().getIdLong();
		var type = event.getName();
		var lastQueries = input.isEmpty()
				? musicHistoryCache.getLastQueries(userId, type)
				: musicHistoryCache.getLastQueriesLike(userId, input, type);
		return lastQueries
				.stream()
				.map(query -> new Choice(StringUtils.trimString(Emojis.REPEAT + " " + query, 100), query))
				.collect(Collectors.toList());
	}),
	HELP_COMMAND("command", true, (event, input) -> {
		var commands = CommandHandler.getSlashCommands();
		return commands
				.values()
				.stream()
				.filter(command -> command.getName().contains(input) && (event.isFromGuild()
						? CommandUtils.canRunCommand(command, event.getMember())
						: CommandUtils.checkForDevCommand(command, event.getUser())))
				.limit(25)
				.sorted(Comparator.comparing(command -> command.getCategory().getName()))
				.map(command -> {
					var name = command.getName();
					return new Choice(command.getCategory().getFriendlyName() + " - " + name, name);
				})
				.collect(Collectors.toList());
	}),
	UNKNOWN(null, true, (__, ___) -> Collections.emptyList());

	private final String optionName;
	private final boolean ignoreEmptyInput;
	private final BiFunction<CommandAutoCompleteInteractionEvent, String, List<Choice>> transformer;

	AutocompleteAction(String optionName, boolean ignoreEmptyInput, BiFunction<CommandAutoCompleteInteractionEvent, String, List<Choice>> transformer) {
		this.optionName = optionName;
		this.ignoreEmptyInput = ignoreEmptyInput;
		this.transformer = transformer;
	}

	private String getOptionName() {
		return optionName;
	}

	public List<Choice> processTransformer(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery query) {
		var input = query.getValue().toLowerCase();
		return input.isEmpty() && ignoreEmptyInput
				? Collections.emptyList()
				: transformer.apply(event, input);
	}

	public static AutocompleteAction fromFocusedOption(AutoCompleteQuery query) {
		var focusedOptionName = query.getName();
		for (var actionType : values()) {
			if (focusedOptionName.equals(actionType.getOptionName())) {
				return actionType;
			}
		}
		return UNKNOWN;
	}
}