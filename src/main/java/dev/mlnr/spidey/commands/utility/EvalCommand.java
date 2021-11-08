package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.script.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EvalCommand extends Command {
	private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("groovy");
	private static final List<String> DEFAULT_IMPORTS = Arrays.asList("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api", "java.lang",
			"java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream");

	public EvalCommand() {
		super("eval", "Evals java code", Category.UTILITY, Permission.UNKNOWN, 0,
				new OptionData(OptionType.STRING, "code", "The code to eval", true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		ctx.deferAndRun(ctx.shouldHideResponse(), () -> {
			var author = ctx.getUser();
			var jda = ctx.getJDA();
			var channel = ctx.getTextChannel();
			SCRIPT_ENGINE.put("guild", channel.getGuild());
			SCRIPT_ENGINE.put("author", author);
			SCRIPT_ENGINE.put("member", ctx.getMember());
			SCRIPT_ENGINE.put("channel", channel);
			SCRIPT_ENGINE.put("jda", jda);
			SCRIPT_ENGINE.put("api", jda);
			SCRIPT_ENGINE.put("cache", ctx.getCache());
			var embedBuilder = Utils.createEmbedBuilder(author);
			var toEval = new StringBuilder();
			DEFAULT_IMPORTS.forEach(imp -> toEval.append("import ").append(imp).append(".*; "));
			toEval.append(ctx.getStringOption("code"));
			try {
				var evaluated = SCRIPT_ENGINE.eval(toEval.toString());
				if (evaluated == null) {
					return;
				}
				embedBuilder.setColor(Color.GREEN);
				embedBuilder.setDescription("```" + evaluated + "```");
			}
			catch (ScriptException ex) {
				embedBuilder.setColor(Color.RED);
				embedBuilder.setDescription("```" + ex.getMessage() + "```");
			}
			ctx.sendFollowup(embedBuilder);
		});
		return true;
	}
}