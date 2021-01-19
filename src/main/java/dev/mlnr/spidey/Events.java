package dev.mlnr.spidey;

import dev.mlnr.spidey.cache.*;
import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.handlers.akinator.AkinatorHandler;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.guild.InviteData;
import dev.mlnr.spidey.objects.messages.MessageData;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

import static net.dv8tion.jda.api.utils.MarkdownSanitizer.escape;

public class Events extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        var guild = event.getGuild();
        var message = event.getMessage();
        var guildId = guild.getIdLong();
        var content = message.getContentRaw().trim();

        if (!content.isEmpty() && GuildSettingsCache.isSnipingEnabled(guildId))
            MessageCache.cacheMessage(message.getIdLong(), new MessageData(message));

        var author = event.getAuthor();

        if (AkinatorCache.hasAkinator(author.getIdLong()))
        {
            AkinatorHandler.handle(author, new AkinatorContext(event));
            return;
        }

        var prefix = GuildSettingsCache.getPrefix(guildId);
        if (!content.startsWith(prefix) || author.isBot() || event.isWebhookMessage())
            return;
        CommandHandler.handle(event, prefix);
    }

    @Override
    public void onGuildBan(GuildBanEvent event)
    {
        var guild = event.getGuild();
        var channel = GuildSettingsCache.getLogChannel(guild.getIdLong());
        if (channel == null)
            return;
        var user = event.getUser();
        var escapedTag = escape(user.getAsTag());
        var eb = new EmbedBuilder();
        eb.setDescription("\uD83D\uDD28 **" + escapedTag + "** has been `banned`");
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
            var last = bans.get(0);
            var bannerTag = escape(last.getUser().getAsTag());
            var reason = last.getReason();
            reason = reason == null || reason.isEmpty() ? "unknown reason" : reason.trim();
            eb.appendDescription(" by **" + bannerTag + "** for **" + reason + "**.");
            Utils.sendMessage(channel, eb.build());
        });
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event)
    {
        var guild = event.getGuild();
        var channel = GuildSettingsCache.getLogChannel(guild.getIdLong());
        if (channel == null)
            return;
        var user = event.getUser();
        var escapedTag = escape(user.getAsTag());
        var eb = new EmbedBuilder();
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
            var last = unbans.get(0);
            var unbannerTag = escape(last.getUser().getAsTag());
            eb.appendDescription(" by **" + unbannerTag + "**.");
            Utils.sendMessage(channel, eb.build());
        });
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event)
    {
        var channel = GuildSettingsCache.getLogChannel(event.getGuild().getIdLong());
        if (channel == null)
            return;
        var user = event.getUser();
        var escapedTag = escape(user.getAsTag());
        var avatarUrl = user.getEffectiveAvatarUrl();
        var userId = user.getIdLong();
        var eb = new EmbedBuilder();
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
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        var guild = event.getGuild();
        var guildId = guild.getIdLong();
        var channel = GuildSettingsCache.getLogChannel(guildId);
        if (channel == null)
            return;
        var joinRole = GuildSettingsCache.getJoinRole(guildId);
        var selfMember = guild.getSelfMember();
        var user = event.getUser();
        var userId = user.getIdLong();

        if (joinRole != null && selfMember.canInteract(joinRole) && selfMember.hasPermission(Permission.MANAGE_ROLES))
            guild.addRoleToMember(userId, joinRole).queue();

        var escapedTag = escape(user.getAsTag());
        var avatarUrl = user.getEffectiveAvatarUrl();
        var eb = new EmbedBuilder();
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
                var last = botsAdded.get(0);
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
            for (var invite : invites)
            {
                var inviteData = GeneralCache.getInviteCache().get(invite.getCode());
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
    public void onGuildReady(GuildReadyEvent event)
    {
        var guild = event.getGuild();
        Utils.storeInvites(guild);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        var guild = event.getGuild();
        var guildId = guild.getIdLong();
        var defaultChannel = guild.getDefaultChannel();
        var jda = event.getJDA();
        if (defaultChannel != null)
            Utils.sendMessage(defaultChannel, "Hey! I'm **Spidey**. Thanks for inviting me. To start, check `s!info`.");
        Utils.storeInvites(guild);
        Requester.updateStats(jda);
        Utils.sendMessage(jda.getTextChannelById(785630223785787452L), "I've joined guild **" + guild.getName() + "** (**" + guildId + "**) with **" + guild.getMemberCount() + "** members");
        guild.findMembers(member -> !member.getUser().isBot()).onSuccess(people ->
        {
            if (people.size() >= 10000)
                GuildSettingsCache.setSnipingEnabled(guildId, false);
        });
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event)
    {
        var guild = event.getGuild();
        var guildId = guild.getIdLong();
        var jda = event.getJDA();
        GeneralCache.getInviteCache().entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
        MessageCache.pruneCache(guildId);
        MusicPlayerCache.destroyMusicPlayer(guild);
        GeneralCache.removeGuild(guildId);
        Requester.updateStats(jda);
        Utils.sendMessage(jda.getTextChannelById(785630223785787452L), "I've been kicked out of guild **" + guild.getName() + "** (**" + guildId + "**) with **" + guild.getMemberCount() + "** members");
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event)
    {
        var guildId = event.getGuild().getIdLong();
        if (event.getChannel().getIdLong() == GuildSettingsCache.getLogChannelId(guildId))
            GuildSettingsCache.removeLogChannel(guildId);
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event)
    {
        var roleId = event.getRole().getIdLong();
        var guildId = event.getGuild().getIdLong();
        if (roleId == GuildSettingsCache.getJoinRoleId(guildId))
            GuildSettingsCache.removeJoinRole(guildId);
        if (roleId == GuildSettingsCache.getDJRoleId(guildId))
            GuildSettingsCache.removeDJRole(guildId);
    }

    @Override
    public void onGuildUpdateBoostTier(GuildUpdateBoostTierEvent event)
    {
        var guild = event.getGuild();
        var channel = GuildSettingsCache.getLogChannel(guild.getIdLong());
        if (channel == null)
            return;
        var eb = new EmbedBuilder();
        eb.setAuthor("GUILD BOOST TIER HAS CHANGED");
        eb.setColor(16023551);
        eb.setTimestamp(Instant.now());
        eb.addField("Boost tier", "**" + event.getNewBoostTier().getKey() + "**", true);
        eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
        Utils.sendMessage(channel, eb.build());
    }

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event)
    {
        GeneralCache.getInviteCache().put(event.getCode(), new InviteData(event.getInvite()));
    }

    @Override
    public void onGuildInviteDelete(GuildInviteDeleteEvent event)
    {
        GeneralCache.getInviteCache().remove(event.getCode());
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        var jda = event.getJDA();
        jda.getPresence().setActivity(Activity.listening("s!help"));
        if (jda.getSelfUser().getIdLong() == 772446532560486410L) // only update stats if it's the production bot
            Requester.updateStats(jda);
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event)
    {
        var messageId = event.getMessageIdLong();
        var responseMessageId = ResponseCache.getResponseMessageId(messageId);
        var channel = event.getChannel();
        if (responseMessageId != null)
        {
            channel.deleteMessageById(responseMessageId).queue();
            ResponseCache.removeResponseMessageId(messageId);
        }
        else if (PaginatorCache.isPaginator(messageId))
            PaginatorCache.removePaginator(messageId);

        if (!GuildSettingsCache.isSnipingEnabled(event.getGuild().getIdLong()))
            return;
        if (!MessageCache.isCached(messageId))
            return;
        MessageCache.setLastDeletedMessage(channel.getIdLong(), messageId);
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event)
    {
        if (!GuildSettingsCache.isSnipingEnabled(event.getGuild().getIdLong()))
            return;
        var messageId = event.getMessageIdLong();
        if (!MessageCache.isCached(messageId))
            return;
        MessageCache.cacheMessage(messageId, new MessageData(event.getMessage()));
        MessageCache.setLastEditedMessage(event.getChannel().getIdLong(), messageId);
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event)
    {
        var member = event.getEntity();
        var guild = member.getGuild();
        var connectedChannel = MusicUtils.getConnectedChannel(guild);
        var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);

        // "join"
        if (event instanceof GuildVoiceJoinEvent || event instanceof GuildVoiceMoveEvent)
        {
            if (!member.getUser().isBot() && event.getChannelJoined().equals(connectedChannel))
            {
                musicPlayer.cancelLeave();
                musicPlayer.unpause();
            }
            return;
        }
        // "leave"
        if (member.getIdLong() == event.getJDA().getSelfUser().getIdLong())
        {
            MusicPlayerCache.destroyMusicPlayer(guild);
            return;
        }
        if (event.getChannelLeft().equals(connectedChannel) && connectedChannel.getMembers().stream().allMatch(connectedMember -> connectedMember.getUser().isBot()))
        {
            musicPlayer.scheduleLeave();
            musicPlayer.pause();
        }
    }

    @Override
    public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event)
    {
        var messageId = event.getMessageIdLong();
        if (!PaginatorCache.isPaginator(messageId))
            return;
        var paginator = PaginatorCache.getPaginator(messageId);
        if (event.getUserIdLong() != paginator.getAuthorId())
            return;
        var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.isEmoji())
            return;
        var emoji = reactionEmote.getEmoji();
        var channel = event.getChannel();
        var currentPage = paginator.getCurrentPage();
        var pagesConsumer = paginator.getPagesConsumer();
        var newPageBuilder = MusicUtils.createMusicResponseBuilder();
        var totalPages = paginator.getTotalPages();

        switch (emoji)
        {
            case Emojis.WASTEBASKET:
                PaginatorCache.removePaginator(messageId);
                return;
            case Emojis.BACKWARDS:
                if (currentPage == 0)
                    return;
                var previousPage = currentPage - 1;
                pagesConsumer.accept(previousPage, newPageBuilder);
                newPageBuilder.setFooter("Page " + (previousPage + 1) + "/" + totalPages);
                paginator.modifyCurrentPage(-1);
                break;
            case Emojis.FORWARD:
                if (currentPage + 1 == totalPages)
                    return;
                var nextPage = currentPage + 1;
                pagesConsumer.accept(nextPage, newPageBuilder);
                newPageBuilder.setFooter("Page " + (nextPage + 1) + "/" + totalPages);
                paginator.modifyCurrentPage(+1);
                break;
            default:
                return;
        }
        channel.editMessageById(messageId, newPageBuilder.build()).queue();
    }
}