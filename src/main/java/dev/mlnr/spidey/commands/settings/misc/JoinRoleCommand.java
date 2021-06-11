package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class JoinRoleCommand extends CommandBase {
	public JoinRoleCommand() {
		super("joinrole", "Sets/removes the role that is added to a member after joining", Category.Settings.MISC, Permission.MANAGE_SERVER, 4,
				new OptionData(OptionType.ROLE, "role", "The role to set as the join role or blank to reset"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var role = ctx.getRoleOption("role");
		var miscSettings = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong());
		var currentJoinRoleId = miscSettings.getJoinRoleId();
		if (role == null) {
			if (currentJoinRoleId == 0) {
				ctx.replyErrorLocalized("roles.not_set", "join");
				return false;
			}
			miscSettings.removeJoinRole();
			ctx.replyLocalized("roles.removed", "join");
			return true;
		}
		if (role.isPublicRole() || role.isManaged()) {
			ctx.replyErrorLocalized("roles.invalid");
			return false;
		}
		var roleId = role.getIdLong();
		if (roleId == currentJoinRoleId) {
			miscSettings.removeJoinRole();
			ctx.replyLocalized("roles.reset", "join");
			return true;
		}
		if (!ctx.getMember().canInteract(role)) {
			ctx.replyErrorLocalized("roles.cant_interact", "join");
			return false;
		}
		miscSettings.setJoinRoleId(roleId);
		ctx.replyLocalized("roles.set", "join", role.getAsMention());
		return true;
	}
}