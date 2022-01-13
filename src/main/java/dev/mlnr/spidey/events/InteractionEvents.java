package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.interactions.autocomplete.AutocompleteAction;
import dev.mlnr.spidey.objects.interactions.components.ComponentAction;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionEvents extends ListenerAdapter {
	private final Cache cache;

	public InteractionEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!event.isFromGuild()) {
			event.reply("Spidey only supports commands in servers. Sorry for this inconvenience.").queue();
			return;
		}
		CommandHandler.handle(event, cache);
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		var splitId = event.getComponentId().split(":");
		var action = cache.getComponentActionCache().getAction(splitId[0]);
		processComponentInteraction(splitId[1], action, event);
	}

	@Override
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
		var dropdownId = event.getComponentId();
		var action = cache.getComponentActionCache().getAction(dropdownId);
		processComponentInteraction(event.getValues().get(0), action, event);
	}

	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
		var focusedOption = event.getFocusedOption();
		var actionType = AutocompleteAction.fromFocusedOption(focusedOption);
		var choices = actionType.processTransformer(event, focusedOption);
		event.replyChoices(choices).queue();
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