package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import net.dv8tion.jda.api.events.interaction.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.stream.Collectors;

public class InteractionEvents extends ListenerAdapter {
	private final Cache cache;

	public InteractionEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		if (!event.isFromGuild()) {
			event.reply("Spidey only supports commands in servers. Sorry for this inconvenience.").queue();
			return;
		}
		CommandHandler.handle(event, cache);
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		var splitId = event.getComponentId().split(":");
		var action = cache.getComponentActionCache().getAction(splitId[0]);
		processComponentInteraction(splitId[1], action, event);
	}

	@Override
	public void onSelectionMenu(SelectionMenuEvent event) {
		var dropdownId = event.getComponentId();
		var action = cache.getComponentActionCache().getAction(dropdownId);
		processComponentInteraction(event.getValues().get(0), action, event);
	}

	@Override
	public void onApplicationCommandAutocomplete(ApplicationCommandAutocompleteEvent event) {
		var input = event.getOption("query").getAsString().toLowerCase();
		var userId = event.getUser().getIdLong();
		var musicHistoryCache = cache.getMusicHistoryCache();

		var type = event.getName();
		var lastQueries = input.isEmpty()
				? musicHistoryCache.getLastQueries(userId, type)
				: musicHistoryCache.getLastQueriesLike(userId, input, type);
		var choices = lastQueries
				.stream()
				.map(query -> new Command.Choice(Emojis.REPEAT + " " + query, query))
				.collect(Collectors.toList());
		event.deferChoices(choices).queue();
	}

	private void processComponentInteraction(String selectionId, ComponentAction componentAction, GenericComponentInteractionCreateEvent event) {
		event.getInteraction().deferEdit().queue(deferred -> {
			if (componentAction == null) {
				return;
			}
			if (event.getUser().getIdLong() != componentAction.getAuthorId()) {
				return;
			}
			componentAction.getType().getActionConsumer().accept(selectionId, componentAction);
		});
	}
}