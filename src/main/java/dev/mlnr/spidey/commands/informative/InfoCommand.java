package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class InfoCommand extends Command
{
	public InfoCommand()
	{
		super("info", new String[]{}, "Shows you info about me", "info", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		final var avatar = message.getJDA().getSelfUser().getAvatarUrl();
		eb.setAuthor("Spidey", null, avatar);
		eb.setColor(0xFEFEFE);
		eb.setThumbnail(avatar);
		eb.addField("About me", "Hey, i'm Spidey. I was made by `cane#6666`.", false);
		eb.addField("Commands", "Type `" + Cache.retrievePrefix(message.getGuild().getIdLong()) + "help` for a list of commands.", false);
		eb.addField("Bot info", "[Library](https://github.com/DV8FromTheWorld/JDA) version: `" + JDAInfo.VERSION + "`", false);
		eb.addField("Links", "[`Website`](https://spidey.mlnr.dev)\n[`Discord`](https://discord.gg/cnAgKrv)" +
				"\n[`GitHub`](https://github.com/caneleex/Spidey)\n[`Twitter`](https://twitter.com/SpideyTheBot)", false);
		eb.addField("Support", "If you want to support the development of Spidey, you can do so by donating using my [PayPal](https://paypal.me/caneleex). Thank you!", false);
		Utils.sendMessage(message.getTextChannel(), eb.build());
	}
}