package dev.mlnr.spidey.commands.slash.informative;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class LegalSlashCommand extends SlashCommand {
	public LegalSlashCommand() {
		super("legal", "Shows you the Privacy Policy", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		ctx.replyLocalized("commands.legal.text");
		return true;
	}
}