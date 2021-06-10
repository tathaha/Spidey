package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
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
		event.deferEdit().queue();

		var splitId = event.getComponentId().split(":");
		var buttonActionCache = cache.getButtonActionCache();
		var buttonAction = buttonActionCache.getButtonAction(splitId[0]);
		if (buttonAction == null) {
			return;
		}
		if (event.getUser().getIdLong() != buttonAction.getAuthorId()) {
			return;
		}
		buttonAction.getType().getButtonConsumer().accept(splitId[1], buttonAction);
	}
}