package me.canelex.Spidey.commands;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.Emojis;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.concurrent.TimeUnit;

public class BanCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;

	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String neededPerm = "BAN_MEMBERS";

		if (API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {

			final Message msg = e.getMessage();
			final TextChannel ch = e.getChannel();
			final String content = msg.getContentRaw();

			final String[] args = content.trim().split("\\s+");

			e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);

			if (args.length < 3) {

				e.getMessage().addReaction(Emojis.cross).queue();

				ch.sendMessage(":no_entry: Wrong syntax.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS, null, ignored -> {}));

			}

			else {

				if (args[1].matches(Message.MentionType.USER.getPattern().pattern())) {

					if (!msg.getMentionedMembers().isEmpty()) {

						final Member mb = msg.getMentionedMembers().get(0);

						try {

							int deldays = 0;

							if (args.length == 3) {

								try {

									deldays = Math.max(0, Math.min(Integer.parseInt(args[2]), 7));
									e.getGuild().getController().ban(mb, deldays, "[Banned by Spidey#2370]").queue(null, ignored -> {});
									msg.addReaction(Emojis.check).queue();
									ch.sendMessage(":white_check_mark: Successfully banned user **" + mb.getUser().getAsTag() + "**.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS, null, ignored -> {}));

								}

								catch (final NumberFormatException ex) {

									msg.addReaction(Emojis.cross).queue();
									ch.sendMessage(":no_entry: Please enter a valid number.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS, null, ignored -> {}));

								}

							}

							else {

								final String reason = content.substring(9 + args[1].length());
								e.getGuild().getController().ban(mb, deldays, "[Banned by Spidey#2370] " + reason).queue(null, ignored -> {});
								msg.addReaction(Emojis.check).queue();
								ch.sendMessage(":white_check_mark: Successfully banned user **" + mb.getUser().getAsTag() + "** with reason **" + reason + "**.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS, null, ignored -> {}));

							}

						}

						catch (final HierarchyException ex) {

							msg.addReaction(Emojis.cross).queue();
							ch.sendMessage(":no_entry: Can't ban the user due to permission hierarchy position.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS, null, ignored -> {}));

						}

					}

					else {

						msg.addReaction(Emojis.cross).queue();
						ch.sendMessage(":no_entry: User wasn't found.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS, null, ignored -> {}));

					}

				}

				else {

					e.getMessage().addReaction(Emojis.cross).queue();
					ch.sendMessage(":no_entry: Wrong syntax.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS, null, ignored -> {}));

				}

			}

		}

		else {

			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);

		}

	}

	@Override
	public final String help() {

		return "Bans user";

	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}