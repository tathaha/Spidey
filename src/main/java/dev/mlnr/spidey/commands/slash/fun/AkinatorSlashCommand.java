package dev.mlnr.spidey.commands.slash.fun;

import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Server;
import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.objects.interactions.components.buttons.AkinatorGame;
import dev.mlnr.spidey.utils.CommandUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class AkinatorSlashCommand extends SlashCommand {
	public AkinatorSlashCommand() {
		super("akinator", "Creates a new akinator game", Category.FUN, Permission.UNKNOWN, 0,
				new OptionData(OptionType.STRING, "type", "The type of the subject to have Akinator guess")
						.addChoices(CommandUtils.getChoicesFromEnum(AkinatorGame.Type.class)));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		var componentActionCache = ctx.getCache().getComponentActionCache();
		if (componentActionCache.getAction(ctx.getUser().getId()) != null) {
			ctx.replyErrorLocalized("commands.akinator.in_game");
			return false;
		}
		ctx.deferAndRun(() -> {
			try {
				var languageCode = ctx.getI18n().getLangCode();
				var language = Server.Language.getById(languageCode);
				language = language == null ? Server.Language.ENGLISH : language;

				var typeOption = ctx.getStringOption("type");
				var type = typeOption == null ? Server.GuessType.CHARACTER : Server.GuessType.valueOf(typeOption);
				var akiwrapper = new AkiwrapperBuilder().setLanguage(language).setGuessType(type).build();
				componentActionCache.createAkinator(ctx, akiwrapper);
			}
			catch (Exception ex) {
				ctx.sendFollowupErrorLocalized("commands.akinator.unavailable");
			}
		});
		return true;
	}
}