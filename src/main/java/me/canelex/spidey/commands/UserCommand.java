package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
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

@SuppressWarnings("unused")
public class UserCommand implements ICommand {

	private final Locale locale = new Locale("en", "EN");
	private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	private final SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);
	private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final EmbedBuilder eb = Utils.createEmbedBuilder(e.getAuthor());

		if (e.getMessage().getMentionedUsers().isEmpty()) {

			eb.setAuthor("USER INFO - " + e.getAuthor().getAsTag());
			eb.setColor(Color.WHITE);
			eb.setThumbnail(e.getAuthor().getEffectiveAvatarUrl());
			eb.addField("ID", e.getAuthor().getId(), false);

			if (Objects.requireNonNull(e.getMember()).getNickname() != null) {
				eb.addField("Nickname for this guild", e.getMember().getNickname(), false);
			}

			cal.setTimeInMillis(e.getAuthor().getTimeCreated().toInstant().toEpochMilli());
			final String creatdate = date.format(cal.getTime());
			final String creattime = time.format(cal.getTime());

			eb.addField("Account created", String.format( "%s | %s UTC", creatdate, creattime), false);

			cal.setTimeInMillis(e.getMember().getTimeJoined().toInstant().toEpochMilli());
			final String joindate = date.format(cal.getTime());
			final String jointime = time.format(cal.getTime());

			eb.addField("User joined", String.format( "%s | %s UTC", joindate, jointime), false);

			if (e.getGuild().getBoosters().contains(e.getMember())) {
				cal.setTimeInMillis(Objects.requireNonNull(e.getMember().getTimeBoosted()).toInstant().toEpochMilli());
				final String boostdate = date.format(cal.getTime());
				final String boosttime = time.format(cal.getTime());
				eb.addField("Boosting since", String.format("%s | %s UTC", boostdate, boosttime), false);
			}

			if (!e.getMember().getRoles().isEmpty()) {

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

			Utils.sendMessage(e.getChannel(), eb.build());
			eb.clear();

		}

		else {

			final User user = e.getMessage().getMentionedUsers().get(0);
			final Member member = e.getGuild().getMember(user);

			eb.setAuthor("USER INFO - " + user.getAsTag());
			eb.setColor(Color.WHITE);
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.addField("ID", user.getId(), false);

			assert member != null;
			if (member.getNickname() != null) {
				eb.addField("Nickname for this guild", member.getNickname(), false);
			}

			cal.setTimeInMillis(user.getTimeCreated().toInstant().toEpochMilli());
			final String creatdate = date.format(cal.getTime());
			final String creattime = time.format(cal.getTime());

			eb.addField("Account created", String.format("%s | %s UTC", creatdate, creattime), false);

			cal.setTimeInMillis(member.getTimeJoined().toInstant().toEpochMilli());
			final String joindate = date.format(cal.getTime());
			final String jointime = time.format(cal.getTime());

			eb.addField("User joined", String.format("%s | %s UTC", joindate, jointime), false);

			if (e.getGuild().getBoosters().contains(member)) {
				cal.setTimeInMillis(Objects.requireNonNull(member.getTimeBoosted()).toInstant().toEpochMilli());
				final String boostdate = date.format(cal.getTime());
				final String boosttime = time.format(cal.getTime());
				eb.addField("Boosting since", String.format("%s | %s UTC", boostdate, boosttime), false);
			}

			if (!member.getRoles().isEmpty()) {

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

			Utils.sendMessage(e.getChannel(), eb.build());
			eb.clear();

		}

	}

	@Override
	public final String getDescription() { return "Shows info about you or mentioned user"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "user"; }
	@Override
	public final Category getCategory() { return Category.INFORMATIVE; }
	@Override
	public final String getUsage() { return "s!user (@someone)"; }

}