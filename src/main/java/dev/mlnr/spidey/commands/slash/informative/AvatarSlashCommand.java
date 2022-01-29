package dev.mlnr.spidey.commands.slash.informative;

import dev.mlnr.spidey.objects.commands.CommandImplSubstitute;
import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class AvatarSlashCommand extends SlashCommand {
	public AvatarSlashCommand() {
		super("avatar", "Shows your/entered user's avatar", Category.INFORMATIVE, Permission.UNKNOWN, 2,
				new OptionData(OptionType.USER, "user", "The user to get the avatar of"),
				new OptionData(OptionType.BOOLEAN, "show-server-avatar", "Whether to display the avatar specific for this server if possible"));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		var author = ctx.getUser();
		var userOption = ctx.getUserOption("user");
		var user = userOption == null ? author : userOption;
		var avatarUrl = user.getEffectiveAvatarUrl();
		var supportsServerAvatar = ctx.getBooleanOption("show-server-avatar");

		if (supportsServerAvatar != null && supportsServerAvatar) {
			var memberOption = ctx.getMemberOption("user");
			if (memberOption != null) {
				avatarUrl = memberOption.getEffectiveAvatarUrl();
			}
		}
		CommandImplSubstitute.avatar(user, avatarUrl, ctx);
		return true;
	}
}