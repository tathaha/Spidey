package dev.mlnr.spidey;

import dev.mlnr.spidey.handlers.CommandHandler;
import dev.mlnr.spidey.objects.cache.*;
import dev.mlnr.spidey.objects.invites.InviteData;
import dev.mlnr.spidey.objects.messages.MessageData;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.Instant;

import static net.dv8tion.jda.api.utils.MarkdownSanitizer.escape;

@SuppressWarnings("StringBufferReplaceableByString")
public class Events extends ListenerAdapter
{
	@Override
	public void onGuildMessageReceived(final GuildMessageReceivedEvent event)
	{
		final var guild = event.getGuild();
		final var message = event.getMessage();
		final var author = event.getAuthor();
		final var guildId = guild.getIdLong();
		final var prefix = PrefixCache.retrievePrefix(guildId);
		final var content = message.getContentRaw().trim();
		final var channel = event.getChannel();

		if (!content.isEmpty())
			MessageCache.cacheMessage(message.getIdLong(), new MessageData(message));

		if ((content.equals("<@468523263853592576>") || content.equals("<@!468523263853592576>")) && !author.isBot())
		{
			final var eb = new EmbedBuilder();
			eb.setColor(16763981);
			eb.setDescription("Looks like you forgot my prefix, no worries though!\n\nPrefix for this server is `" + prefix + "`.");
			Utils.sendMessage(channel, eb.build());
			return;
		}

		if (content.startsWith(prefix) && !author.isBot())
		{
			if (guild.getSelfMember().hasPermission(Permission.ADMINISTRATOR))
			{
				Utils.sendMessage(channel, CommandHandler.ADMIN_WARNING);
				return;
			}
			CommandHandler.handle(event, prefix);
			return;
		}

		if (message.getType() == MessageType.GUILD_MEMBER_BOOST)
		{
			final var eb = new EmbedBuilder();
			final var log = LogChannelCache.getLogAsChannel(guildId, event.getJDA());
			if (log == null)
				return;
			Utils.deleteMessage(message);
			eb.setDescription(new StringBuilder().append("<:boosting:699731065052332123>")
					.append(" **").append(escape(author.getAsTag())).append("** has `boosted` ")
					.append("the server. The server currently has **").append(guild.getBoostCount()).append("** boosts.").toString());
			eb.setAuthor("NEW BOOST");
			eb.setColor(16023551);
			eb.setFooter("User boost", author.getEffectiveAvatarUrl());
			eb.setTimestamp(Instant.now());
			Utils.sendMessage(log, eb.build());
		}
	}

	@Override
	public void onGuildBan(final GuildBanEvent event)
	{
		final var user = event.getUser();
		final var guild = event.getGuild();
		final var channel = LogChannelCache.getLogAsChannel(guild.getIdLong(), event.getJDA());

		if (channel == null)
			return;
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
		final var user = event.getUser();
		final var guild = event.getGuild();
		final var channel = LogChannelCache.getLogAsChannel(guild.getIdLong(), event.getJDA());

		if (channel == null)
			return;
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
		final var user = event.getUser();
		final var guild = event.getGuild();
		final var channel = LogChannelCache.getLogAsChannel(guild.getIdLong(), event.getJDA());

		if (channel == null)
			return;
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
	public void onGuildMemberJoin(@NotNull final GuildMemberJoinEvent event)
	{
		final var user = event.getUser();
		final var guild = event.getGuild();
		final var guildId = guild.getIdLong();
		final var jda = event.getJDA();
		final var channel = LogChannelCache.getLogAsChannel(guildId, jda);
		final var joinRole = JoinRoleCache.getJoinRole(guildId, jda);
		final var userId = user.getIdLong();
		final var selfMember = guild.getSelfMember();

		if (channel == null)
			return;
		if (joinRole != null && (selfMember.canInteract(joinRole) && selfMember.hasPermission(Permission.MANAGE_ROLES)))
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
				final var inviteData = Cache.getInviteCache().get(invite.getCode());
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
	public void onGuildReady(@NotNull final GuildReadyEvent event)
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
	}

	@Override
	public void onGuildLeave(final GuildLeaveEvent event)
	{
		final var guildId = event.getGuild().getIdLong();
		Cache.getInviteCache().entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
		MessageCache.pruneCache(guildId);
		Cache.removeEntry(guildId);
	}

	@Override
	public void onTextChannelDelete(final TextChannelDeleteEvent event)
	{
		final var guildId = event.getGuild().getIdLong();
		if (event.getChannel().getIdLong() == LogChannelCache.retrieveLogChannel(guildId))
			LogChannelCache.removeLogChannel(guildId);
	}

	@Override
	public void onRoleDelete(final RoleDeleteEvent event)
	{
		final var guildId = event.getGuild().getIdLong();
		if (event.getRole().getIdLong() == JoinRoleCache.retrieveJoinRole(guildId))
			JoinRoleCache.removeJoinRole(guildId);
	}

	@Override
	public void onGuildUpdateBoostTier(final GuildUpdateBoostTierEvent event)
	{
		final var guild = event.getGuild();
		final var channel = LogChannelCache.getLogAsChannel(guild.getIdLong(), event.getJDA());
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
	public void onGuildInviteCreate(@NotNull final GuildInviteCreateEvent event)
	{
		Cache.getInviteCache().put(event.getCode(), new InviteData(event.getInvite()));
	}

	@Override
	public void onGuildInviteDelete(@NotNull final GuildInviteDeleteEvent event)
	{
		Cache.getInviteCache().remove(event.getCode());
	}

	@Override
	public void onReady(@NotNull final ReadyEvent event)
	{
		Utils.startup(event.getJDA());
	}

	@Override
	public void onGuildMessageDelete(@NotNull final GuildMessageDeleteEvent event)
	{
		final var messageId = event.getMessageIdLong();
		if(!MessageCache.isCached(messageId))
			return;
		MessageCache.setLastDeletedMessage(event.getChannel().getIdLong(), messageId);
	}

	@Override
	public void onGuildMessageUpdate(@Nonnull final GuildMessageUpdateEvent event)
	{
		final var messageId = event.getMessageIdLong();
		if(!MessageCache.isCached(messageId))
			return;
		MessageCache.cacheMessage(messageId, new MessageData(event.getMessage()));
		MessageCache.setLastEditedMessage(event.getChannel().getIdLong(), messageId);
	}
}
