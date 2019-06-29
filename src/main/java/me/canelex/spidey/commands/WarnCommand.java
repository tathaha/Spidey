package me.canelex.spidey.commands;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.category.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.PermissionError;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "ConstantConditions"})
public class WarnCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final int maxArgs = 3;
		final Message msg = e.getMessage();

		e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);

		if (e.getMember() != null && !Utils.hasPerm(e.getMember(), Permission.BAN_MEMBERS))  {
			Utils.sendMessage(e.getChannel(), PermissionError.getErrorMessage("BAN_MEMBERS"), false);
			return;
		}

		final String[] args = msg.getContentRaw().trim().split("\\s+", maxArgs);

		if (args.length < 3) {
			Utils.returnError("Wrong syntax", msg);
			return;
		}

		if (!args[1].matches(Message.MentionType.USER.getPattern().pattern())) {
			Utils.returnError("Wrong syntax (no mention)", msg);
			return;
		}

		List<Member> members = msg.getMentionedMembers();

		if (members.isEmpty()) {
			Utils.returnError("User wasn't found", msg);
			return;
		}

		final Member mb = members.get(0);

		if (!e.getMember().canInteract(mb)) {
			Utils.returnError("Can't warn the user due to permission hierarchy position", msg);
			return;
		}

		final EmbedBuilder eb = new EmbedBuilder();
		final Guild guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {
			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			eb.setAuthor("NEW WARN");
			eb.setThumbnail(mb.getUser().getEffectiveAvatarUrl());
			eb.addField("User", "**" + mb.getUser().getAsTag() + "**", true);
			eb.addField("ID", "**" + mb.getUser().getId() + "**", true);
			eb.addField("Moderator", "**" + e.getAuthor().getAsTag() + "**", true);
			eb.addField("Reason", "**" + args[2] + "**", true);
			eb.setColor(Color.ORANGE);
			assert log != null;
			Utils.sendMessage(log, eb.build());
			Utils.sendPrivateMessageFormat(mb.getUser(), ":warning: You've been warned on the guild **%s** from **%s** for **%s**.", guild.getName(), e.getAuthor().getName(), args[2]);
		}

	}

	@Override
	public final String getDescription() { return "Warns user"; }
	@Override
	public final boolean isAdmin() { return true; }
	@Override
	public final String getInvoke() { return "warn"; }
	@Override
	public final Category getCategory() { return Category.MODERATION; }
	@Override
	public final String getUsage() { return "s!warn <@someone> <reason>"; }

}