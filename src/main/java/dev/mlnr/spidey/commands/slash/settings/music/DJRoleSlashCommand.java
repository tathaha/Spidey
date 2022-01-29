package dev.mlnr.spidey.commands.slash.settings.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class DJRoleSlashCommand extends SlashCommand {
	public DJRoleSlashCommand() {
		super("djrole", "Sets/removes the DJ role", Category.Settings.MUSIC, Permission.MANAGE_SERVER, 4,
				new OptionData(OptionType.ROLE, "role", "The role to set as the DJ role or blank to reset"));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		var role = ctx.getRoleOption("role");
		var musicSettings = ctx.getCache().getGuildSettingsCache().getMusicSettings(ctx.getGuild().getIdLong());
		var currentDJRoleId = musicSettings.getDJRoleId();
		if (role == null) {
			if (currentDJRoleId == 0) {
				ctx.replyErrorLocalized("roles.not_set", "DJ");
				return false;
			}
			musicSettings.removeDJRole();
			ctx.replyLocalized("roles.removed", "DJ");
			return true;
		}
		if (role.isPublicRole() || role.isManaged()) {
			ctx.replyErrorLocalized("roles.invalid");
			return false;
		}
		var roleId = role.getIdLong();
		if (roleId == currentDJRoleId) {
			musicSettings.removeDJRole();
			ctx.replyLocalized("roles.reset", "DJ");
			return true;
		}
		if (!ctx.getMember().canInteract(role)) {
			ctx.replyErrorLocalized("roles.cant_interact", "DJ");
			return false;
		}
		musicSettings.setDJRoleId(roleId);
		ctx.replyLocalized("roles.set", "DJ", role.getAsMention());
		return true;
	}
}