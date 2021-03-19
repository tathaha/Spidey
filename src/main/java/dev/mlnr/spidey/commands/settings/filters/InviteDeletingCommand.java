package dev.mlnr.spidey.commands.settings.filters;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.settings.guild.GuildFiltersSettings;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class InviteDeletingCommand extends Command {

	public InviteDeletingCommand() {
		super("invitedeleting", new String[]{"invitedel", "invdel", "invitefilter"}, Category.Settings.FILTERS, Permission.MANAGE_SERVER, 3, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var filtersSettings = guildSettingsCache.getFiltersSettings(guildId);
		var i18n = ctx.getI18n();
		if (args.length == 0) {
			var enabled = !filtersSettings.isInviteDeletingEnabled();
			filtersSettings.setInviteDeletingEnabled(enabled);
			ctx.reactLike();
			ctx.reply(i18n.get("commands.invitedeleting.other.done.text", enabled ? i18n.get("enabled") : i18n.get("disabled")) +
					(enabled ? " " + i18n.get("commands.invitedeleting.other.done.ignored.text") : ""));
			return;
		}
		var prefix = guildSettingsCache.getMiscSettings(guildId).getPrefix();
		if (!filtersSettings.isInviteDeletingEnabled()) {
			ctx.replyErrorLocalized("commands.invitedeleting.other.disabled", prefix);
			return;
		}
		var wrongSyntax = i18n.get("command_failures.wrong_syntax", prefix, "invitedeleting");
		if (args[0].equalsIgnoreCase("list")) {
			listIgnored(args, ctx, filtersSettings);
			return;
		}
		if (args.length != 3) {
			ctx.replyError(wrongSyntax);
			return;
		}
		if (args[1].equalsIgnoreCase("user")) {
			ctx.getArgumentAsUser(2, user -> proceed(args, ctx, filtersSettings, user));
		}
		else if (args[1].equalsIgnoreCase("role")) {
			ctx.getArgumentAsRole(2, role -> proceed(args, ctx, filtersSettings, role));
		}
		else {
			ctx.replyError(wrongSyntax);
		}
	}

	private void proceed(String[] args, CommandContext ctx, GuildFiltersSettings filtersSettings, IMentionable entity) {
		var id = entity.getIdLong();
		var i18n = ctx.getI18n();
		var mention = entity.getAsMention();
		if (args[0].equalsIgnoreCase("add")) {
			if (entity instanceof User) {
				if (filtersSettings.isUserIgnored(id)) {
					ctx.replyErrorLocalized("commands.invitedeleting.other.already_ignored", "User", mention);
				}
				else {
					filtersSettings.addIgnoredUser(id);
					ctx.replyLocalized("commands.invitedeleting.other.done.ignored.added", "User", mention);
				}
			}
			else if (entity instanceof Role) {
				if (filtersSettings.isRoleIgnored(id)) {
					ctx.replyErrorLocalized("commands.invitedeleting.other.already_ignored", "Role", mention);
				}
				else {
					filtersSettings.addIgnoredRole(id);
					ctx.replyLocalized("commands.invitedeleting.other.done.ignored.added", "Role", mention);
				}
			}
		}
		else if (args[0].equalsIgnoreCase("remove")) {
			if (entity instanceof User) {
				if (filtersSettings.isUserIgnored(id)) {
					filtersSettings.removeIgnoredUser(id);
					ctx.replyLocalized("commands.invitedeleting.other.done.ignored.removed", "User", mention);
				}
				else {
					ctx.replyErrorLocalized("commands.invitedeleting.other.not_ignored", "User", mention);
				}
			}
			else if (entity instanceof Role) {
				if (filtersSettings.isRoleIgnored(id)) {
					filtersSettings.removeIgnoredRole(id);
					ctx.replyLocalized("commands.invitedeleting.other.done.ignored.removed", "Role", mention);
				}
				else {
					ctx.replyErrorLocalized("commands.invitedeleting.other.not_ignored", "Role", mention);
				}
			}
		}
		else {
			var prefix = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong()).getPrefix();
			ctx.replyErrorLocalized("command_failures.wrong_syntax", prefix, "invitedeleting");
		}
	}

	private void listIgnored(String[] args, CommandContext ctx, GuildFiltersSettings filtersSettings) {
		var embedBuilder = Utils.createEmbedBuilder(ctx.getAuthor());
		var i18n = ctx.getI18n();
		var prefix = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong()).getPrefix();
		var wrongSyntax = i18n.get("command_failures.wrong_syntax", prefix, "invitedeleting");
		if (args.length == 1) {
			embedBuilder.setAuthor(i18n.get("commands.invitedeleting.other.listing.all"));
			var ignoredUsers = filtersSettings.getIgnoredUsers();
			var ignoredRoles = filtersSettings.getIgnoredRoles();
			embedBuilder.appendDescription(i18n.get("commands.invitedeleting.other.listing.ignored.users")).appendDescription(":");
			embedBuilder.appendDescription(ignoredUsers.isEmpty()
					? " " + i18n.get("commands.invitedeleting.other.listing.none")
					: "\n" + ignoredUsers.stream().map(userId -> "<@" + userId + ">").collect(Collectors.joining("\n")));
			embedBuilder.appendDescription(i18n.get("commands.invitedeleting.other.listing.ignored.roles")).appendDescription(":");
			embedBuilder.appendDescription(ignoredRoles.isEmpty()
					? " " + i18n.get("commands.invitedeleting.other.listing.none")
					: "\n" + ignoredRoles.stream().map(roleId -> "<@&" + roleId + ">").collect(Collectors.joining("\n")));
		}
		else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("user")) {
				var ignoredUsers = filtersSettings.getIgnoredUsers();
				embedBuilder.setAuthor(i18n.get("commands.invitedeleting.other.listing.users"));
				embedBuilder.appendDescription(ignoredUsers.isEmpty() ? " None" : ignoredUsers.stream().map(userId -> "<@" + userId + ">").collect(Collectors.joining("\n")));
			}
			else if (args[1].equalsIgnoreCase("role")) {
				var ignoredRoles = filtersSettings.getIgnoredRoles();
				embedBuilder.setAuthor(i18n.get("commands.invitedeleting.other.listing.roles"));
				embedBuilder.appendDescription(ignoredRoles.isEmpty() ? " None" : ignoredRoles.stream().map(roleId -> "<@&" + roleId + ">").collect(Collectors.joining("\n")));
			}
			else {
				ctx.replyError(wrongSyntax);
				return;
			}
		}
		else {
			ctx.replyError(wrongSyntax);
			return;
		}
		ctx.reply(embedBuilder);
	}
}