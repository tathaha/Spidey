package dev.mlnr.spidey.commands.settings.channels;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.settings.guild.GuildChannelsSettings;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BlacklistChannelCommand extends Command {
	public BlacklistChannelCommand() {
		super("blacklist", new String[]{"bh", "blacklistchannel"}, Category.Settings.CHANNELS, Permission.MANAGE_CHANNEL, 2, 2);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var i18n = ctx.getI18n();
		var prefix = guildSettingsCache.getMiscSettings(guildId).getPrefix();
		var wrongSyntax = i18n.get("command_failures.wrong_syntax", prefix, "blacklist");
		if (args.length == 0) {
			ctx.replyError(wrongSyntax);
			return false;
		}
		var channelsSettings = guildSettingsCache.getChannelsSettings(guildId);
		if (args[0].equalsIgnoreCase("list")) {
			listBlacklistedChannels(ctx, prefix, channelsSettings);
			return true;
		}
		if (args.length == 1) {
			ctx.replyError(wrongSyntax);
			return false;
		}
		ctx.getArgumentAsTextChannel(1, channel -> {
			var mention = channel.getAsMention();
			if (args[0].equalsIgnoreCase("add")) {
				if (channelsSettings.isChannelBlacklisted(channel)) {
					ctx.replyErrorLocalized("commands.blacklist.other.channels.state.already_blacklisted", mention);
					return;
				}
				if (channelsSettings.isChannelWhitelisted(channel, true)) {
					ctx.replyErrorLocalized("commands.blacklist.other.channels.state.add.whitelisted", mention, prefix);
					return;
				}
				channelsSettings.addBlacklistedChannel(channel);
				ctx.replyLocalized("commands.blacklist.other.channels.state.add.success", mention);
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				if (!channelsSettings.isChannelBlacklisted(channel)) {
					ctx.replyErrorLocalized("commands.blacklist.other.channels.state.not_blacklisted", mention);
					return;
				}
				channelsSettings.removeBlacklistedChannel(channel);
				ctx.replyLocalized("commands.blacklist.other.channels.state.removed", mention);
			}
			else {
				ctx.replyError(wrongSyntax);
			}
		});
		return true;
	}

	private void listBlacklistedChannels(CommandContext ctx, String prefix, GuildChannelsSettings channelsSettings) {
		var blacklistedChannels = channelsSettings.getBlacklistedChannels();
		var i18n = ctx.getI18n();
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser()).setAuthor(i18n.get("commands.blacklist.other.channels.text"));

		embedBuilder.setDescription(blacklistedChannels.isEmpty()
				? i18n.get("commands.blacklist.other.channels.none")
				: blacklistedChannels.stream().map(channelId -> "<#" + channelId + ">").collect(Collectors.joining("\n")));
		embedBuilder.appendDescription("\n\n" + i18n.get("commands.blacklist.other.channels.add_remove", prefix));
		ctx.reply(embedBuilder);
	}
}