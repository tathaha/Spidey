package me.canelex.Spidey.objects.command;

import java.util.ArrayList;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandParser {
	
	public final CommandContainer parse(final String rw, final GuildMessageReceivedEvent e){
		
		final ArrayList<String> split = new ArrayList<String>();
		final String raw = rw;
		final String beheaded = raw.replaceFirst("s!", "");
		final String[] SplitBeheadded = beheaded.split(" ");
		
		for (final String s : SplitBeheadded) {
			
			split.add(s);

		}
		
		final String invoke = split.get(0).toLowerCase();
		final String[] args = new String[split.size() - 1];
		split.subList(1, split.size()).toArray(args);
		
		return new CommandContainer(raw, beheaded, SplitBeheadded, invoke, args, e);
		
	}
	
	 public final class CommandContainer {
		 
		 public final String raw;
		 public final String beheaded;
		 public final String[] SplitBeheadded;
		 public final String invoke;
		 public final String[] args;
		 public final GuildMessageReceivedEvent event;
		 
		 public CommandContainer(String rw, String beheaded, String[] SplitBeheaded, String invoke, String[] args, GuildMessageReceivedEvent e){
			 
			 this.raw = rw;
			 this.beheaded = beheaded;
			 this.SplitBeheadded = SplitBeheaded;
			 this.invoke = invoke;
			 this.args = args;
			 this.event = e;
			 
		 }
	 }	

}