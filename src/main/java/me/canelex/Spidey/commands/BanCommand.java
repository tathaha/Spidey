package me.canelex.Spidey.commands;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.Emojis;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BanCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;

	}

	private void returnError(String errMsg, Message origin)  {

		origin.addReaction(Emojis.cross).queue();
		origin.getTextChannel().sendMessage(String.format(":no_entry: %s.", errMsg)).queue(m -> {

			origin.delete().queueAfter(5, TimeUnit.SECONDS, null, userGone -> {});
			m.delete().queueAfter(5, TimeUnit.SECONDS, null, botGone -> {});

		});

	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final int maxArgs = 4;
		final Message msg = e.getMessage();
		final Permission perm = Permission.BAN_MEMBERS;

		e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);

		if (!API.hasPerm(e.getMember(), perm))  {

			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(perm.getName()), false);
			return;

		}

		final String[] args = msg.getContentRaw().trim().split("\\s+", maxArgs);

		if (args.length < 3) {

			returnError("Wrong syntax", msg);
			return;

		}

		if (!args[1].matches(Message.MentionType.USER.getPattern().pattern())) {

			returnError("Wrong syntax (no mention)", msg);
			return;

		}

		List<Member> members = msg.getMentionedMembers();

		if (members.isEmpty()) {

			returnError("User wasn't found", msg);
			return;

		}

		final Member mb = members.get(0);

		if (!e.getGuild().getSelfMember().canInteract(mb)) {

			returnError("Can't ban the user due to permission hierarchy position", msg);
			return;

		}

		int delDays;

		try {

			delDays = Math.max(0, Math.min(Integer.parseInt(args[2]), 7));

		} catch (final NumberFormatException ex) {

			returnError("Please enter a valid number", msg);
			return;

		}

		final StringBuilder reasonBuilder = new StringBuilder("[Banned by Spidey#2370]");
		final StringBuilder banMsgBuilder = new StringBuilder(":white_check_mark: Successfully banned user **"
				+ mb.getUser().getAsTag() + "**.");

		if (args.length == maxArgs) {

			final String reason = args[3];
			reasonBuilder.append(String.format(" %s", reason));
			banMsgBuilder.deleteCharAt(banMsgBuilder.length() - 1).append(String.format(" with reason **%s**.", reason));

		}

		final String reasonMsg = reasonBuilder.toString();
		final String banMsg = banMsgBuilder.toString();

		msg.addReaction(Emojis.check).queue();
		e.getGuild().getController().ban(mb, delDays, reasonMsg).queue();

		e.getChannel().sendMessage(banMsg).queue(m -> {

			msg.delete().queueAfter(5, TimeUnit.SECONDS, null, userGone -> {});
			m.delete().queueAfter(5, TimeUnit.SECONDS, null, botGone -> {});

		});

	}

	@Override
	public final String help() {

		return "Bans user";

	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}