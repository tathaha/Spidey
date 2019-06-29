package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.PermissionError;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class BanCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final int maxArgs = 4;
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

		if (!e.getGuild().getSelfMember().canInteract(mb)) {

			Utils.returnError("Can't ban the user due to permission hierarchy position", msg);
			return;

		}

		int delDays;

		try {

			delDays = Math.max(0, Math.min(Integer.parseInt(args[2]), 7));

		} catch (final NumberFormatException ex) {

			Utils.returnError("Please enter a valid number", msg);
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
	public final String getDescription() { return "Bans user"; }
	@Override
	public final boolean isAdmin() { return true; }
	@Override
	public final String getInvoke() { return "ban"; }
	@Override
	public final Category getCategory() { return Category.MODERATION; }
	@Override
	public final String getUsage() { return "s!ban <@someone> <delDays> <reason>"; }

}