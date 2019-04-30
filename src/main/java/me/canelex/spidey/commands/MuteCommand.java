package me.canelex.spidey.commands;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import me.canelex.spidey.utils.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.GuildController;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MuteCommand implements ICommand {  	
	
	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final GuildController controller = e.getGuild().getController(); //TODO rewrite
		final long l = MySQL.getChannelId(e.getGuild().getIdLong());
		
		if (e.getGuild().getTextChannelById(l) != null) {

			final TextChannel c = e.getGuild().getTextChannelById(l);
			final String neededPerm = "BAN_MEMBERS";

			final List<User> men = e.getMessage().getMentionedUsers();

			if (!e.getMessage().getContentRaw().equals("s!mute")) {

				if (API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {

					if (e.getGuild().getRolesByName("Muted", false).isEmpty()) {

						controller.createRole().setName("Muted").setColor(Color.GRAY).complete();

					}

					String length = e.getMessage().getContentRaw().substring(5 + 2);
					length = length.substring(0, length.indexOf(" "));

					String time = e.getMessage().getContentRaw().substring((6 + 2 + length.length()));
					time = time.substring(0, time.indexOf(" "));

					String reason = e.getMessage().getContentRaw().substring((7 + 2 + length.length() + time.length()));
					reason = reason.substring(0, reason.lastIndexOf(" "));

					final int lengthV = Integer.valueOf(length);

					if (!men.isEmpty()) {

						for (final User user : men) {

							final Role muted = e.getGuild().getRolesByName("Muted", false).get(0);
							final Member member = e.getGuild().getMember(user);

							API.deleteMessage(e.getMessage());
							controller.addSingleRoleToMember(member, muted).queue();

							if (StringUtils.isNumeric(length) && lengthV != 0) {

								if (time.equalsIgnoreCase("d")) {

									final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
									eb.setAuthor("NEW MUTE");
									eb.setThumbnail(user.getEffectiveAvatarUrl());
									eb.setColor(Color.RED);
									eb.addField("User", user.getAsMention(), true);
									eb.addField("Moderator", e.getAuthor().getAsMention(), true);
									eb.addField("Reason", "**" + reason + "**", true);

									if (lengthV == 1) {

										eb.addField("Length", "**1** day", true);

									}

									else {

										eb.addField("Length", "**" + lengthV + "** days", true);

									}

									API.sendMessage(c, eb.build());

									controller.removeSingleRoleFromMember(member, muted).queueAfter(lengthV, TimeUnit.DAYS);

								}

								else {

									if (time.equalsIgnoreCase("w")) {

										final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
										eb.setAuthor("NEW MUTE");
										eb.setThumbnail(user.getEffectiveAvatarUrl());
										eb.setColor(Color.RED);
										eb.addField("User", user.getAsMention(), true);
										eb.addField("Moderator", e.getAuthor().getAsMention(), true);
										eb.addField("Reason", "**" + reason + "**", true);

										if (lengthV == 1) {

											eb.addField("Length", "**1** week", true);

										}

										else {

											eb.addField("Length", "**" + lengthV + "** weeks", true);

										}

										API.sendMessage(c, eb.build());

										controller.removeSingleRoleFromMember(member, muted).queueAfter((lengthV * 7), TimeUnit.DAYS);

									}

									else {

										if (time.equalsIgnoreCase("m")) {

											final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
											eb.setAuthor("NEW MUTE");
											eb.setThumbnail(user.getEffectiveAvatarUrl());
											eb.setColor(Color.RED);
											eb.addField("User", user.getAsMention(), true);
											eb.addField("Moderator", e.getAuthor().getAsMention(), true);
											eb.addField("Reason", "**" + reason + "**", true);

											if (lengthV == 1) {

												eb.addField("Length", "**1** minute", true);
												API.sendMessage(c, eb.build());

											}

											else {

												eb.addField("Length", "**" + lengthV + "** minut", true);

												API.sendMessage(c, eb.build());

											}

											controller.removeSingleRoleFromMember(member, muted).queueAfter(lengthV, TimeUnit.MINUTES);

										}

										else {

											API.sendMessage(e.getChannel(), "Unknown time value. Value must be **d**ay(s), **w**eek(s) or **m**inute(s).", false);

										}

									}

								}

							}

							else {

								API.sendMessage(e.getChannel(), "Given value is not a number.", false);

							}

						}

					}

					else {

						API.sendMessage(e.getChannel(), "Unknown arguments..", false);

					}

				}

				else {

					API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);

				}

			}

			else {

				API.sendMessage(e.getChannel(), "Unknown arguments..", false);

			}
			
		}
		
	}

	@Override
	public final String help() {

		return "Mutes user";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}