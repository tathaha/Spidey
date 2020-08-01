package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.cache.PrefixCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class StatsCommand extends Command
{
	public StatsCommand()
	{
		super("stats", new String[]{}, "Shows you Spidey's stats", "stats", Category.INFORMATIVE, Permission.UNKNOWN, 0, 3);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		final var prefix = PrefixCache.retrievePrefix(message.getGuild().getIdLong());
		final var jda = message.getJDA();
		final var runtime = Runtime.getRuntime();
		final var total = runtime.totalMemory();
		final var memory = (total - runtime.freeMemory()) / 1000000;
		eb.setColor(9590015);
		eb.setAuthor("Spidey's stats", "https://spidey.mlnr.dev", jda.getSelfUser().getEffectiveAvatarUrl());
		eb.setDescription("For more info about Spidey, type " + prefix + "info");
		eb.addField("Total servers", "" + jda.getGuildCache().size(), true);
		eb.addField("Total members", "" + jda.getUserCache().size(), true);
		eb.addField("Memory usage", memory + "MB / " + (total / 100000) + "MB", true);
		eb.addField("Thread count", "" + ManagementFactory.getThreadMXBean().getThreadCount(), true);
		eb.setFooter("spidey.mlnr.dev");
		Utils.sendMessage(message.getTextChannel(), eb.build());
	}
}