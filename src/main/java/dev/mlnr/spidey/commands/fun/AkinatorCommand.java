package dev.mlnr.spidey.commands.fun;

import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Server;
import dev.mlnr.spidey.objects.interactions.buttons.AkinatorGame;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class AkinatorCommand extends Command {
	public AkinatorCommand() {
		super("akinator", "Creates a new akinator game", Category.FUN, Permission.UNKNOWN, 0,
				new OptionData(OptionType.STRING, "type", "The type of the subject to have Akinator guess")
						.addChoices(Utils.getChoicesFromEnum(AkinatorGame.Type.class)));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var componentActionCache = ctx.getCache().getComponentActionCache();
		if (componentActionCache.getAction(ctx.getUser().getId()) != null) {
			ctx.replyErrorLocalized("commands.akinator.in_game");
			return false;
		}
		ctx.deferAndRun(false, () -> {
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
				ctx.sendFollowupError("commands.akinator.unavailable");
			}
		});
		return true;
	}
}