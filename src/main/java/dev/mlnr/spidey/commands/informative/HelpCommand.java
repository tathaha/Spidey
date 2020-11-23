package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.handlers.command.CooldownHandler;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.StringUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.*;

@SuppressWarnings("unused")
public class HelpCommand extends Command
{
    private static final String COOLDOWN_REDUCE_HALF = "If you want to reduce the commands' cooldown by half, you can achieve so by donating at least 2€.";

    public HelpCommand()
    {
        super("help", new String[]{"commands", "cmds"}, "Shows the help message", "help (command)", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var commandsMap = CommandHandler.getCommands();
        final var author = ctx.getAuthor();
        final var guildId = ctx.getGuild().getIdLong();
        final var prefix = GuildSettingsCache.getPrefix(guildId);
        final var eb = Utils.createEmbedBuilder(author)
                .setColor(0xFEFEFE)
                .setAuthor("Spidey's commands", "https://github.com/caneleex/Spidey", ctx.getJDA().getSelfUser().getEffectiveAvatarUrl());

        if (args.length == 0)
        {
            final var commandsCopy = new HashMap<>(commandsMap);
            final var entries = commandsCopy.entrySet();
            entries.removeIf(entry -> !ctx.getMember().hasPermission(entry.getValue().getRequiredPermission()));
            final var hidden = commandsMap.size() - commandsCopy.size();
            final var iter = entries.iterator();
            final var valueSet = new HashSet<>();
            while (iter.hasNext())
            {
                if (!valueSet.add(iter.next().getValue()))
                    iter.remove();
            }
            commandsCopy.remove("help");
            commandsCopy.remove("eval");
            final var categories = new EnumMap<Category, List<Command>>(Category.class);
            var nsfwHidden = false;
            commandsCopy.values().forEach(cmd -> categories.computeIfAbsent(cmd.getCategory(), k -> new ArrayList<>()).add(cmd));
            if (!ctx.getTextChannel().isNSFW())
            {
                categories.remove(Category.NSFW);
                nsfwHidden = true;
            }

            final var sb = new StringBuilder();
            categories.forEach((category, commandz) ->
            {
                sb.append("\n");
                sb.append(category.getFriendlyName());
                sb.append(" ").append("-").append(" ");
                sb.append(listToString(commandz));
            });
            eb.setDescription("Prefix: **" + prefix + "**\n" + sb + "\n\nTo see more info about a command, type `" + prefix + "help <command>`.");
            if (hidden > 0)
                eb.appendDescription("\n**" + hidden + "** commands were hidden as you don't have permissions to use them.");
            if (nsfwHidden)
                eb.appendDescription("\nNSFW commands were hidden from the help msg. If you want to see all NSFW commands, type the help command in a NSFW channel.");
            ctx.reply(eb);
            return;
        }
        final var invoke = args[0].toLowerCase();
        final var command = commandsMap.get(invoke);
        if (command == null)
        {
            final var similar = StringUtils.getSimilarCommand(invoke);
            ctx.replyError("**" + invoke + "** isn't a valid command. " + (similar == null ? "Check `" + prefix + "help` for a list of commands." : "Did you perhaps mean **" + similar + "**?"), false);
            return;
        }
        final var requiredPermission = command.getRequiredPermission();
        final var aliases = command.getAliases();
        final var cooldown = CooldownHandler.getCooldown(guildId, command);
        eb.setAuthor("Viewing command info - " + invoke);
        eb.setColor(0xFEFEFE);
        eb.addField("Description", command.getDescription(), false);
        eb.addField("Usage", "`" + prefix + command.getUsage() + "` (<> = required, () = optional)", false);
        eb.addField("Category",  command.getCategory().getFriendlyName(), false);
        eb.addField("Required permission", requiredPermission == Permission.UNKNOWN ? "None" : requiredPermission.getName(), false);
        eb.addField("Aliases", aliases.length == 0 ? "None" : String.join(", ", aliases), false);
        eb.addField("Cooldown", cooldown == 0 ? "None" : cooldown + " seconds", false);

        if (!GuildSettingsCache.isVip(guildId))
        {
            eb.addBlankField(false);
            eb.addField("Reducing commands' cooldown", COOLDOWN_REDUCE_HALF + "\nBy donating, you also support the Developer and"
                    + " help cover hosting fees. Type `" + prefix + "info` for the PayPal link. Thank you!", false);
        }
        ctx.reply(eb);
    }

    private String listToString(final List<Command> commands)
    {
        final var builder = new StringBuilder();
        for (var i = 0; i < commands.size(); i++)
        {
            final var cmd = commands.get(i);
            builder.append("`").append(cmd.getInvoke()).append("`");
            if (i != commands.size() - 1)
                builder.append(", ");
        }
        return builder.toString();
    }
}