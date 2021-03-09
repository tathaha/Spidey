package dev.mlnr.spidey.events;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyEvents extends ListenerAdapter {
	private final Spidey spidey;

	public ReadyEvents(Spidey spidey) {
		this.spidey = spidey;
	}

	@Override
	public void onGuildReady(GuildReadyEvent event) {
		spidey.getDatabaseManager().registerGuild(event.getGuild().getIdLong());
	}

	@Override
	public void onReady(ReadyEvent event) {
		var jda = event.getJDA();
		var cache = new Cache(spidey, jda);
		jda.getPresence().setActivity(Activity.listening("s!help"));
		jda.getGuildCache().forEachUnordered(guild -> Utils.storeInvites(guild, cache.getGeneralCache()));
		jda.addEventListener(new BanEvents(cache), new DeleteEvents(cache), new GuildEvents(spidey, cache), new InviteEvents(cache),
				new MemberEvents(cache), new MessageEvents(cache), new PaginatorEvent(cache), new VoiceEvent(cache));
	}
}