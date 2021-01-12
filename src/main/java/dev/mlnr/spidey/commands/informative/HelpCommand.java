package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.cache.GuildSettingsCache;
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
    public HelpCommand()
    {
        super("help", new String[]{"commands", "cmds"}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var commandsMap = CommandHandler.getCommands();
        final var author = ctx.getAuthor();
        final var guildId = ctx.getGuild().getIdLong();
        final var prefix = GuildSettingsCache.getPrefix(guildId);
        final var i18n = ctx.getI18n();
        final var eb = Utils.createEmbedBuilder(author)
                .setAuthor(i18n.get("commands.help.other.text"), "https://github.com/caneleex/Spidey",
                        ctx.getJDA().getSelfUser().getEffectiveAvatarUrl());

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
            eb.setDescription(i18n.get("commands.help.other.embed_content", prefix, sb.toString(), prefix));
            if (hidden > 0)
                eb.appendDescription(i18n.get("commands.help.other.hidden.text", hidden));
            if (nsfwHidden)
                eb.appendDescription(i18n.get("commands.help.other.hidden.nsfw"));
            ctx.reply(eb);
            return;
        }
        final var invoke = args[0].toLowerCase();
        final var command = commandsMap.get(invoke);
        if (command == null)
        {
            final var similar = StringUtils.getSimilarCommand(invoke);
            ctx.replyError(i18n.get("command_failures.invalid.message", invoke) + " " + (similar == null
                    ? i18n.get("command_failures.invalid.check_help", prefix)
                    : i18n.get("command_failures.invalid.suggestion", similar)));
            return;
        }
        final var none = i18n.get("commands.help.other.command_info.info_none");
        final var requiredPermission = command.getRequiredPermission();
        final var aliases = command.getAliases();
        final var cooldown = CooldownHandler.getCooldown(guildId, command);

        eb.setAuthor(i18n.get("commands.help.other.viewing") + " - " + invoke);
        eb.addField(i18n.get("commands.help.other.command_info.description"),
                i18n.get("commands." + invoke + ".description"), false);

        eb.addField(i18n.get("commands.help.other.command_info.usage"),
                "`" + prefix + i18n.get("commands." + invoke + ".usage") + "` " +
                        i18n.get("commands.help.other.command_info.usage_required_optional"), false);

        eb.addField(i18n.get("commands.help.other.command_info.category"), command.getCategory().getFriendlyName(), false);
        eb.addField(i18n.get("commands.help.other.command_info.required_permission"), requiredPermission == Permission.UNKNOWN
                ? none : requiredPermission.getName(), false);
        eb.addField(i18n.get("commands.help.other.command_info.aliases"), aliases.length == 0 ? none : String.join(", ", aliases), false);
        eb.addField(i18n.get("commands.help.other.command_info.cooldown"), cooldown == 0 ? none : cooldown + " " +
                i18n.get("commands.help.other.command_info.seconds"), false);

        if (!GuildSettingsCache.isVip(guildId))
        {
            eb.addBlankField(false);
            eb.addField(i18n.get("commands.help.other.donate.title"), i18n.get("commands.help.other.donate.text"), false);
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