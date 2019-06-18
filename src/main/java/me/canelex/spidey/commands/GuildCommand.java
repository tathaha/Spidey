package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GuildCommand implements ICommand {

	private final Locale locale = new Locale("en", "EN");
	private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	private final SimpleDateFormat date = new SimpleDateFormat("EE, d.LLL Y", locale);
	private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final EmbedBuilder eb = Utils.createEmbedBuilder(e.getAuthor());
		eb.setColor(Color.ORANGE);
		eb.setThumbnail(e.getGuild().getIconUrl());

		eb.addField("Server Name", e.getGuild().getName(), true);
		eb.addField("Server ID", e.getGuild().getId(), true);

		eb.addField("Owner", Objects.requireNonNull(e.getGuild().getOwner()).getUser().getAsTag(), true);
		eb.addField("Owner ID", e.getGuild().getOwnerId(), true);

		eb.addField("Text Channels", "" + e.getGuild().getTextChannelCache().size(), true);
		eb.addField("Voice Channels", "" + e.getGuild().getVoiceChannelCache().size(), true);

		eb.addField("Members", "" + e.getGuild().getMemberCache().size(), true);
		eb.addField("Verification Level", e.getGuild().getVerificationLevel().name(), true);

		eb.addField("Region", e.getGuild().getRegionRaw(), true);

		cal.setTimeInMillis(e.getGuild().getTimeCreated().toInstant().toEpochMilli());
		final String creatdate = date.format(cal.getTime());
		final String creattime = time.format(cal.getTime());
		eb.addField("Creation", String.format( "%s | %s", creatdate, creattime), true);

		//by Minn
		if (!Utils.canSetVanityUrl(e.getGuild())) {
			eb.addField("Custom invite/Vanity url", "Guild isn't eligible to set vanity url", true);
		}
        else {
			e.getGuild().retrieveVanityUrl().submit().handle((v, error) -> v == null ? "Guild has no vanity url set" : "discord.gg/" + v)
					.thenAccept(vanity -> eb.addField("Custom invite/Vanity url", vanity, true));
		}
        //thanks for the code

		final List<Role> roles = e.getGuild().getRoleCache().stream().filter(role -> e.getGuild().getPublicRole() != role).collect(Collectors.toList());
        eb.addField("Roles", "" + roles.size(), true);

		final StringBuilder st = new StringBuilder();

		int ec = 0;
		final long an = e.getGuild().getEmotes().stream().filter(Emote::isAnimated).count();

		for (final Emote emote : e.getGuild().getEmotes()) {
			ec++;
			if (ec == e.getGuild().getEmoteCache().size()) {
				st.append(emote.getAsMention());
			}

			else {
				st.append(emote.getAsMention()).append(" ");
			}
		}

		if (ec > 0) {
			if (st.length() > 1024) {
				eb.addField(String.format("Emotes (**%s** | **%s** animated)", ec, an), "Limit exceeded", false);
			}

			else {
				eb.addField(String.format("Emotes (**%s** | **%s** animated)", ec, an), (st.toString().length() == 0) ? "None" : st.toString(), false);
			}
		}

		Utils.sendMessage(e.getChannel(), eb.build());

	}

	@Override
	public final String help() { return "Shows you info about this guild"; }
	@Override
	public final boolean isAdmin() {
		return false;
	}
	@Override
	public final String invoke() { return "guild"; }

}