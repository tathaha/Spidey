package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.interactions.Interaction;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

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
		var interaction = cache.getInteractionCache().getInteraction(splitId[0]);
		processComponentInteraction(splitId[1], interaction, event);
	}

	@Override
	public void onSelectionMenu(SelectionMenuEvent event) {
		var dropdownId = event.getComponentId();
		var interaction = cache.getInteractionCache().getInteraction(dropdownId);
		processComponentInteraction(event.getValues().get(0), interaction, event);
	}

	private void processComponentInteraction(String selectionId, Interaction interaction, GenericInteractionCreateEvent event) {
		((ComponentInteraction) event.getInteraction()).deferEdit().queue();
		if (interaction == null) {
			return;
		}
		if (event.getUser().getIdLong() != interaction.getAuthorId()) {
			return;
		}
		interaction.getType().getInteractionConsumer().accept(selectionId, interaction);
	}
}