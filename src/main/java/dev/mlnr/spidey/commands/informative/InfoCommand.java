package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@SuppressWarnings("unused")
public class InfoCommand extends Command {
	private static final String INVITE_URL = "https://discord.com/oauth2/authorize?client_id=772446532560486410&permissions=1100857043105&scope=bot+applications.commands";

	public InfoCommand() {
		super("info", "Shows you info about me", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var avatar = ctx.getJDA().getSelfUser().getEffectiveAvatarUrl();
		var i18n = ctx.getI18n();

		embedBuilder.setAuthor("Spidey", null, avatar);
		embedBuilder.setThumbnail(avatar);
		embedBuilder.addField(i18n.get("commands.info.fields.about.title"), i18n.get("commands.info.fields.about.text", "cane#0101"), false);
		embedBuilder.addField(i18n.get("commands.info.fields.commands.title"), i18n.get("commands.info.fields.commands.text"), false);
		embedBuilder.addField(i18n.get("commands.info.fields.info.title"), i18n.get("commands.info.fields.info.text", JDAInfo.VERSION), false);
		embedBuilder.addField(i18n.get("commands.info.fields.links"), "[`Website`](https://spidey.mlnr.dev)\n[`Discord`](https://discord.gg/uJCw7B9fxZ)" +
				"\n[`GitHub`](https://github.com/caneleex/Spidey)", false);
		embedBuilder.addField(i18n.get("commands.info.fields.translate.title"), i18n.get("commands.info.fields.translate.text"), false);
		embedBuilder.addField(i18n.get("commands.info.fields.support.title"), i18n.get("commands.info.fields.support.text"), false);

		var inviteButton = Button.link(INVITE_URL, i18n.get("commands.info.invite"));
		ctx.replyWithComponents(embedBuilder, inviteButton);
		return true;
	}
}
