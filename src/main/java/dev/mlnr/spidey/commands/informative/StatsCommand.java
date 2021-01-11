package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class StatsCommand extends Command
{
	public StatsCommand()
	{
		super("stats", new String[]{}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(final String[] args, final CommandContext ctx)
	{
		final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		final var prefix = GuildSettingsCache.getPrefix(ctx.getGuild().getIdLong());
		final var jda = ctx.getJDA();
		final var runtime = Runtime.getRuntime();
		final var total = runtime.totalMemory();
		final var memory = (total - runtime.freeMemory()) / 1000000;
		final var i18n = ctx.getI18n();

		eb.setColor(Utils.SPIDEY_COLOR);
		eb.setAuthor(i18n.get("commands.stats.other.title"), "https://spidey.mlnr.dev", jda.getSelfUser().getEffectiveAvatarUrl());
		eb.setDescription(i18n.get("commands.stats.other.more", prefix));
		eb.addField(i18n.get("commands.stats.other.total_servers"), String.valueOf(jda.getGuildCache().size()), true);
		eb.addField(i18n.get("commands.stats.other.memory"), memory + "MB / " + (total / 100000) + "MB", true);
		eb.addField(i18n.get("commands.stats.other.threads"), String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount()), true);
		eb.setFooter("spidey.mlnr.dev");
		ctx.reply(eb);
	}
}