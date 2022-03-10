package dev.mlnr.spidey.events;

import dev.mlnr.blh.core.api.BLHBuilder;
import dev.mlnr.blh.core.api.BotList;
import dev.mlnr.blh.jda.BLHJDAListener;
import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyEvents extends ListenerAdapter {
	private final Spidey spidey;

	public ReadyEvents(Spidey spidey) {
		this.spidey = spidey;
	}

	@Override
	public void onReady(ReadyEvent event) {
		var jda = event.getJDA();
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening("/help"));

		var blh = new BLHBuilder().setDevModePredicate(botId -> botId != Utils.SPIDEY_ID)
				.setSuccessLoggingEnabled(false)
				.setUnavailableEventsEnabled(false)
				.setErrorLoggingThreshold(2)
				.addBotList(BotList.TOP_GG, System.getenv("topgg"))
				.addBotList(BotList.DISCORDLIST_SPACE, System.getenv("botlistspace"))
				.addBotList(BotList.DSERVICES, System.getenv("dservices"))
				.addBotList(BotList.DBOTS_GG, System.getenv("dbotsgg"))
				.addBotList(BotList.DBL, System.getenv("dbl"))
				.addBotList(BotList.DEL, System.getenv("del"))
				.addBotList(BotList.DISCORDS, System.getenv("bfd"))
				.build();

		var cache = new Cache(spidey);
		var databaseManager = spidey.getDatabaseManager();

		CommandHandler.loadCommands(jda);

		jda.addEventListener(new BanEvents(cache), new DeleteEvents(cache), new GuildEvents(databaseManager, cache),
				new MemberEvents(cache), new MessageEvents(cache), new VoiceEvent(cache), new InteractionEvents(cache), new BLHJDAListener(blh));

		jda.getGuildCache().forEachUnordered(guild -> databaseManager.registerGuild(guild.getIdLong()));
	}
}