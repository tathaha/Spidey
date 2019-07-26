package me.canelex.spidey.commands;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.PermissionError;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "ConstantConditions"})
public class LogCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final var neededPerm = "ADMINISTRATOR";

		if (e.getMember() != null && Utils.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {

			Utils.deleteMessage(e.getMessage());

			if (e.getGuild().getSystemChannel() != null) {

				e.getGuild().getManager().setSystemChannel(null).queue();

			}

			if (MySQL.getChannelId(e.getGuild().getIdLong()) != null) {

				MySQL.insertData(e.getGuild().getIdLong(), e.getChannel().getIdLong());
				e.getChannel().sendMessage(":white_check_mark: Log channel set to " + e.getChannel().getAsMention() + ". Type this command again to set log channel to default guild channel.").queue(m -> m.delete().queueAfter(5,  TimeUnit.SECONDS));

			}

			else {

				if (MySQL.getChannelId(e.getGuild().getIdLong()) == e.getChannel().getIdLong()) {

					MySQL.removeData(e.getGuild().getIdLong());
					MySQL.insertData(e.getGuild().getIdLong(), Objects.requireNonNull(e.getGuild().getDefaultChannel()).getIdLong());
					e.getChannel().sendMessage(":white_check_mark: Log channel set to " + e.getGuild().getDefaultChannel().getAsMention() + ". Type this command again in channel you want to be as log channel.").queue(m -> m.delete().queueAfter(5,  TimeUnit.SECONDS));

				}

				else {

					MySQL.removeData(e.getGuild().getIdLong());
					MySQL.insertData(e.getGuild().getIdLong(), e.getChannel().getIdLong());
					e.getChannel().sendMessage(":white_check_mark: Log channel set to " + e.getChannel().getAsMention() + ". Type this command again to set log channel to default guild channel.").queue(m -> m.delete().queueAfter(5,  TimeUnit.SECONDS));

				}

			}

		}

		else {

			Utils.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);

		}

	}

	@Override
	public final String getDescription() { return "Sets log channel"; }
	@Override
	public final boolean isAdmin() { return true; }
	@Override
	public final String getInvoke() { return "log"; }
	@Override
	public final Category getCategory() { return Category.MODERATION; }
	@Override
	public final String getUsage() { return "s!log"; }

}