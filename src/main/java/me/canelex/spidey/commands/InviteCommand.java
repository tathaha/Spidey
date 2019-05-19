package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class InviteCommand implements ICommand {

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
    public final boolean isAdmin() {
        return false;
    }

}