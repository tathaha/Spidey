package me.canelex.spidey.commands.moderation;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class WarnCommand extends Command
{
	public WarnCommand()
	{
		super("warn", new String[]{}, "Warns a user", "warn <@someone> <reason>", Category.MODERATION, Permission.BAN_MEMBERS, 3);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var member = message.getMember();

		message.delete().queueAfter(5, TimeUnit.SECONDS);

		final var requiredPermission = getRequiredPermission();
		if (!Utils.hasPerm(member, requiredPermission))
		{
			Utils.getPermissionsError(requiredPermission, message);
			return;
		}
		
		if (args.length < 3)
		{
			Utils.returnError("Wrong syntax", message);
			return;
		}

		if (!Message.MentionType.USER.getPattern().matcher(args[1]).matches())
		{
			Utils.returnError("Wrong syntax (no mention)", message);
			return;
		}

		final var members = message.getMentionedMembers();
		if (members.isEmpty())
		{
			Utils.returnError("User wasn't found", message);
			return;
		}
		final var mb = members.get(0);

		if (!member.canInteract(mb))
		{
			Utils.returnError("Can't warn the user due to permission hierarchy position", message);
			return;
		}

		final var eb = new EmbedBuilder();
		final var guild = message.getGuild();

		final var channel = Utils.getLogChannel(guild.getIdLong());
		if (channel != null)
		{
			final var user = mb.getUser();
			final var author = message.getAuthor();
			eb.setAuthor("NEW WARN");
			eb.setThumbnail(user.getAvatarUrl());
			eb.addField("User", "**" + user.getAsTag() + "**", true);
			eb.addField("ID", "**" + user.getId() + "**", true);
			eb.addField("Moderator", "**" + author.getAsTag() + "**", true);
			eb.addField("Reason", "**" + args[2] + "**", true);
			eb.setColor(Color.ORANGE);
			Utils.sendMessage(channel, eb.build());
			Utils.sendPrivateMessageFormat(user, ":warning: You've been warned in the guild **%s** from **%s** for **%s**.", guild.getName(), author.getName(), args[2]);
		}
	}
}