package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

@SuppressWarnings("unused")
public class AvatarCommand extends Command {
	public AvatarCommand() {
		super("avatar", Category.INFORMATIVE, Permission.UNKNOWN, 2,
				new OptionData(OptionType.USER, "user", "The user to get the avatar of"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var userOption = ctx.getUserOption("user");
		var author = ctx.getUser();
		var user = userOption == null ? author : userOption;
		var avatarUrl = user.getEffectiveAvatarUrl() + "?size=2048";
		var eb = Utils.createEmbedBuilder(author);

		eb.setAuthor(ctx.getI18n().get("commands.avatar.other.title") + " " + MarkdownSanitizer.escape(user.getAsTag()));
		eb.setDescription("[Avatar link](" + avatarUrl + ")");
		eb.setImage(avatarUrl);
		ctx.reply(eb);
		return true;
	}
}