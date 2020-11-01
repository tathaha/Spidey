package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static dev.mlnr.spidey.utils.Utils.addReaction;

@SuppressWarnings("unused")
public class EvalCommand extends Command
{
    private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("groovy");
    private static final List<String> DEFAULT_IMPORTS = Arrays.asList("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api", "java.lang",
            "java.io", "java.math", "java.util", "java.util.concurrent", "java.time");

    public EvalCommand()
    {
        super("eval", new String[]{}, "Evals java code (developer only)", "eval <code>", Category.UTILITY, Permission.UNKNOWN, 1, 0);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var author = msg.getAuthor();
        final var jda = msg.getJDA();
        final var channel = msg.getTextChannel();
        if (author.getIdLong() != 394607709741252621L)
        {
            Utils.returnError("This command can only be executed by the Developer", msg);
            return;
        }
        var input = args[0];
        if (input.startsWith("```") && input.endsWith("```"))
            input = input.substring(3, input.length() - 3);
        SCRIPT_ENGINE.put("guild", msg.getGuild());
        SCRIPT_ENGINE.put("author", author);
        SCRIPT_ENGINE.put("msg", msg);
        SCRIPT_ENGINE.put("message", msg);
        SCRIPT_ENGINE.put("channel", channel);
        SCRIPT_ENGINE.put("jda", jda);
        SCRIPT_ENGINE.put("api", jda);
        final var eb = Utils.createEmbedBuilder(author);
        eb.addField("Input", "```java\n" + input + "```", false);
        final var toEval = new StringBuilder();
        DEFAULT_IMPORTS.forEach(imp -> toEval.append("import ").append(imp).append(".*; "));
        toEval.append(input);
        try
        {
            final var evaluated = SCRIPT_ENGINE.eval(toEval.toString());
            addReaction(msg, Emojis.CHECK);
            eb.setAuthor("SUCCESSFULLY EVALUATED");
            eb.setColor(Color.GREEN);
            if (evaluated != null)
                eb.addField("Output", "```" + evaluated + "```", false);
        }
        catch (final ScriptException ex)
        {
            addReaction(msg, Emojis.CROSS);
            eb.setAuthor("EVALUATING FAILED");
            eb.setColor(Color.RED);
            eb.addField("Error", "```" + ex.getMessage() + "```", false);
        }
        Utils.sendMessage(channel, eb.build());
    }
}