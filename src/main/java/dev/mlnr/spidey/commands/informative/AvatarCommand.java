package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

@SuppressWarnings("unused")
public class AvatarCommand extends Command {

	public AvatarCommand() {
		super("avatar", new String[]{"pfp", "av"}, Category.INFORMATIVE, Permission.UNKNOWN, 1, 2);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		if (args.length == 0) {
			respond(ctx, ctx.getAuthor());
			return true;
		}
		ctx.getArgumentAsUser(0, user -> respond(ctx, user));
		return true;
	}

	private void respond(CommandContext ctx, User user) {
		var avatarUrl = user.getEffectiveAvatarUrl() + "?size=2048";
		var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		eb.setAuthor(ctx.getI18n().get("commands.avatar.other.title") + " " + MarkdownSanitizer.escape(user.getAsTag()));
		eb.setDescription("[Avatar link](" + avatarUrl + ")");
		eb.setImage(avatarUrl);
		ctx.reply(eb);
	}
}