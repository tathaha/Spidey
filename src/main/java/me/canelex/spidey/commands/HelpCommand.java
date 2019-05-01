package me.canelex.spidey.commands;

import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HelpCommand extends Core implements ICommand {

    @Override
    public final void action(final GuildMessageReceivedEvent e) {

        final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor())
                .setColor(Color.WHITE)
                .setAuthor("Spidey's Commands", "https://github.com/caneleex/Spidey", e.getJDA().getSelfUser().getEffectiveAvatarUrl());

        final StringBuilder sb = new StringBuilder();

        final HashMap<String, ICommand> commands = new HashMap<>();

        for (final Map.Entry<String, ICommand> entry : Core.commands.entrySet()) {

            commands.put(entry.getKey(), entry.getValue());

        }

        commands.remove("yt");

        for (final String cmd : commands.keySet()) {

            if (cmd.equals("g")) {

                sb.append("`s!").append(cmd).append("` | `s!yt` - ").append(Core.commands.get(cmd).help()).append("\n");

            }

            else {

                sb.append("`s!").append(cmd).append("` - ").append(Core.commands.get(cmd).help()).append("\n");

            }

            eb.setDescription(sb.toString());

        }

        API.sendMessage(e.getChannel(), eb.build());

    }

    @Override
    public final String help() {

        return "Shows you this message";

    }

}