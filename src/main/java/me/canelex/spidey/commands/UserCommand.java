package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class UserCommand implements ICommand {

	private final Locale locale = new Locale("en", "EN");
	private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	private final SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);
	private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		if (e.getMessage().getMentionedUsers().isEmpty()) {

			final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());

			eb.setAuthor("USER INFO - " + e.getAuthor().getAsTag());
			eb.setColor(Color.WHITE);
			eb.setThumbnail(e.getAuthor().getEffectiveAvatarUrl());
			eb.addField("ID", "**" + e.getAuthor().getId() + "**", false);

			eb.addField("Nickname for this guild", "**" + (Objects.requireNonNull(e.getMember()).getNickname() == null ? "None" : e.getMember().getNickname()) + "**", false);

			cal.setTimeInMillis(e.getAuthor().getTimeCreated().toInstant().toEpochMilli());
			final String creatdate = date.format(cal.getTime());
			final String creattime = time.format(cal.getTime());

			eb.addField("Account created", String.format( "**%s** | **%s** UTC", creatdate, creattime), false);

			cal.setTimeInMillis(e.getMember().getTimeJoined().toInstant().toEpochMilli());
			final String joindate = date.format(cal.getTime());
			final String jointime = time.format(cal.getTime());

			eb.addField("User joined", String.format( "**%s** | **%s** UTC", joindate, jointime), false);

			if (e.getMember().getRoles().isEmpty()) {

				eb.addField("Roles [**0**]", "None", false);

			}

			else {

				int i = 0;
				StringBuilder s = new StringBuilder();

				for (final Role role : e.getMember().getRoles()) {

					i++;

					if (i == e.getMember().getRoles().size()) {

						s.append(role.getName());

					}

					else {

						s.append(role.getName()).append(", ");

					}

				}

				eb.addField("Roles [**" + i + "**]", s.toString(), false);

			}

			API.sendMessage(e.getChannel(), eb.build());

		}

		else {

			final User user = e.getMessage().getMentionedUsers().get(0);
			final Member member = e.getGuild().getMember(user);

			final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());

			eb.setAuthor("USER INFO - " + user.getAsTag());
			eb.setColor(Color.WHITE);
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.addField("ID", "**" + user.getId() + "**", false);

			assert member != null;
			eb.addField("Nickname for this guild", "**" + (member.getNickname() == null ? "None" : member.getNickname()) + "**", false);

			cal.setTimeInMillis(user.getTimeCreated().toInstant().toEpochMilli());
			final String creatdate = date.format(cal.getTime());
			final String creattime = time.format(cal.getTime());

			eb.addField("Account created", String.format( "**%s** | **%s** UTC", creatdate, creattime), false);

			cal.setTimeInMillis(member.getTimeJoined().toInstant().toEpochMilli());
			final String joindate = date.format(cal.getTime());
			final String jointime = time.format(cal.getTime());

			eb.addField("User joined", String.format( "**%s** | **%s** UTC", joindate, jointime), false);

			if (member.getRoles().isEmpty()) {

				eb.addField("Roles [**0**]", "None", false);

			}

			else {

				int i = 0;
				StringBuilder s = new StringBuilder();

				for (final Role role : member.getRoles()) {

					i++;

					if (i == member.getRoles().size()) {

						s.append(role.getName());

					}

					else {

						s.append(role.getName()).append(", ");

					}

				}

				eb.addField("Roles [**" + i + "**]", s.toString(), false);

			}

			API.sendMessage(e.getChannel(), eb.build());

		}

	}

	@Override
	public final String help() {

		return "Shows info about you or mentioned user";

	}

}