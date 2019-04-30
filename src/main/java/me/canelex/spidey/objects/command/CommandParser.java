package me.canelex.spidey.objects.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser {

	public final CommandContainer parse(final String rw, final GuildMessageReceivedEvent e){

		final String beheaded = rw.replaceFirst("s!", "");
		final String[] splitbeheaded = beheaded.split(" ");
		final ArrayList<String> split = new ArrayList<>(Arrays.asList(splitbeheaded));
		final String invoke = split.get(0).toLowerCase();
		final String[] args = new String[split.size() - 1];
		split.subList(1, split.size()).toArray(args);

		return new CommandContainer(rw, beheaded, splitbeheaded, invoke, args, e);

	}

	public final class CommandContainer {

		final String raw;
		final String beheaded;
		final String[] splitbeheadded;
		public final String invoke;
		final String[] args;
		public final GuildMessageReceivedEvent event;

		CommandContainer(String rw, String beheaded, String[] splitbeheaded, String invoke, String[] args, GuildMessageReceivedEvent e){

			this.raw = rw;
			this.beheaded = beheaded;
			this.splitbeheadded = splitbeheaded;
			this.invoke = invoke;
			this.args = args;
			this.event = e;

		}

	}

}