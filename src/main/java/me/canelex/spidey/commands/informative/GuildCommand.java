package me.canelex.spidey.commands.informative;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

@SuppressWarnings("unused")
public class GuildCommand extends Command
{
	public GuildCommand()
	{
		super("guild", new String[]{"server"}, "Shows you info about this guild", "guild", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		final var guild = message.getGuild();
		eb.setColor(Color.ORANGE);
		eb.setThumbnail(guild.getIconUrl());

		eb.addField("Server Name", guild.getName(), true);
		eb.addField("Server ID", guild.getId(), true);

		eb.addField("Owner", guild.getOwner().getUser().getAsTag(), true);
		eb.addField("Owner ID", guild.getOwnerId(), true);

		eb.addField("Text Channels", "" + guild.getTextChannelCache().size(), true);
		eb.addField("Voice Channels", "" + guild.getVoiceChannelCache().size(), true);

		eb.addField("Members", "" + guild.getMemberCache().size(), true);
		eb.addField("Verification Level", guild.getVerificationLevel().name(), true);

		eb.addField("Boost tier", "" + guild.getBoostTier().getKey(), true);
		eb.addField("Boosts", "" + guild.getBoostCount(), true);

		eb.addField("Region", guild.getRegion().getName(), true);
		eb.addField("Creation", Utils.getTime(guild.getTimeCreated().toInstant().toEpochMilli()), true);

		final var vanityUrl = guild.getVanityUrl();
		if (!Utils.canSetVanityUrl(guild)) //could use ternary here too, but i don't use it because of readability
			eb.addField("Custom invite/Vanity url", "Guild isn't eligible to set vanity url", true);
        else
			eb.addField("Custom invite/Vanity url", vanityUrl == null ? "Guild has no vanity url set" : vanityUrl, true);

        eb.addField("Roles", "" + (guild.getRoleCache().size() - 1), true);

		final var st = new StringBuilder();

		var ec = 0;
		final var emotes = guild.retrieveEmotes().complete();
		final var an = emotes.stream().filter(Emote::isAnimated).count();

		for (final var emote : emotes)
		{
			ec++;
			final var mention = emote.getAsMention();
			if (ec == emotes.size())
				st.append(mention);
			else
				st.append(mention).append(" ");
		}

		if (ec > 0)
		{
			if (st.length() > 1024)
				eb.addField(String.format("Emotes (**%s** | **%s** animated)", ec, an), "Limit exceeded", false);
			else
				eb.addField(String.format("Emotes (**%s** | **%s** animated)", ec, an), (st.toString().length() == 0) ? "None" : st.toString(), false);
		}

		Utils.sendMessage(message.getChannel(), eb.build());
	}
}