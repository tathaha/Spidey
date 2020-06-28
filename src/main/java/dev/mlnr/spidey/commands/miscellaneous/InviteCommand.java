package dev.mlnr.spidey.commands.miscellaneous;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

@SuppressWarnings("unused")
public class InviteCommand extends Command
{
    public InviteCommand()
    {
        super("invite", new String[]{}, "Sends you Spidey's invite link to PM", "invite", Category.MISC, Permission.UNKNOWN, 0, 0);
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