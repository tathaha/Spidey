package me.canelex.spidey.commands.miscellaneous;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

@SuppressWarnings("unused")
public class InviteCommand implements ICommand
{
    @Override
    public final void action(final String[] args, final Message message)
    {
        Utils.sendPrivateMessageFormat(message.getAuthor(), "Link for inviting me: ||" + Utils.getInviteUrl() + "||");
        message.getChannel().sendMessage(":white_check_mark: Sent you an invite link.")
                            .delay(Duration.ofSeconds(5))
                            .flatMap(Message::delete)
                            .flatMap(ignored -> message.delete())
                            .queue();
    }

    @Override
    public final String getDescription() { return "Sends you Spidey's invite link to PM"; }
    @Override
    public final String getInvoke() { return "invite"; }
    @Override
    public final Category getCategory() { return Category.MISC; }
    @Override
    public final String getUsage() { return "s!invite"; }
}