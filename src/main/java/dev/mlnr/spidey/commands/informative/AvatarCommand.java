package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

@SuppressWarnings("unused")
public class AvatarCommand extends CommandBase {
	public AvatarCommand() {
		super("avatar", "Shows your/entered user's avatar", Category.INFORMATIVE, Permission.UNKNOWN, 2,
				new OptionData(OptionType.USER, "user", "The user to get the avatar of"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var userOption = ctx.getUserOption("user");
		var author = ctx.getUser();
		var user = userOption == null ? author : userOption;
		var avatarUrl = user.getEffectiveAvatarUrl() + "?size=2048";
		var embedBuilder = Utils.createEmbedBuilder(author);

		embedBuilder.setAuthor(ctx.getI18n().get("commands.avatar.title") + " " + MarkdownSanitizer.escape(user.getAsTag()));
		embedBuilder.setDescription("[Avatar link](" + avatarUrl + ")");
		embedBuilder.setImage(avatarUrl);
		ctx.reply(embedBuilder);
		return true;
	}
}