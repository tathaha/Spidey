package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class StatsCommand extends CommandBase {
	public StatsCommand() {
		super("stats", "Shows you Spidey's stats", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var jda = ctx.getJDA();
		var runtime = Runtime.getRuntime();
		var total = runtime.totalMemory();
		var memory = (total - runtime.freeMemory()) / 1000000;
		var i18n = ctx.getI18n();

		embedBuilder.setColor(Utils.SPIDEY_COLOR);
		embedBuilder.setAuthor(i18n.get("commands.stats.title"), "https://spidey.mlnr.dev", jda.getSelfUser().getEffectiveAvatarUrl());
		embedBuilder.setDescription(i18n.get("commands.stats.more"));
		embedBuilder.addField(i18n.get("commands.stats.total_servers"), String.valueOf(jda.getGuildCache().size()), true);
		embedBuilder.addField(i18n.get("commands.stats.memory"), memory + "MB / " + (total / 100000) + "MB", true);
		embedBuilder.addField(i18n.get("commands.stats.threads"), String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount()), true);
		embedBuilder.setFooter("spidey.mlnr.dev");
		ctx.reply(embedBuilder);
		return true;
	}
}