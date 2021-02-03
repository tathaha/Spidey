package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ThreadLocalRandom;

import static dev.mlnr.spidey.utils.Utils.getColorHex;

@SuppressWarnings("unused")
public class HowGayCommand extends Command {

	public HowGayCommand() {
		super("howgay", new String[]{"gay"}, Category.FUN, Permission.UNKNOWN, 1, 2);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		if (args.length == 0) {
			respond(ctx, ctx.getAuthor());
			return;
		}
		ctx.getArgumentAsUser(0, user -> respond(ctx, user));
	}

	private void respond(CommandContext ctx, User user) {
		var prideFlag = "\uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08";
		var random = ThreadLocalRandom.current().nextInt(0, 100 + 1); // values from 0 to 100, 100 + 1 cos 100 has to be inclusive
		var text = " **" + random + "**% gay " + prideFlag;
		var author = ctx.getAuthor();
		var eb = Utils.createEmbedBuilder(author);
		var i18n = ctx.getI18n();
		eb.setAuthor(i18n.get("commands.howgay.other.title"));
		eb.setColor(getColorHex(random, 100));
		eb.setDescription((user.equals(author)
				? i18n.get("commands.howgay.other.person.second")
				: user.getAsMention() + " " + i18n.get("commands.howgay.other.person.third")) + text);
		ctx.reply(eb);
	}
}