package dev.mlnr.spidey;

import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.objects.command.CommandHandler;
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

@SuppressWarnings({"ConstantConditions", "StringBufferReplaceableByString"})
public class Events extends ListenerAdapter
{
	@Override
	public final void onGuildMessageReceived(final GuildMessageReceivedEvent e)
	{
		final var guild = e.getGuild();
		final var message = e.getMessage();
		final var author = e.getAuthor();
		final var guildId = guild.getIdLong();
		final var eb = new EmbedBuilder();
		final var prefix = Cache.retrievePrefix(guildId);
		final var content = message.getContentRaw().trim();

		if (!content.isEmpty())
			Cache.cacheMessage(message.getIdLong(), new MessageData(message));

		if (content.startsWith(prefix) && !author.isBot())
		{
			if (guild.getSelfMember().hasPermission(Permission.ADMINISTRATOR))
			{
				Utils.sendMessage(message.getTextChannel(), CommandHandler.ADMIN_WARNING);
				return;
			}
			CommandHandler.handle(message, prefix);
			return;
		}

		if (message.getType() == MessageType.GUILD_MEMBER_BOOST)
		{
			final var jda = e.getJDA();
			final var channel = Cache.getLogAsChannel(guildId, jda);
			if (channel == null)
				return;
			Utils.deleteMessage(message);
			eb.setDescription(new StringBuilder().append(jda.getEmoteById(699731065052332123L).getAsMention())
					.append(" **").append(escape(author.getAsTag())).append("** has `boosted` ")
					.append("the server. The server currently has **").append(guild.getBoostCount()).append("** boosts.").toString());
			eb.setAuthor("NEW BOOST");
			eb.setColor(16023551);
			eb.setFooter("User boost", author.getEffectiveAvatarUrl());
			eb.setTimestamp(Instant.now());
			Utils.sendMessage(channel, eb.build());
		}
	}

	@Override
	public final void onGuildBan(final GuildBanEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();
		final var channel = Cache.getLogAsChannel(guild.getIdLong(), e.getJDA());

		if (channel == null || !guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS, Permission.VIEW_AUDIT_LOGS))
			return;
		guild.retrieveBan(user).queue(ban -> guild.retrieveAuditLogs().type(ActionType.BAN).queue(bans ->
		{
			final var banner = bans.get(0).getUser();
			final var eb = new EmbedBuilder();

			var reason = "";
			final var providedReason = ban.getReason();
			if (providedReason != null && banner.equals(e.getJDA().getSelfUser()))
				reason = (providedReason.equals("[Banned by Spidey#2370]") ? "Unknown" : providedReason.substring(24));
			else
				reason = (providedReason == null ? "Unknown" : providedReason);

			eb.setDescription(new StringBuilder().append(Emojis.CROSS).append(" **").append(escape(user.getAsTag()))
					.append("** (").append(user.getId()).append(") has been `banned` by ").append("**")
					.append(banner.getAsTag()).append("** for **").append(reason.trim()).append("**.").toString());
			eb.setColor(14495300);
			eb.setFooter("User ban", user.getEffectiveAvatarUrl());
			eb.setTimestamp(Instant.now());
			Utils.sendMessage(channel, eb.build());
		}));
	}

	@Override
	public final void onGuildUnban(final GuildUnbanEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();
		final var channel = Cache.getLogAsChannel(guild.getIdLong(), e.getJDA());

		if (channel == null || !guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS))
			return;
		guild.retrieveAuditLogs().type(ActionType.UNBAN).queue(unbans ->
		{
			final var sb = new StringBuilder().append(Emojis.CHECK).append(" **")
					.append(escape(user.getAsTag())).append("** (").append(user.getId()).append(") has been `unbanned`");
			final var eb = new EmbedBuilder();
			eb.setColor(7844437);
			eb.setFooter("User unban", user.getEffectiveAvatarUrl());
			eb.setTimestamp(Instant.now());

			if (unbans.isEmpty())
			{
				sb.append(".");
				eb.setDescription(sb.toString());
				Utils.sendMessage(channel, eb.build());
				return;
			}
			eb.setDescription(sb.append(" by **").append(unbans.get(0).getUser().getAsTag()).append("**.").toString());
			Utils.sendMessage(channel, eb.build());
		});
	}

	@Override
	public final void onGuildMemberRemove(final GuildMemberRemoveEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();
		final var channel = Cache.getLogAsChannel(guild.getIdLong(), e.getJDA());

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
			eb.setDescription("\uD83E\uDD16 Bot **" + escapedTag + "** (" + userId + ") has been removed from this server.");
			eb.setFooter("Bot remove", avatarUrl);
			Utils.sendMessage(channel, eb.build());
			return;
		}
		eb.setDescription("\uD83D\uDCE4 **" + escapedTag + "** (" + userId + ") has `left` the server.");
		eb.setFooter("User leave", avatarUrl);
		Utils.sendMessage(channel, eb.build());
	}

	@Override
	public final void onGuildMemberJoin(@NotNull final GuildMemberJoinEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();
		final var guildId = guild.getIdLong();
		final var jda = e.getJDA();
		final var channel = Cache.getLogAsChannel(guildId, jda);
		final var joinRole = Cache.getJoinRole(guildId, jda);
		final var userId = user.getId();
		final var selfMember = guild.getSelfMember();

		if (channel == null)
			return;
		if (joinRole != null && selfMember.canInteract(joinRole))
			guild.addRoleToMember(userId, joinRole).queue();

		final var escapedTag = escape(user.getAsTag());
		final var avatarUrl = user.getEffectiveAvatarUrl();
		final var eb = new EmbedBuilder();
		eb.setColor(7844437);
		eb.setTimestamp(Instant.now());
		if (user.isBot())
		{
			eb.setFooter("Bot add", avatarUrl);
			if (!selfMember.hasPermission(Permission.VIEW_AUDIT_LOGS))
			{
				eb.setDescription("\uD83E\uDD16 Bot **" + escapedTag + "** (" + userId + ") has been added to this server.");
				Utils.sendMessage(channel, eb.build());
				return;
			}
			guild.retrieveAuditLogs().type(ActionType.BOT_ADD).queue(botsAdded ->
			{
				final var last = botsAdded.get(0);
				eb.setDescription("\uD83E\uDD16 **" + escape(last.getUser().getAsTag()) + "** has added bot **" + escapedTag + "** (" + userId + ") to this server.");
				Utils.sendMessage(channel, eb.build());
			});
			return;
		}
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
				if (invite.getUses() > inviteData.getUses())
				{
					inviteData.incrementUses();
					eb.appendDescription(" with invite **" + invite.getUrl() + "** (**" + escape(invite.getInviter().getAsTag()) + "**).");
					Utils.sendMessage(channel, eb.build());
					break;
				}
			}
		});
	}

	@Override
	public final void onGuildReady(@NotNull final GuildReadyEvent e)
	{
		final var guild = e.getGuild();
		Utils.storeInvites(guild);
	}

	@Override
	public final void onGuildJoin(final GuildJoinEvent e)
	{
		final var guild = e.getGuild();
		final var defaultChannel = guild.getDefaultChannel();
		if (defaultChannel != null && defaultChannel.canTalk())
			Utils.sendMessage(defaultChannel, "Hey! I'm **Spidey**. Thanks for inviting me. To start, check `s!info`.");
		Utils.storeInvites(guild);
	}

	@Override
	public final void onGuildLeave(final GuildLeaveEvent e)
	{
		final var guildId = e.getGuild().getIdLong();
		Cache.getInviteCache().entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
		Cache.removeEntry(guildId);
	}

	@Override
	public final void onTextChannelDelete(final TextChannelDeleteEvent e)
	{
		final var guildId = e.getGuild().getIdLong();
		if (e.getChannel().getIdLong() == Cache.retrieveLogChannel(guildId))
			Cache.removeLogChannel(guildId);
	}

	@Override
	public final void onRoleDelete(final RoleDeleteEvent e)
	{
		final var guildId = e.getGuild().getIdLong();
		if (e.getRole().getIdLong() == Cache.retrieveJoinRole(guildId))
			Cache.removeJoinRole(guildId);
	}

	@Override
	public final void onGuildUpdateBoostTier(final GuildUpdateBoostTierEvent e)
	{
		final var guild = e.getGuild();
		final var channel = Cache.getLogAsChannel(guild.getIdLong(), e.getJDA());
		if (channel == null)
			return;
		final var eb = new EmbedBuilder();
		eb.setAuthor("GUILD BOOST TIER HAS CHANGED");
		eb.setColor(16023551);
		eb.setTimestamp(Instant.now());
		eb.addField("Boost tier", "**" + e.getNewBoostTier().getKey() + "**", true);
		eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
		Utils.sendMessage(channel, eb.build());
	}

	@Override
	public final void onGuildInviteCreate(@NotNull final GuildInviteCreateEvent e)
	{
		Cache.getInviteCache().put(e.getCode(), new InviteData(e.getInvite()));
	}

	@Override
	public final void onGuildInviteDelete(@NotNull final GuildInviteDeleteEvent e)
	{
		Cache.getInviteCache().remove(e.getCode());
	}

	@Override
	public void onReady(@NotNull final ReadyEvent e)
	{
		Utils.startup(e.getJDA());
	}

	@Override
	public void onGuildMessageDelete(@NotNull final GuildMessageDeleteEvent e)
	{
		Cache.setLastDeletedMessage(e.getChannel().getIdLong(), e.getMessageIdLong());
	}

	@Override
	public void onGuildMessageUpdate(@Nonnull final GuildMessageUpdateEvent event)
	{
		final var messageId = event.getMessageIdLong();
		Cache.cacheMessage(messageId, new MessageData(event.getMessage()));
		Cache.setLastEditedMessage(event.getChannel().getIdLong(), messageId);
	}
}
