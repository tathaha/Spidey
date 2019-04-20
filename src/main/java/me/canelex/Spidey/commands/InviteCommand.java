package me.canelex.Spidey.commands;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class InviteCommand implements ICommand {

    @Override
    public final boolean called(final GuildMessageReceivedEvent e) {

        return true;

    }

    @Override
    public final void action(final GuildMessageReceivedEvent e) {

        e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
        API.sendPrivateMessageFormat(e.getAuthor(), "Link for inviting me: ||" + API.getInviteUrl() + "||");
        e.getChannel().sendMessage(":white_check_mark: Sent you an invite link.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));

    }

    @Override
    public final String help() {

        return "Sends you Spidey's invite link to PM";

    }

    @Override
    public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}