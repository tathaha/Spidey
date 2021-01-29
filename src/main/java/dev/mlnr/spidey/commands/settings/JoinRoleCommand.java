package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class JoinRoleCommand extends Command {

	public JoinRoleCommand() {
		super("joinrole", new String[]{}, Category.SETTINGS, Permission.MANAGE_SERVER, 1, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var guildId = ctx.getGuild().getIdLong();
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var dbRole = guildSettingsCache.getJoinRoleId(guildId);
		var i18n = ctx.getI18n();
		if (args.length == 0) {
			if (dbRole == 0) {
				ctx.replyError(i18n.get("roles.not_set", "join"));
				return;
			}
			guildSettingsCache.removeJoinRole(guildId);
			ctx.reply(i18n.get("roles.removed", "join"));
			return;
		}
		ctx.getArgumentAsRole(0, role -> {
			var roleId = role.getIdLong();
			if (roleId == dbRole) {
				guildSettingsCache.removeJoinRole(guildId);
				ctx.reply(i18n.get("roles.reset", "join"));
				return;
			}
			if (!ctx.getMember().canInteract(role)) {
				ctx.replyError(i18n.get("roles.cant_interact", "join"));
				return;
			}
			guildSettingsCache.setJoinRoleId(guildId, roleId);
			ctx.reply(i18n.get("roles.set", "join", role.getAsMention()));
		});
	}
}