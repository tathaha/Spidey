package dev.mlnr.spidey.commands.slash.informative;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import static dev.mlnr.spidey.objects.commands.CommandImplSubstitute.user;

@SuppressWarnings("unused")
public class UserSlashCommand extends SlashCommand {
	public UserSlashCommand() {
		super("user", "Shows info about you or entered user", Category.INFORMATIVE, Permission.UNKNOWN, 2,
				new OptionData(OptionType.USER, "user", "The user to get the info about"));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		var user = ctx.getUserOption("user");
		var author = ctx.getUser();
		if (user == null || user.equals(author)) {
			user(author, ctx.getMember(), ctx);
		}
		else {
			var member = ctx.getMemberOption("user");
			user(user, member, ctx);
		}
		return true;
	}
}