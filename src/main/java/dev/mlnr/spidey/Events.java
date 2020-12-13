package dev.mlnr.spidey;

import dev.mlnr.spidey.cache.GeneralCache;
import dev.mlnr.spidey.cache.MessageCache;
import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.guild.InviteData;
import dev.mlnr.spidey.objects.messages.MessageData;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

import static net.dv8tion.jda.api.utils.MarkdownSanitizer.escape;

public class Events extends ListenerAdapter
{
	@Override
	public void onGuildMessageReceived(final GuildMessageReceivedEvent event)
	{
		final var guild = event.getGuild();
		final var message = event.getMessage();
		final var guildId = guild.getIdLong();
		final var content = message.getContentRaw().trim();

		if (!content.isEmpty() && GuildSettingsCache.isSnipingEnabled(guildId))
			MessageCache.cacheMessage(message.getIdLong(), new MessageData(message));

		final var prefix = GuildSettingsCache.getPrefix(guildId);
		if (!content.startsWith(prefix) || event.getAuthor().isBot() || event.isWebhookMessage())
			return;
		CommandHandler.handle(event, prefix);
	}

	@Override
	public void onGuildBan(final GuildBanEvent event)
	{
		final var guild = event.getGuild();
		final var channel = GuildSettingsCache.getLogChannel(guild.getIdLong());
		if (channel == null)
			return;
		final var user = event.getUser();
		final var escapedTag = escape(user.getAsTag());
		final var eb = new EmbedBuilder();
		eb.setDescription(Emojis.CROSS + " **" + escapedTag + "** has been `banned`");
		eb.setColor(14495300);
		eb.setFooter("User ban", user.getEffectiveAvatarUrl());
		eb.setTimestamp(Instant.now());
		if (!guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS))
		{
			eb.appendDescription(".");
			Utils.sendMessage(channel, eb.build());
			return;
		}
		guild.retrieveAuditLogs().type(ActionType.BAN).queue(bans ->
		{
			final var last = bans.get(0);
			final var bannerTag = escape(last.getUser().getAsTag());
			var reason = last.getReason();
			reason = reason == null || reason.isEmpty() ? "unknown reason" : reason.trim();
			eb.appendDescription(" by **" + bannerTag + "** for **" + reason + "**.");
			Utils.sendMessage(channel, eb.build());
		});
	}

	@Override
	public void onGuildUnban(final GuildUnbanEvent event)
	{
		final var guild = event.getGuild();
		final var channel = GuildSettingsCache.getLogChannel(guild.getIdLong());
		if (channel == null)
			return;
		final var user = event.getUser();
		final var escapedTag = escape(user.getAsTag());
		final var eb = new EmbedBuilder();
		eb.setDescription(Emojis.CHECK + " **" + escapedTag + "** has been `unbanned`");
		eb.setColor(7844437);
		eb.setFooter("User unban", user.getEffectiveAvatarUrl());
		eb.setTimestamp(Instant.now());
		if (!guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS))
		{
			eb.appendDescription(".");
			Utils.sendMessage(channel, eb.build());
			return;
		}
		guild.retrieveAuditLogs().type(ActionType.UNBAN).queue(unbans ->
		{
			final var last = unbans.get(0);
			final var unbannerTag = escape(last.getUser().getAsTag());
			eb.appendDescription(" by **" + unbannerTag + "**.");
			Utils.sendMessage(channel, eb.build());
		});
	}

	@Override
	public void onGuildMemberRemove(final GuildMemberRemoveEvent event)
	{
		final var channel = GuildSettingsCache.getLogChannel(event.getGuild().getIdLong());
		if (channel == null)
			return;
		final var user = event.getUser();
		final var escapedTag = escape(user.getAsTag());
		final var avatarUrl = user.getEffectiveAvatarUrl();
		final var userId = user.getIdLong();
		final var eb = new EmbedBuilder();
		eb.setColor(14495300);
		eb.setTimestamp(Instant.now());
		if (user.isBot())
		{
			eb.setDescription("\uD83E\uDD16 Bot **" + escapedTag + "** (" + userId + ") has been `removed` from this server.");
			eb.setFooter("Bot remove", avatarUrl);
			Utils.sendMessage(channel, eb.build());
			return;
		}
		eb.setDescription("\uD83D\uDCE4 **" + escapedTag + "** (" + userId + ") has `left` the server.");
		eb.setFooter("User leave", avatarUrl);
		Utils.sendMessage(channel, eb.build());
	}

	@Override
	public void onGuildMemberJoin(final GuildMemberJoinEvent event)
	{
		final var guild = event.getGuild();
		final var guildId = guild.getIdLong();
		final var channel = GuildSettingsCache.getLogChannel(guildId);
		if (channel == null)
			return;
		final var joinRole = GuildSettingsCache.getJoinRole(guildId);
		final var selfMember = guild.getSelfMember();
		final var user = event.getUser();
		final var userId = user.getIdLong();

		if (joinRole != null && selfMember.canInteract(joinRole) && selfMember.hasPermission(Permission.MANAGE_ROLES))
			guild.addRoleToMember(userId, joinRole).queue();

		final var escapedTag = escape(user.getAsTag());
		final var avatarUrl = user.getEffectiveAvatarUrl();
		final var eb = new EmbedBuilder();
		eb.setTimestamp(Instant.now());
		if (user.isBot())
		{
			eb.setFooter("Bot add", avatarUrl);
			eb.setColor(5614830);
			if (!selfMember.hasPermission(Permission.VIEW_AUDIT_LOGS))
			{
				eb.setDescription("\uD83E\uDD16 Bot **" + escapedTag + "** (" + userId + ") has been `added` to this server.");
				Utils.sendMessage(channel, eb.build());
				return;
			}
			guild.retrieveAuditLogs().type(ActionType.BOT_ADD).queue(botsAdded ->
			{
				final var last = botsAdded.get(0);
				eb.setDescription("\uD83E\uDD16 **" + escape(last.getUser().getAsTag()) + "** has `added` bot **" + escapedTag + "** (" + userId + ") to this server.");
				Utils.sendMessage(channel, eb.build());
			});
			return;
		}
		eb.setColor(7844437);
		eb.setFooter("User join", avatarUrl);
		eb.setDescription("\uD83D\uDCE5 **" + escapedTag + "** (" + userId + ") has `joined` the server");
		if (!selfMember.hasPermission(Permission.MANAGE_SERVER))
		{
			eb.appendDescription(".");
			Utils.sendMessage(channel, eb.build());
			return;
		}
		guild.retrieveInvites().queue(invites ->
		{
			for (final var invite : invites)
			{
				final var inviteData = GeneralCache.getInviteCache().get(invite.getCode());
				if (inviteData == null || invite.getUses() == inviteData.getUses())
					continue;
				inviteData.incrementUses();
				eb.appendDescription(" with invite **" + invite.getUrl() + "** (**" + escape(invite.getInviter().getAsTag()) + "**).");
				Utils.sendMessage(channel, eb.build());
				break;
			}
		});
	}

	@Override
	public void onGuildReady(final GuildReadyEvent event)
	{
		final var guild = event.getGuild();
		Utils.storeInvites(guild);
	}

	@Override
	public void onGuildJoin(final GuildJoinEvent event)
	{
		final var guild = event.getGuild();
		final var defaultChannel = guild.getDefaultChannel();
		if (defaultChannel != null)
			Utils.sendMessage(defaultChannel, "Hey! I'm **Spidey**. Thanks for inviting me. To start, check `s!info`.");
		Utils.storeInvites(guild);
		Requester.updateStats(event.getJDA());
		guild.findMembers(member -> !member.getUser().isBot()).onSuccess(people ->
		{
			if (people.size() >= 10000)
				GuildSettingsCache.setSnipingEnabled(guild.getIdLong(), false);
		});
	}

	@Override
	public void onGuildLeave(final GuildLeaveEvent event)
	{
		final var guild = event.getGuild();
		final var guildId = guild.getIdLong();
		GeneralCache.getInviteCache().entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
		MessageCache.pruneCache(guildId);
		MusicPlayerCache.destroyMusicPlayer(guild);
		GeneralCache.removeGuild(guildId);
		Requester.updateStats(event.getJDA());
	}

	@Override
	public void onTextChannelDelete(final TextChannelDeleteEvent event)
	{
		final var guildId = event.getGuild().getIdLong();
		if (event.getChannel().getIdLong() == GuildSettingsCache.getLogChannelId(guildId))
			GuildSettingsCache.removeLogChannel(guildId);
	}

	@Override
	public void onRoleDelete(final RoleDeleteEvent event)
	{
		final var roleId = event.getRole().getIdLong();
		final var guildId = event.getGuild().getIdLong();
		if (roleId == GuildSettingsCache.getJoinRoleId(guildId))
			GuildSettingsCache.removeJoinRole(guildId);
		if (roleId == GuildSettingsCache.getDJRoleId(guildId))
			GuildSettingsCache.removeDJRole(guildId);
	}

	@Override
	public void onGuildMemberUpdateBoostTime(final GuildMemberUpdateBoostTimeEvent event)
	{
		final var guild = event.getGuild();
		final var log = GuildSettingsCache.getLogChannel(guild.getIdLong());
		if (log == null)
			return;
		if (event.getOldTimeBoosted() != null)
			return;
		final var user = event.getUser();
		final var eb = new EmbedBuilder();
		eb.setDescription("**" + escape(user.getAsTag()) + "** has `boosted` the server. The server currently has **" + guild.getBoostCount() + "** boosts.");
		eb.setAuthor("NEW BOOST");
		eb.setColor(16023551);
		eb.setFooter("User boost", user.getEffectiveAvatarUrl());
		eb.setTimestamp(Instant.now());
		Utils.sendMessage(log, eb.build());
	}

	@Override
	public void onGuildUpdateBoostTier(final GuildUpdateBoostTierEvent event)
	{
		final var guild = event.getGuild();
		final var channel = GuildSettingsCache.getLogChannel(guild.getIdLong());
		if (channel == null)
			return;
		final var eb = new EmbedBuilder();
		eb.setAuthor("GUILD BOOST TIER HAS CHANGED");
		eb.setColor(16023551);
		eb.setTimestamp(Instant.now());
		eb.addField("Boost tier", "**" + event.getNewBoostTier().getKey() + "**", true);
		eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
		Utils.sendMessage(channel, eb.build());
	}

	@Override
	public void onGuildInviteCreate(final GuildInviteCreateEvent event)
	{
		GeneralCache.getInviteCache().put(event.getCode(), new InviteData(event.getInvite()));
	}

	@Override
	public void onGuildInviteDelete(final GuildInviteDeleteEvent event)
	{
		GeneralCache.getInviteCache().remove(event.getCode());
	}

	@Override
	public void onReady(final ReadyEvent event)
	{
		final var jda = event.getJDA();
		Utils.startup(jda);
		Requester.updateStats(jda);
	}

	@Override
	public void onGuildMessageDelete(final GuildMessageDeleteEvent event)
	{
		if (!GuildSettingsCache.isSnipingEnabled(event.getGuild().getIdLong()))
			return;
		final var messageId = event.getMessageIdLong();
		if (!MessageCache.isCached(messageId))
			return;
		MessageCache.setLastDeletedMessage(event.getChannel().getIdLong(), messageId);
	}

	@Override
	public void onGuildMessageUpdate(final GuildMessageUpdateEvent event)
	{
		if (!GuildSettingsCache.isSnipingEnabled(event.getGuild().getIdLong()))
			return;
		final var messageId = event.getMessageIdLong();
		if (!MessageCache.isCached(messageId))
			return;
		MessageCache.cacheMessage(messageId, new MessageData(event.getMessage()));
		MessageCache.setLastEditedMessage(event.getChannel().getIdLong(), messageId);
	}

	@Override
	public void onGuildVoiceJoin(final GuildVoiceJoinEvent event)
	{
		final var guild = event.getGuild();
		if (!event.getMember().getUser().isBot() && event.getChannelJoined().equals(MusicUtils.getConnectedChannel(guild)))
		{
			final var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);
			musicPlayer.cancelLeave();
			musicPlayer.unpause();
		}
	}

	@Override
	public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event)
	{
		final var guild = event.getGuild();
		if (event.getMember().getIdLong() == event.getJDA().getSelfUser().getIdLong())
		{
			MusicPlayerCache.destroyMusicPlayer(guild);
			return;
		}
		final var connectedChannel = MusicUtils.getConnectedChannel(guild);
		if (event.getChannelLeft().equals(connectedChannel) && connectedChannel.getMembers().stream().allMatch(member -> member.getUser().isBot()))
		{
			final var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);
			musicPlayer.scheduleLeave();
			musicPlayer.pause();
		}
	}
}