package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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

	private void processComponentInteraction(String selectionId, ComponentAction componentAction, GenericComponentInteractionCreateEvent event) {
		event.getInteraction().deferEdit().queue();
		if (componentAction == null) {
			return;
		}
		if (event.getUser().getIdLong() != componentAction.getAuthorId()) {
			return;
		}
		componentAction.getType().getActionConsumer().accept(selectionId, componentAction);
	}
}