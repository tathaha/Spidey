package me.canelex.spidey.commands.informative;

import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unused")
public class HelpCommand extends Command
{
    public HelpCommand()
    {
        super("help", new String[]{}, "Shows the help message", "help (command)", Category.INFORMATIVE, Permission.UNKNOWN, 0);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var commandsMap = Core.getCommands();
        final var channel = message.getChannel();
        final var author = message.getAuthor();
        final var eb = Utils.createEmbedBuilder(author)
                            .setColor(Color.WHITE)
                            .setAuthor("Spidey's Commands", "https://github.com/caneleex/Spidey", message.getJDA().getSelfUser().getEffectiveAvatarUrl());
        final var prefix = Utils.getPrefix(message.getGuild().getIdLong());

        if (args.length < 2)
        {
            final var commandsCopy = new HashMap<>(commandsMap);
            final var entries = commandsCopy.entrySet();
            entries.removeIf(entry -> !Utils.hasPerm(message.getMember(), entry.getValue().getRequiredPermission()));
            final var hidden = commandsMap.size() - commandsCopy.size();
            final var iter = entries.iterator();
            final var valueSet = new HashSet<>();
            while (iter.hasNext())
            {
                if (!valueSet.add(iter.next().getValue()))
                    iter.remove();
            }
            commandsCopy.remove("help");

            final EnumMap<Category, List<Command>> categories = new EnumMap<>(Category.class);
            commandsCopy.values().forEach(cmd -> categories.computeIfAbsent(cmd.getCategory(), ignored -> new ArrayList<>()).add(cmd));

            final var sb = new StringBuilder();
            categories.forEach((category, commandz) ->
            {
                sb.append("\n");
                sb.append(category.getFriendlyName());
                sb.append(" ").append("-").append(" ");
                sb.append(listToString(commandz, Command::getInvoke));
            });
            eb.setDescription("Prefix: **" + prefix + "**\n" + sb.toString());
            if (hidden > 0)
                eb.appendDescription("\n\n **" + hidden + "** commands were hidden as you don't have permissions to use them.");
            Utils.sendMessage(channel, eb.build());
        }
        else
        {
            final var cmd = message.getContentRaw().substring(prefix.length() + 5).toLowerCase();
            if (!commandsMap.containsKey(cmd))
                Utils.returnError("**" + cmd + "** isn't a valid command", message);
            else
            {
                final var command = commandsMap.get(cmd);
                final var description = command.getDescription();
                final var usage = command.getUsage();
                final var requiredPermission = command.getRequiredPermission();
                final var aliases = command.getAliases();
                eb.setAuthor("Viewing command info - " + cmd);
                eb.setColor(Color.WHITE);
                eb.addField("Description", description == null ? "Unspecified" : description, false);
                eb.addField("Usage", usage == null ? "Unspecified" : "`" + prefix + usage + "` (<> = required, () = optional)", false);
                eb.addField("Category",  command.getCategory().getFriendlyName(), false);
                eb.addField("Required permission", requiredPermission == Permission.UNKNOWN ? "None" : requiredPermission.getName(), false);
                eb.addField("Aliases", aliases.length == 0 ? "None" : String.join(", ", aliases), false);
                Utils.sendMessage(channel, eb.build());
            }
        }
    }

    private String listToString(final List<Command> list, final Function<Command, String> transformer)
    {
        final var builder = new StringBuilder();
        for (var i = 0; i < list.size(); i++)
        {
            final var cmd = list.get(i);
            final var aliases = cmd.getAliases();
            builder.append("`").append(transformer.apply(cmd)).append("`").append(aliases.length == 0 ? "" : " (`" + String.join(", ", aliases) + "`)");
            if (i != list.size() - 1)
                builder.append(", ");
        }
        return builder.toString();
    }
}