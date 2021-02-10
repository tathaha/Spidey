package dev.mlnr.spidey.commands.settings.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class DJRoleCommand extends Command {

	public DJRoleCommand() {
		super("djrole", new String[]{}, Category.Settings.MUSIC, Permission.MANAGE_SERVER, 1, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var musicSettings = ctx.getCache().getGuildSettingsCache().getMusicSettings(ctx.getGuild().getIdLong());
		var dbRole = musicSettings.getDJRoleId();
		var i18n = ctx.getI18n();
		if (args.length == 0) {
			if (dbRole == 0) {
				ctx.replyError(i18n.get("roles.not_set", "DJ"));
				return;
			}
			musicSettings.removeDJRole();
			ctx.reply(i18n.get("roles.removed", "DJ"));
			return;
		}
		ctx.getArgumentAsRole(0, role -> {
			if (role.isPublicRole() || role.isManaged()) {
				ctx.replyError(i18n.get("roles.invalid"));
				return;
			}
			var roleId = role.getIdLong();
			if (roleId == dbRole) {
				musicSettings.removeDJRole();
				ctx.reply(i18n.get("roles.reset", "DJ"));
				return;
			}
			if (!ctx.getMember().canInteract(role)) {
				ctx.replyError(i18n.get("roles.cant_interact", "DJ"));
				return;
			}
			musicSettings.setDJRoleId(roleId);
			ctx.reply(i18n.get("roles.set", "DJ", role.getAsMention()));
		});
	}
}