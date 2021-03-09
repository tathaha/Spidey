package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.guild.InviteData;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InviteEvents extends ListenerAdapter {
	private final Cache cache;

	public InviteEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGuildInviteCreate(GuildInviteCreateEvent event) {
		cache.getGeneralCache().getInviteCache().put(event.getCode(), new InviteData(event.getInvite()));
	}

	@Override
	public void onGuildInviteDelete(GuildInviteDeleteEvent event) {
		cache.getGeneralCache().getInviteCache().remove(event.getCode());
	}
}