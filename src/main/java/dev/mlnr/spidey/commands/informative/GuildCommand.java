package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;

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
	public void execute(final String[] args, final CommandContext ctx)
	{
		final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		final var guild = ctx.getGuild();
		eb.setColor(Color.ORANGE);
		eb.setThumbnail(guild.getIconUrl());

		eb.addField("Server Name", guild.getName(), true);
		eb.addField("Server ID", String.valueOf(guild.getIdLong()), true);

		final var ownerId = guild.getOwnerId();
		eb.addField("Owner", "<@" + ownerId + ">", true);
		eb.addField("Owner ID", ownerId, true);

		eb.addField("Text Channels", String.valueOf(guild.getTextChannelCache().size()), true);
		eb.addField("Voice Channels", String.valueOf(guild.getVoiceChannelCache().size()), true);

		eb.addField("Members", String.valueOf(guild.getMemberCount()), true);

		final var verificationLevel = guild.getVerificationLevel().name().toLowerCase();
		eb.addField("Verification Level", verificationLevel.substring(0, 1).toUpperCase() + verificationLevel.substring(1), true);

		eb.addField("Boost tier", String.valueOf(guild.getBoostTier().getKey()), true);
		eb.addField("Boosts", String.valueOf(guild.getBoostCount()), true);

		eb.addField("Region", guild.getRegion().getName(), true);
		eb.addField("Creation", Utils.formatDate(guild.getTimeCreated()), true);

		final var vanityUrl = guild.getVanityUrl();
		eb.addField("Custom invite/Vanity url", guild.getFeatures().contains("VANITY_URL") ? (vanityUrl == null ? "Guild has no vanity url set" : vanityUrl) : "Guild isn't eligible to set vanity url", true);

        eb.addField("Roles", String.valueOf(guild.getRoleCache().size()-1), true);

		final var emoteCache = guild.getEmoteCache();
		final var emotes = emoteCache.applyStream(stream -> stream.filter(emote -> !emote.isManaged()).collect(Collectors.toList()));
		if (!emotes.isEmpty())
		{
			final var sb = new StringBuilder();
			var ec = 0;
			for (final var emote : emotes)
			{
				ec++;
				sb.append(emote.getAsMention()).append(ec == emotes.size() ? "" : " ");
			}
			eb.addField(String.format("Emotes (**%d** | **%d** animated)", ec, emoteCache.applyStream(stream -> stream.filter(Emote::isAnimated).count())), sb.length() > 1024 ? "Limit exceeded" : sb.toString(), false);
		}
		ctx.reply(eb);
	}
}