package me.canelex.spidey.commands;

import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public class HelpCommand extends Core implements ICommand {

    @Override
    public final void action(final GuildMessageReceivedEvent e) {

        final HashMap<String, ICommand> commands = new HashMap<>();

        final var args = e.getMessage().getContentRaw().split("\\s+");
        final var emb = Utils.createEmbedBuilder(e.getAuthor())
                .setColor(Color.WHITE)
                .setAuthor("Spidey's Commands", "https://github.com/caneleex/Spidey", e.getJDA().getSelfUser().getEffectiveAvatarUrl());

        if (args.length < 2) {
            for (final var entry : Core.commands.entrySet()) {
                commands.put(entry.getKey(), entry.getValue());
            }
            commands.remove("help");

            if (!Utils.hasPerm(Objects.requireNonNull(e.getMember()), Permission.BAN_MEMBERS)) {
                commands.keySet().removeIf(com -> Core.commands.get(com).isAdmin());
            }

            final HashMap<Category, List<ICommand>> categories = new HashMap<>();
            for (final var cmd : commands.values()) {
                final var list = categories.computeIfAbsent(cmd.getCategory(), ignored -> new ArrayList<>());
                list.add(cmd);
            }
            final var sb = new StringBuilder();
            categories.forEach((category, commandz) -> {
                sb.append("\n");
                sb.append(category.friendlyName());
                sb.append(" ").append("-").append(" ");
                sb.append(listToString(commandz, ICommand::getInvoke));
                emb.setDescription("Prefix: **s!**\n" + sb.toString());
            });
            Utils.sendMessage(e.getChannel(), emb.build());
        }
        else {
            final var cmd = e.getMessage().getContentRaw().substring(7);
            if (!Core.commands.containsKey(cmd)) {
                Utils.sendMessage(e.getChannel(), ":no_entry: **" + cmd + "** isn't a valid command.", false);
            }
            else {
                final var command = Core.commands.get(cmd);
                final var eb = Utils.createEmbedBuilder(e.getAuthor());
                eb.setAuthor("Viewing command info - " + cmd);
                eb.setColor(Color.WHITE);
                eb.addField("Description", command.getDescription() == null ? "Unspecified" : command.getDescription(), false);
                eb.addField("Usage", command.getUsage() == null ? "Unspecified" : "`" + command.getUsage() + "`", false);
                eb.addField("Category",  command.getCategory().friendlyName(), false);
                Utils.sendMessage(e.getChannel(), eb.build());
            }
        }

    }

    private String listToString(final List<ICommand> list, final Function<ICommand, String> transformer) {
        final var builder = new StringBuilder();
        for (var i = 0; i < list.size(); i++) {
            final var cmd = list.get(i);
            builder.append("`").append(transformer.apply(cmd)).append("`");
            if (i != list.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    @Override
    public final String getInvoke() { return "help"; }
    @Override
    public final String getDescription() { return "Shows help message"; }
    @Override
    public final String getUsage() { return "s!help (<command>)"; }
    @Override
    public final Category getCategory() { return Category.INFORMATIVE; }
    @Override
    public final boolean isAdmin() { return false; }

}