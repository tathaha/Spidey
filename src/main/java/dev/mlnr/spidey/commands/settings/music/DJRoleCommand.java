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
	public boolean execute(String[] args, CommandContext ctx) {
		var musicSettings = ctx.getCache().getGuildSettingsCache().getMusicSettings(ctx.getGuild().getIdLong());
		var dbRole = musicSettings.getDJRoleId();
		if (args.length == 0) {
			if (dbRole == 0) {
				ctx.replyErrorLocalized("roles.not_set", "DJ");
				return false;
			}
			musicSettings.removeDJRole();
			ctx.replyLocalized("roles.removed", "DJ");
			return true;
		}
		ctx.getArgumentAsRole(0, role -> {
			if (role.isPublicRole() || role.isManaged()) {
				ctx.replyErrorLocalized("roles.invalid");
				return;
			}
			var roleId = role.getIdLong();
			if (roleId == dbRole) {
				musicSettings.removeDJRole();
				ctx.replyLocalized("roles.reset", "DJ");
				return;
			}
			if (!ctx.getMember().canInteract(role)) {
				ctx.replyErrorLocalized("roles.cant_interact", "DJ");
				return;
			}
			musicSettings.setDJRoleId(roleId);
			ctx.replyLocalized("roles.set", "DJ", role.getAsMention());
		});
		return true;
	}
}