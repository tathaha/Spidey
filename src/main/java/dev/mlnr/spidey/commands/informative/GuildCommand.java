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
public class GuildCommand extends Command {

	public GuildCommand() {
		super("guild", new String[]{"server"}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		var guild = ctx.getGuild();
		var i18n = ctx.getI18n();

		eb.setColor(Color.ORANGE);
		eb.setThumbnail(guild.getIconUrl());

		eb.addField(i18n.get("commands.guild.fields.name"), guild.getName(), true);
		eb.addField(i18n.get("commands.guild.fields.id"), String.valueOf(guild.getIdLong()), true);

		var ownerId = guild.getOwnerId();
		eb.addField(i18n.get("commands.guild.fields.owner.title"), "<@" + ownerId + ">", true);
		eb.addField(i18n.get("commands.guild.fields.owner.id"), ownerId, true);

		eb.addField(i18n.get("commands.guild.fields.channels.text"), String.valueOf(guild.getTextChannelCache().size()), true);
		eb.addField(i18n.get("commands.guild.fields.channels.voice"), String.valueOf(guild.getVoiceChannelCache().size()), true);

		eb.addField(i18n.get("commands.guild.fields.members"), String.valueOf(guild.getMemberCount()), true);

		eb.addField(i18n.get("commands.guild.fields.verification_level.title"),
				i18n.get("commands.guild.fields.verification_level." + guild.getVerificationLevel().name().toLowerCase()), true);

		eb.addField(i18n.get("commands.guild.fields.boost.tier"), String.valueOf(guild.getBoostTier().getKey()), true);
		eb.addField(i18n.get("commands.guild.fields.boost.amount"), String.valueOf(guild.getBoostCount()), true);

		eb.addField(i18n.get("commands.guild.fields.region"), guild.getRegion().getName(), true);
		eb.addField(i18n.get("commands.guild.fields.creation"), Utils.formatDate(guild.getTimeCreated()), true);

		var vanityUrl = guild.getVanityUrl();
		eb.addField(i18n.get("commands.guild.fields.vanity_url.title"), guild.getFeatures().contains("VANITY_URL")
				? (vanityUrl == null ? i18n.get("commands.guild.fields.vanity_url.none") : vanityUrl)
				: i18n.get("commands.guild.fields.vanity_url.not_eligible"), true);

		eb.addField(i18n.get("commands.guild.fields.roles"), String.valueOf(guild.getRoleCache().size() - 1), true);

		var emoteCache = guild.getEmoteCache();
		var emotes = emoteCache.applyStream(stream -> stream.filter(emote -> !emote.isManaged()).collect(Collectors.toList()));
		if (!emotes.isEmpty()) {
			var sb = new StringBuilder();
			var ec = 0;
			for (var emote : emotes) {
				ec++;
				sb.append(emote.getAsMention()).append(ec == emotes.size() ? "" : " ");
			}
			var animated = emoteCache.applyStream(stream -> stream.filter(Emote::isAnimated).count());
			eb.addField(i18n.get("commands.guild.fields.emotes", emotes.size(), animated),
					sb.length() > 1024 ? i18n.get("limit_exceeded") : sb.toString(), false);
		}
		ctx.reply(eb);
	}
}