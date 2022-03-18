package dev.mlnr.spidey.events;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyEvents extends ListenerAdapter {
	private final Spidey spidey;
	private final Cache cache;

	public ReadyEvents(Spidey spidey) {
		this.spidey = spidey;
		this.cache = new Cache(spidey);
	}

	@Override
	public void onReady(ReadyEvent event) {
		var shard = event.getJDA();
		var shardId = shard.getShardInfo().getShardId();
		shard.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening("/help | Shard " + shardId));

		if (shardId == 0) {
			CommandHandler.loadCommands(shard);
		}
		var databaseManager = spidey.getDatabaseManager();
		shard.addEventListener(new BanEvents(cache), new DeleteEvents(cache), new GuildEvents(databaseManager, cache),
				new MemberEvents(cache), new MessageEvents(cache), new VoiceEvent(cache), new InteractionEvents(cache));
		shard.getGuildCache().forEachUnordered(guild -> databaseManager.registerGuild(guild.getIdLong()));
	}
}