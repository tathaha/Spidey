package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class InfoCommand extends Command {

	public InfoCommand() {
		super("info", new String[]{}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		var avatar = ctx.getJDA().getSelfUser().getEffectiveAvatarUrl();
		var i18n = ctx.getI18n();

		eb.setAuthor("Spidey", null, avatar);
		eb.setThumbnail(avatar);
		eb.addField(i18n.get("commands.info.fields.about.title"), i18n.get("commands.info.fields.about.text", "cane#0570"), false);
		eb.addField(i18n.get("commands.info.fields.commands.title"),
				i18n.get("commands.info.fields.commands.text", ctx.getCache().getGuildSettingsCache().getPrefix(ctx.getGuild().getIdLong())), false);
		eb.addField(i18n.get("commands.info.fields.info.title"), i18n.get("commands.info.fields.info.text", JDAInfo.VERSION), false);
		eb.addField(i18n.get("commands.info.fields.links"), "[`Website`](https://spidey.mlnr.dev)\n[`Discord`](https://discord.gg/uJCw7B9fxZ)" +
				"\n[`GitHub`](https://github.com/caneleex/Spidey)", false);
		eb.addField(i18n.get("commands.info.fields.support.title"), i18n.get("commands.info.fields.support.text"), false);
		ctx.reply(eb);
	}
}