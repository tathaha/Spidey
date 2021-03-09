package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceEvent extends ListenerAdapter {
	private final Cache cache;

	public VoiceEvent(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		var member = event.getEntity();
		var guild = member.getGuild();
		var musicPlayerCache = cache.getMusicPlayerCache();
		var connectedChannel = MusicUtils.getConnectedChannel(guild);
		var musicPlayer = musicPlayerCache.getMusicPlayer(guild);

		// "join"
		if (event instanceof GuildVoiceJoinEvent || event instanceof GuildVoiceMoveEvent) {
			if (!member.getUser().isBot() && event.getChannelJoined().equals(connectedChannel)) {
				musicPlayer.cancelLeave();
				musicPlayer.unpause();
			}
			return;
		}
		// "leave"
		if (member.getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
			musicPlayerCache.destroyMusicPlayer(guild);
			return;
		}
		if (event.getChannelLeft().equals(connectedChannel) && connectedChannel.getMembers().stream().allMatch(connectedMember -> connectedMember.getUser().isBot())) {
			musicPlayer.scheduleLeave();
			musicPlayer.pause();
		}
	}
}