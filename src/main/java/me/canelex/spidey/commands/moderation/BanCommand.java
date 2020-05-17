package me.canelex.spidey.commands.moderation;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

@SuppressWarnings("unused")
public class BanCommand extends Command
{
	public BanCommand()
	{
		super("ban", new String[]{}, "Bans the given user", "ban <@someone> <delDays> <reason>", Category.MODERATION,
				Permission.BAN_MEMBERS, 4);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var channel = message.getChannel();
		final var guild = message.getGuild();

		final var requiredPermission = getRequiredPermission();
		if (!Utils.hasPerm(message.getMember(), requiredPermission))
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
		if (!guild.getSelfMember().canInteract(mb))
			Utils.returnError("Can't ban the user due to permission hierarchy position", message);

		var delDays = 0;
		try
		{
			delDays = Math.max(0, Math.min(Integer.parseUnsignedInt(args[2]), 7));
		}
		catch (final NumberFormatException ex)
		{
			Utils.returnError("Please enter a valid number", message);
			return;
		}

		final var reasonBuilder = new StringBuilder("[Banned by Spidey#2370]");
		final var banmessageBuilder = new StringBuilder(":white_check_mark: Successfully banned user **"
				+ mb.getUser().getAsTag() + "**.");

		if (args.length == getMaxArgs())
		{
			final var reason = args[3];
			reasonBuilder.append(String.format(" %s", reason));
			banmessageBuilder.deleteCharAt(banmessageBuilder.length() - 1).append(String.format(" with reason **%s**.", reason));
		}

		final var reasonMessage = reasonBuilder.toString();
		final var banMessage = banmessageBuilder.toString();

		message.addReaction(Emojis.CHECK).queue();
		guild.ban(mb, delDays, reasonMessage).queue();

		channel.sendMessage(banMessage)
			   .delay(Duration.ofSeconds(5))
			   .flatMap(Message::delete)
			   .flatMap(ignored -> message.delete())
			   .queue();
	}
}