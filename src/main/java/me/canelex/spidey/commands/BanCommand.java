package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BanCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final int maxArgs = 4;
		final Message msg = e.getMessage();

		e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);

		if (e.getMember() != null && !API.hasPerm(e.getMember(), Permission.BAN_MEMBERS))  {

			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage("BAN_MEMBERS"), false);
			return;

		}

		final String[] args = msg.getContentRaw().trim().split("\\s+", maxArgs);

		if (args.length < 3) {

			API.returnError("Wrong syntax", msg);
			return;

		}

		if (!args[1].matches(Message.MentionType.USER.getPattern().pattern())) {

			API.returnError("Wrong syntax (no mention)", msg);
			return;

		}

		List<Member> members = msg.getMentionedMembers();

		if (members.isEmpty()) {

			API.returnError("User wasn't found", msg);
			return;

		}

		final Member mb = members.get(0);

		if (!e.getGuild().getSelfMember().canInteract(mb)) {

			API.returnError("Can't ban the user due to permission hierarchy position", msg);
			return;

		}

		int delDays;

		try {

			delDays = Math.max(0, Math.min(Integer.parseInt(args[2]), 7));

		} catch (final NumberFormatException ex) {

			API.returnError("Please enter a valid number", msg);
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

		msg.addReaction(Emojis.CHECK).queue();
		e.getGuild().ban(mb, delDays, reasonMsg).queue();

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
	public final boolean isAdmin() {
		return true;
	}

}