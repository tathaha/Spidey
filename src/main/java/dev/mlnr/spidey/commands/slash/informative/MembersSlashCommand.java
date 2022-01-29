package dev.mlnr.spidey.commands.slash.informative;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.time.Instant;

@SuppressWarnings("unused")
public class MembersSlashCommand extends SlashCommand {
	public MembersSlashCommand() {
		super("members", "Shows you the membercount of the server", Category.INFORMATIVE, Permission.UNKNOWN, 2);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		ctx.getGuild().loadMembers().onSuccess(members -> {
			var total = members.size();
			var bots = members.stream().filter(member -> member.getUser().isBot()).count();
			var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
			var i18n = ctx.getI18n();

			embedBuilder.setAuthor(i18n.get("commands.members.title"));
			embedBuilder.setTimestamp(Instant.now());
			embedBuilder.addField(i18n.get("commands.members.total"), "**" + total + "**", true);
			embedBuilder.addField(i18n.get("commands.members.people"), "**" + (total - bots) + "**", true);
			embedBuilder.addField(i18n.get("commands.members.bots"), "**" + bots + "**", true);
			ctx.reply(embedBuilder);
		});
		return true;
	}
}