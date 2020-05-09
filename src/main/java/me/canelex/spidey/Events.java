package me.canelex.spidey;

import me.canelex.jda.api.EmbedBuilder;
import me.canelex.jda.api.Permission;
import me.canelex.jda.api.audit.ActionType;
import me.canelex.jda.api.entities.MessageType;
import me.canelex.jda.api.events.ShutdownEvent;
import me.canelex.jda.api.events.channel.text.TextChannelDeleteEvent;
import me.canelex.jda.api.events.guild.*;
import me.canelex.jda.api.events.guild.invite.GuildInviteCreateEvent;
import me.canelex.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import me.canelex.jda.api.events.guild.member.GuildMemberJoinEvent;
import me.canelex.jda.api.events.guild.member.GuildMemberRemoveEvent;
import me.canelex.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import me.canelex.jda.api.events.message.guild.GuildMessageReceivedEvent;
import me.canelex.jda.api.hooks.ListenerAdapter;
import me.canelex.jda.api.utils.MarkdownSanitizer;
import me.canelex.spidey.objects.command.CommandHandler;
import me.canelex.spidey.objects.invites.WrappedInvite;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@SuppressWarnings({"ConstantConditions", "StringBufferReplaceableByString"})
public class Events extends ListenerAdapter
{
	private static final ConcurrentMap<String, WrappedInvite> invitesMap = new ConcurrentHashMap<>();

	public static ConcurrentMap<String, WrappedInvite> getInvites()
	{
		return invitesMap;
	}

	@Override
	public final void onGuildMessageReceived(final GuildMessageReceivedEvent e)
	{
		final var guild = e.getGuild();
		final var message = e.getMessage();
		final var author = e.getAuthor();
		final var id = guild.getIdLong();
		final var prefix = Utils.getPrefix(id);

		if (message.getContentRaw().startsWith(prefix) && !author.isBot())
		{
			CommandHandler.handle(message, prefix);
			return;
		}

		final var channel = Utils.getLogChannel(id);
		if (message.getType() == MessageType.GUILD_MEMBER_BOOST && channel != null)
		{
			Utils.deleteMessage(message);
			final var eb = new EmbedBuilder();
			eb.setDescription(new StringBuilder().append(e.getJDA().getEmoteById(699731065052332123L).getAsMention())
					.append(" **").append(MarkdownSanitizer.escape(author.getAsTag())).append("** has `boosted` ")
					.append("the server. The server currently has **").append(guild.getBoostCount()).append("** boosts.").toString());
			eb.setAuthor("NEW BOOST");
			eb.setColor(16023551);
			eb.setFooter("User boost", author.getAvatarUrl());
			eb.setTimestamp(Instant.now());
			Utils.sendMessage(channel, eb.build());
		}
	}

	@Override
	public final void onGuildBan(final GuildBanEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();

		final var channel = Utils.getLogChannel(guild.getIdLong());
		if (channel != null)
		{
			guild.retrieveBan(user).queue(ban ->
				guild.retrieveAuditLogs().type(ActionType.BAN).queue(bans ->
				{
					final var banner = bans.get(0).getUser();
					final var eb = new EmbedBuilder();

					var reason = "";
					final var providedReason = ban.getReason();
					if (providedReason != null && banner.equals(e.getJDA().getSelfUser()))
						reason = (providedReason.equals("[Banned by Spidey#2370]") ?  "Unknown" : providedReason.substring(24));
					else
						reason = (providedReason == null ? "Unknown" : providedReason);

					eb.setDescription(new StringBuilder().append(Emojis.CROSS).append(" **").append(MarkdownSanitizer.escape(user.getAsTag()))
							.append("** (").append(user.getId()).append(") has been `banned` by ").append("**")
							.append(banner.getAsTag()).append("** for **").append(reason).append("**.").toString());
					eb.setColor(14495300);
					eb.setFooter("User ban", user.getAvatarUrl());
					eb.setTimestamp(Instant.now());
					Utils.sendMessage(channel, eb.build());
				}));
		}
	}

	@Override
	public final void onGuildUnban(final GuildUnbanEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();

		final var channel = Utils.getLogChannel(guild.getIdLong());
		if (channel != null)
		{
			guild.retrieveAuditLogs().type(ActionType.UNBAN).queue(unbans ->
			{
				final var eb = new EmbedBuilder();
				eb.setDescription(new StringBuilder().append(Emojis.CHECK).append(" **")
						.append(MarkdownSanitizer.escape(user.getAsTag())).append("** (").append(user.getId()).append(") has been `unbanned` ")
						.append("by **").append(unbans.get(0).getUser().getAsTag()).append("**.").toString());
				eb.setColor(7844437);
				eb.setFooter("User unban", user.getAvatarUrl());
				eb.setTimestamp(Instant.now());
				Utils.sendMessage(channel, eb.build());
			});
		}
	}

	@Override
	public final void onGuildMemberRemove(final GuildMemberRemoveEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();

		final var channel = Utils.getLogChannel(guild.getIdLong());
		if (channel != null)
		{
			final var eb = new EmbedBuilder();
			eb.setDescription(new StringBuilder().append("\uD83D\uDCE4 **").append(MarkdownSanitizer.escape(user.getAsTag()))
					.append("** (").append(user.getId()).append(") has `left` the server.").toString());
			eb.setColor(14495300);
			eb.setFooter("User leave", user.getAvatarUrl());
			eb.setTimestamp(Instant.now());
			Utils.sendMessage(channel, eb.build());
		}
	}

	@Override
	public final void onGuildMemberJoin(@NotNull final GuildMemberJoinEvent e)
	{
		final var user = e.getUser();
		final var guild = e.getGuild();
		final var id = guild.getIdLong();
		final var channel = Utils.getLogChannel(id);
		final var role = guild.getRoleById(MySQL.getRole(id));
		final var userId = user.getId();

		if (channel != null)
		{
			if (role != null)
			{
				final var selfMember = guild.getSelfMember();
				if (!selfMember.canInteract(role) || !Utils.hasPerm(selfMember, Permission.MANAGE_ROLES))
					Utils.sendMessage(channel, "I'm not able to add the joinrole to user **" + user.getAsTag() + "** as i don't have permissions to do so.");
				else
					guild.addRoleToMember(userId, role).queue();
			}

			final var eb = new EmbedBuilder();
			eb.setDescription("\uD83D\uDCE5 **" + MarkdownSanitizer.escape(user.getAsTag()) + "** (" + userId + ") has `joined` the server");
			eb.setColor(7844437);
			eb.setFooter("User join", user.getAvatarUrl());
			eb.setTimestamp(Instant.now());
			if (user.isBot())
			{
				eb.appendDescription(".");
				Utils.sendMessage(channel, eb.build());
				return;
			}
			guild.retrieveInvites().queue(invites ->
			{
				final var guildInvites = invitesMap.entrySet().stream().filter(entry -> entry.getValue().getGuildId() == id).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				for (final var invite : invites)
				{
					final var wrappedInvite = guildInvites.get(invite.getCode());
					if (invite.getUses() > wrappedInvite.getUses())
					{
						wrappedInvite.incrementUses();
						eb.appendDescription(" with invite **" + invite.getUrl() + "** (**" + invite.getInviter().getAsTag() + "**).");
						Utils.sendMessage(channel, eb.build());
						break;
					}
				}
			});
		}
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
		if (defaultChannel != null && defaultChannel.canTalk(guild.getSelfMember()))
			Utils.sendMessage(defaultChannel, "Hey! I'm **Spidey**. Thanks for inviting me. To start, check `s!help`.");
		Utils.storeInvites(guild);
	}

	@Override
	public final void onGuildLeave(final GuildLeaveEvent e)
	{
		final var guild = e.getGuild();
		final var id = guild.getIdLong();
		invitesMap.entrySet().removeIf(entry -> entry.getValue().getGuildId() == id);
		MySQL.removeChannel(id);
	}

	@Override
	public final void onTextChannelDelete(final TextChannelDeleteEvent e)
	{
		final var id = e.getGuild().getIdLong();
		if (e.getChannel().getIdLong() == MySQL.getChannel(id))
			MySQL.removeChannel(id);
	}

	@Override
	public final void onGuildUpdateBoostTier(final GuildUpdateBoostTierEvent e)
	{
		final var guild = e.getGuild();

		final var channel = Utils.getLogChannel(guild.getIdLong());
		if (channel != null)
		{
			final var eb = new EmbedBuilder();
			eb.setAuthor("GUILD BOOST TIER HAS CHANGED");
			eb.setColor(16023551);
			eb.setTimestamp(Instant.now());
			eb.addField("Boost tier", "**" + e.getNewBoostTier().getKey() + "**", true);
			eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
			Utils.sendMessage(channel, eb.build());
		}
	}

	@Override
	public final void onGuildInviteCreate(@NotNull final GuildInviteCreateEvent e)
	{
		invitesMap.put(e.getCode(), new WrappedInvite(e.getInvite()));
	}

	@Override
	public final void onGuildInviteDelete(@NotNull final GuildInviteDeleteEvent e)
	{
		invitesMap.remove(e.getCode());
	}

	@Override
	public final void onShutdown(@NotNull final ShutdownEvent e)
	{
		invitesMap.clear();
	}
}