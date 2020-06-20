package me.canelex.spidey.commands.informative;

import me.canelex.spidey.Core;
import me.canelex.spidey.objects.cache.Cache;
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
    private static final String COOLDOWN_REDUCE_HALF = "If you want to reduce the commands' cooldown by half, you can achieve so by "
            + "donating at least 1€ or if you want to completely remove cooldowns, you *have* to donate at least 3€.";
    private static final String COOLDOWN_REMOVE = "If you want to completely remove the commands' cooldown, you can achieve so by donating at least 3€.";

    public HelpCommand()
    {
        super("help", new String[]{}, "Shows the help message", "help (command)", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var commandsMap = Core.getCommands();
        final var channel = message.getTextChannel();
        final var author = message.getAuthor();
        final var guildId = message.getGuild().getIdLong();
        final var prefix = Cache.getPrefix(guildId);
        final var eb = Utils.createEmbedBuilder(author)
                .setColor(Color.WHITE)
                .setAuthor("Spidey's Commands", "https://github.com/caneleex/Spidey", message.getJDA().getSelfUser().getEffectiveAvatarUrl());

        if (args.length == 0)
        {
            final var commandsCopy = new HashMap<>(commandsMap);
            final var entries = commandsCopy.entrySet();
            entries.removeIf(entry -> !message.getMember().hasPermission(entry.getValue().getRequiredPermission()));
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
            eb.setDescription("Prefix: **" + prefix + "**\n" + sb.toString() + "\n\nTo see more info about a command, type `" + prefix + "help <command>`.");
            if (hidden > 0)
                eb.appendDescription("\n**" + hidden + "** commands were hidden as you don't have permissions to use them.");
            Utils.sendMessage(channel, eb.build());
        }
        else
        {
            final var cmd = args[0].toLowerCase();
            if (!commandsMap.containsKey(cmd))
                Utils.returnError("**" + cmd + "** isn't a valid command", message);
            else
            {
                final var command = commandsMap.get(cmd);
                final var description = command.getDescription();
                final var usage = command.getUsage();
                final var requiredPermission = command.getRequiredPermission();
                final var aliases = command.getAliases();
                final var cooldown = Cache.getCooldown(guildId, command);
                eb.setAuthor("Viewing command info - " + cmd);
                eb.setColor(Color.WHITE);
                eb.addField("Description", description == null ? "Unspecified" : description, false);
                eb.addField("Usage", usage == null ? "Unspecified" : "`" + prefix + usage + "` (<> = required, () = optional)", false);
                eb.addField("Category",  command.getCategory().getFriendlyName(), false);
                eb.addField("Required permission", requiredPermission == Permission.UNKNOWN ? "None" : requiredPermission.getName(), false);
                eb.addField("Aliases", aliases.length == 0 ? "None" : String.join(", ", aliases), false);
                eb.addField("Cooldown", cooldown == 0 ? "None" : cooldown + " seconds", false);

                final var isVip = Cache.isVip(guildId);
                final var isSupporter = Cache.isSupporter(guildId);
                var text = "";
                if (!isVip && !isSupporter)
                    text = COOLDOWN_REDUCE_HALF;
                else if (isVip && !isSupporter)
                    text = COOLDOWN_REMOVE;

                if (!text.isEmpty())
                {
                    eb.addBlankField(false);
                    eb.addField("Reducing/removing commands' cooldown", text + "\nBy donating, you also support the Developer and"
                                + " help cover hosting fees. Type `" + prefix + "info` for the PayPal link. Thank you!", false);
                }
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
            builder.append("`").append(transformer.apply(cmd)).append("`");
            if (i != list.size() - 1)
                builder.append(", ");
        }
        return builder.toString();
    }
}