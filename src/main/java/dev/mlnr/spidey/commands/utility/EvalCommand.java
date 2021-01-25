package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EvalCommand extends Command {

	private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("groovy");
	private static final List<String> DEFAULT_IMPORTS = Arrays.asList("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api", "java.lang",
			"java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream");

	public EvalCommand() {
		super("eval", new String[]{}, Category.UTILITY, Permission.UNKNOWN, 1, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var author = ctx.getAuthor();
		var jda = ctx.getJDA();
		var channel = ctx.getTextChannel();
		var message = ctx.getMessage();
		if (author.getIdLong() != 394607709741252621L) {
			ctx.replyError(ctx.getI18n().get("command_failures.only_dev"));
			return;
		}
		SCRIPT_ENGINE.put("guild", channel.getGuild());
		SCRIPT_ENGINE.put("author", author);
		SCRIPT_ENGINE.put("member", ctx.getMember());
		SCRIPT_ENGINE.put("msg", message);
		SCRIPT_ENGINE.put("message", message);
		SCRIPT_ENGINE.put("channel", channel);
		SCRIPT_ENGINE.put("jda", jda);
		SCRIPT_ENGINE.put("api", jda);
		var eb = Utils.createEmbedBuilder(author);
		var toEval = new StringBuilder();
		DEFAULT_IMPORTS.forEach(imp -> toEval.append("import ").append(imp).append(".*; "));
		toEval.append(args[0]);
		try {
			var evaluated = SCRIPT_ENGINE.eval(toEval.toString());
			ctx.reactLike();
			if (evaluated == null) {
				return;
			}
			eb.setColor(Color.GREEN);
			eb.setDescription("```" + evaluated + "```");
		}
		catch (ScriptException ex) {
			Utils.addReaction(ctx.getMessage(), Emojis.DISLIKE);
			eb.setColor(Color.RED);
			eb.setDescription("```" + ex.getMessage() + "```");
		}
		ctx.reply(eb);
	}
}