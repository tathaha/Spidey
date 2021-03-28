package dev.mlnr.spidey.commands.settings.channels;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.settings.guild.GuildChannelsSettings;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class WhitelistChannelCommand extends Command {
	public WhitelistChannelCommand() {
		super("whitelist", new String[]{"wh", "whitelistchannel"}, Category.Settings.CHANNELS, Permission.MANAGE_CHANNEL, 2, 2);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var i18n = ctx.getI18n();
		var prefix = guildSettingsCache.getMiscSettings(guildId).getPrefix();
		var wrongSyntax = i18n.get("command_failures.wrong_syntax", prefix, "whitelist");
		if (args.length == 0) {
			ctx.replyError(wrongSyntax);
			return;
		}
		var channelsSettings = guildSettingsCache.getChannelsSettings(guildId);
		if (args[0].equalsIgnoreCase("list")) {
			listWhitelistedChannels(ctx, prefix, channelsSettings);
			return;
		}
		if (args.length == 1) {
			ctx.replyError(wrongSyntax);
			return;
		}
		ctx.getArgumentAsTextChannel(1, channel -> {
			var mention = channel.getAsMention();
			if (args[0].equalsIgnoreCase("add")) {
				if (channelsSettings.isChannelWhitelisted(channel, true)) {
					ctx.replyErrorLocalized("commands.whitelist.other.channels.state.already_whitelisted", mention);
					return;
				}
				if (channelsSettings.isChannelBlacklisted(channel)) {
					ctx.replyErrorLocalized("commands.whitelist.other.channels.state.add.blacklisted", mention, prefix);
					return;
				}
				channelsSettings.addWhitelistedChannel(channel);
				ctx.replyLocalized("commands.whitelist.other.channels.state.add.success", mention);
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				if (!channelsSettings.isChannelWhitelisted(channel, true)) {
					ctx.replyErrorLocalized("commands.whitelist.other.channels.state.not_whitelisted", mention);
					return;
				}
				channelsSettings.removeWhitelistedChannel(channel);
				ctx.replyLocalized("commands.whitelist.other.channels.state.removed", mention);
			}
			else {
				ctx.replyError(wrongSyntax);
			}
		});
	}

	private void listWhitelistedChannels(CommandContext ctx, String prefix, GuildChannelsSettings channelsSettings) {
		var whitelistedChannels = channelsSettings.getWhitelistedChannels();
		var i18n = ctx.getI18n();
		var embedBuilder = Utils.createEmbedBuilder(ctx.getAuthor()).setAuthor(i18n.get("commands.whitelist.other.channels.text"));

		embedBuilder.setDescription(whitelistedChannels.isEmpty()
				? i18n.get("commands.whitelist.other.channels.none")
				: whitelistedChannels.stream().map(channelId -> "<#" + channelId + ">").collect(Collectors.joining("\n")));
		embedBuilder.appendDescription("\n\n" + i18n.get("commands.whitelist.other.channels.add_remove", prefix));
		ctx.reply(embedBuilder);
	}
}