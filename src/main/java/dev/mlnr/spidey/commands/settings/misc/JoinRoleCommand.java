package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class JoinRoleCommand extends Command {

	public JoinRoleCommand() {
		super("joinrole", new String[]{}, Category.Settings.MISC, Permission.MANAGE_SERVER, 1, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var miscSettings = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong());
		var dbRole = miscSettings.getJoinRoleId();
		var i18n = ctx.getI18n();
		if (args.length == 0) {
			if (dbRole == 0) {
				ctx.replyError(i18n.get("roles.not_set", "join"));
				return;
			}
			miscSettings.removeJoinRole();
			ctx.reply(i18n.get("roles.removed", "join"));
			return;
		}
		ctx.getArgumentAsRole(0, role -> {
			if (role.isPublicRole() || role.isManaged()) {
				ctx.replyError(i18n.get("roles.invalid"));
				return;
			}
			var roleId = role.getIdLong();
			if (roleId == dbRole) {
				miscSettings.removeJoinRole();
				ctx.reply(i18n.get("roles.reset", "join"));
				return;
			}
			if (!ctx.getMember().canInteract(role)) {
				ctx.replyError(i18n.get("roles.cant_interact", "join"));
				return;
			}
			miscSettings.setJoinRoleId(roleId);
			ctx.reply(i18n.get("roles.set", "join", role.getAsMention()));
		});
	}
}