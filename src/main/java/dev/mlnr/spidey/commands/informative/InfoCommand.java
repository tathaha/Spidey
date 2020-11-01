package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.cache.PrefixCache;
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
	public void execute(final String[] args, final Message msg)
	{
		final var eb = Utils.createEmbedBuilder(msg.getAuthor());
		final var avatar = msg.getJDA().getSelfUser().getEffectiveAvatarUrl();
		eb.setAuthor("Spidey", null, avatar);
		eb.setColor(0xFEFEFE);
		eb.setThumbnail(avatar);
		eb.addField("About me", "Hey, i'm Spidey. I was made by `cane#6666`.", false);
		eb.addField("Commands", "Type `" + PrefixCache.retrievePrefix(msg.getGuild().getIdLong()) + "help` for a list of commands.", false);
		eb.addField("Bot info", "[Library](https://github.com/DV8FromTheWorld/JDA) version: `" + JDAInfo.VERSION + "`", false);
		eb.addField("Links", "[`Discord`](https://discord.gg/VQk2BUCSqM)" +
				"\n[`GitHub`](https://github.com/caneleex/Spidey)", false);
		eb.addField("Support", "If you want to support the development of Spidey, you can do so by donating using my [PayPal](https://paypal.me/caneleex). Thank you!", false);
		Utils.sendMessage(msg.getTextChannel(), eb.build());
	}
}