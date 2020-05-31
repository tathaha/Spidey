package me.canelex.spidey.commands.informative;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

@SuppressWarnings("unused")
public class InfoCommand extends Command
{
	public InfoCommand()
	{
		super("info", new String[]{}, "Shows you info about me", "info", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		final var avatar = message.getJDA().getSelfUser().getAvatarUrl();
		eb.setAuthor("Spidey", null, avatar);
		eb.setColor(Color.WHITE);
		eb.setThumbnail(avatar);
		eb.addField("About me", "Hey, i'm Spidey. I was made by `/home/canelex_#6666`.", false);
		eb.addField("Commands", "Use `" + Utils.getPrefix(message.getGuild().getIdLong()) + "help` for a list of commands.", false);
		eb.addField("Bot info", "[Library](https://github.com/DV8FromTheWorld/JDA) version: `" + JDAInfo.VERSION + "`", false);
		eb.addField("Links", "[`Discord`](https://discord.gg/cnAgKrv)\n[`GitHub`](https://github.com/caneleex/Spidey)", false);
		eb.addField("Support", "If you want to support the development of Spidey, you can do so by donating using my [PayPal](https://paypal.me/caneleex). Thank you!", false);
		Utils.sendMessage(message.getChannel(), eb.build());
	}
}