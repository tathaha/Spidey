package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GuildCommand extends Command
{
	public GuildCommand()
	{
		super("guild", new String[]{"server"}, "Shows you info about this guild", "guild", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public final void execute(final String[] args, final Message msg)
	{
		final var eb = Utils.createEmbedBuilder(msg.getAuthor());
		final var guild = msg.getGuild();
		eb.setColor(Color.ORANGE);
		eb.setThumbnail(guild.getIconUrl());

		eb.addField("Server Name", guild.getName(), true);
		eb.addField("Server ID", "" + guild.getIdLong(), true);

		eb.addField("Owner", guild.getOwner().getUser().getAsTag(), true);
		eb.addField("Owner ID", guild.getOwnerId(), true);

		eb.addField("Text Channels", "" + guild.getTextChannelCache().size(), true);
		eb.addField("Voice Channels", "" + guild.getVoiceChannelCache().size(), true);

		eb.addField("Members", "" + guild.getMemberCache().size(), true);

		final var verificationLevel = guild.getVerificationLevel().name().toLowerCase();
		eb.addField("Verification Level", verificationLevel.substring(0, 1).toUpperCase() + verificationLevel.substring(1), true);

		eb.addField("Boost tier", "" + guild.getBoostTier().getKey(), true);
		eb.addField("Boosts", "" + guild.getBoostCount(), true);

		eb.addField("Region", guild.getRegion().getName(), true);
		eb.addField("Creation", Utils.getTime(guild.getTimeCreated().toInstant().toEpochMilli()), true);

		final var vanityUrl = guild.getVanityUrl();
		eb.addField("Custom invite/Vanity url", !guild.getFeatures().contains("VANITY_URL") ? "Guild isn't eligible to set vanity url" : (vanityUrl == null ? "Guild has no vanity url set" : vanityUrl), true);

        eb.addField("Roles", "" + (guild.getRoleCache().size() - 1), true);

		final var emoteCache = guild.getEmoteCache();
		final var emotes = emoteCache.applyStream(stream -> stream.filter(emote -> !emote.isManaged()).collect(Collectors.toList()));
		if (!emotes.isEmpty())
		{
			final var sb = new StringBuilder();
			var ec = 0;
			for (final var emote : emotes)
				sb.append(emote.getAsMention()).append(++ec != emotes.size() ? " " : "");
			eb.addField(String.format("Emotes (**%s** | **%d** animated)", ec, emoteCache.applyStream(stream -> stream.filter(Emote::isAnimated).count())), sb.length() > 1024 ? "Limit exceeded" : sb.toString(), false);
		}
		Utils.sendMessage(msg.getTextChannel(), eb.build());
	}
}