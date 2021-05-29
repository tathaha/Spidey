package dev.mlnr.spidey.events;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyEvents extends ListenerAdapter {
	private final DatabaseManager databaseManager;
	private final Cache cache;

	public ReadyEvents(DatabaseManager databaseManager, Cache cache) {
		this.databaseManager = databaseManager;
		this.cache = cache;
	}

	@Override
	public void onGuildReady(GuildReadyEvent event) {
		var guild = event.getGuild();
		databaseManager.registerGuild(guild.getIdLong());
		Utils.storeInvites(guild, cache.getGeneralCache());
	}

	@Override
	public void onReady(ReadyEvent event) {
		var jda = event.getJDA();

		CommandHandler.loadCommands(jda);
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening("s!help"));
	}
}