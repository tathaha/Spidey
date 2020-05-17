package me.canelex.spidey.commands.miscellaneous;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;

import java.time.Duration;

@SuppressWarnings("unused")
public class InviteCommand extends Command
{
    public InviteCommand()
    {
        super("invite", new String[]{}, "Sends you Spidey's invite link to PM", "invite", Category.MISC, Permission.UNKNOWN, 0);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        Utils.sendPrivateMessageFormat(message.getAuthor(), "Link for inviting me: ||" + Utils.getInviteUrl() + "||");
        message.getChannel().sendMessage(":white_check_mark: I've sent you the invite link.")
                            .delay(Duration.ofSeconds(5))
                            .flatMap(Message::delete)
                            .flatMap(ignored -> message.delete())
                            .queue();
    }
}