package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;

import java.awt.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ServerCommand extends Command {
	public ServerCommand() {
		super("server", "Shows you info about this server", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var eb = Utils.createEmbedBuilder(ctx.getUser());
		var guild = ctx.getGuild();
		var i18n = ctx.getI18n();

		eb.setColor(Color.ORANGE);
		eb.setThumbnail(guild.getIconUrl());

		eb.addField(i18n.get("commands.server.fields.name"), guild.getName(), true);
		eb.addField(i18n.get("commands.server.fields.id"), String.valueOf(guild.getIdLong()), true);

		var ownerId = guild.getOwnerId();
		eb.addField(i18n.get("commands.server.fields.owner.title"), "<@" + ownerId + ">", true);
		eb.addField(i18n.get("commands.server.fields.owner.id"), ownerId, true);

		eb.addField(i18n.get("commands.server.fields.channels.text"), String.valueOf(guild.getTextChannelCache().size()), true);
		eb.addField(i18n.get("commands.server.fields.channels.voice"), String.valueOf(guild.getVoiceChannelCache().size()), true);

		eb.addField(i18n.get("commands.server.fields.members"), String.valueOf(guild.getMemberCount()), true);

		eb.addField(i18n.get("commands.server.fields.verification_level.title"),
				i18n.get("commands.server.fields.verification_level." + guild.getVerificationLevel().name().toLowerCase()), true);

		eb.addField(i18n.get("commands.server.fields.boost.tier"), String.valueOf(guild.getBoostTier().getKey()), true);
		eb.addField(i18n.get("commands.server.fields.boost.amount"), String.valueOf(guild.getBoostCount()), true);

		eb.addField(i18n.get("commands.server.fields.region"), guild.getRegion().getName(), true);
		eb.addField(i18n.get("commands.server.fields.creation"), Utils.formatDate(guild.getTimeCreated()), true);

		var vanityUrl = guild.getVanityUrl();
		eb.addField(i18n.get("commands.server.fields.vanity_url.title"), guild.getFeatures().contains("VANITY_URL")
				? (vanityUrl == null ? i18n.get("commands.server.fields.vanity_url.none") : vanityUrl)
				: i18n.get("commands.server.fields.vanity_url.not_eligible"), true);

		eb.addField(i18n.get("commands.server.fields.roles"), String.valueOf(guild.getRoleCache().size() - 1), true);

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
			eb.addField(i18n.get("commands.server.fields.emotes", emotes.size(), animated),
					sb.length() > 1024 ? i18n.get("limit_exceeded") : sb.toString(), false);
		}
		ctx.reply(eb);
		return true;
	}
}