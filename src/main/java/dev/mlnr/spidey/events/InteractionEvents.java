package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
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
			event.reply("Spidey only supports commands in a server. Sorry for this inconvenience.").queue();
			return;
		}
		CommandHandler.handle(event, cache);
	}
}