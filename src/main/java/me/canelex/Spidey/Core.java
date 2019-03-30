package me.canelex.Spidey;

import java.util.HashMap;

import me.canelex.Spidey.commands.AvatarCommand;
import me.canelex.Spidey.commands.BanCommand;
import me.canelex.Spidey.commands.DeleteCommand;
import me.canelex.Spidey.commands.EightBallCommand;
import me.canelex.Spidey.commands.EvalCommand;
import me.canelex.Spidey.commands.GifCommand;
import me.canelex.Spidey.commands.GuildCommand;
import me.canelex.Spidey.commands.HelpCommand;
import me.canelex.Spidey.commands.InfoCommand;
import me.canelex.Spidey.commands.JoindateCommand;
import me.canelex.Spidey.commands.LeaveCommand;
import me.canelex.Spidey.commands.LogCommand;
import me.canelex.Spidey.commands.MembercountCommand;
import me.canelex.Spidey.commands.MuteCommand;
import me.canelex.Spidey.commands.PingCommand;
import me.canelex.Spidey.commands.PollCommand;
import me.canelex.Spidey.commands.RedditCommand;
import me.canelex.Spidey.commands.RolesCommand;
import me.canelex.Spidey.commands.SayCommand;
import me.canelex.Spidey.commands.SearchCommand;
import me.canelex.Spidey.commands.SlowmodeCommand;
import me.canelex.Spidey.commands.SupportGuildsCommand;
import me.canelex.Spidey.commands.UptimeCommand;
import me.canelex.Spidey.commands.UrbanDictionaryCommand;
import me.canelex.Spidey.commands.UserCommand;
import me.canelex.Spidey.commands.WarnCommand;
import me.canelex.Spidey.commands.YouTubeChannelCommand;
import me.canelex.Spidey.objects.command.CommandParser;
import me.canelex.Spidey.objects.command.ICommand;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Core {	
	
	public static final CommandParser parser = new CommandParser();
	public static final HashMap<String, ICommand> commands = new HashMap<String, ICommand>();	
	
	public static final void main(final String[] args) throws Exception {
    	
		@SuppressWarnings("unused")
		final JDA jda = new JDABuilder(AccountType.BOT)
    			.setToken(Secrets.token)
    			.addEventListeners(new Events())
    			.setActivity(Activity.streaming("discord.gg/cnAgKrv", "https://twitch.tv/canelex_"))    
    			.build().awaitReady();      
		
		setupCommands();
        
    } 	
	
	public static final void setupCommands() {
		
		commands.clear();
		commands.put("guild", new GuildCommand());
		commands.put("help", new HelpCommand());
		commands.put("info", new InfoCommand());
		commands.put("joindate", new JoindateCommand());	
		commands.put("log", new LogCommand());
		commands.put("membercount", new MembercountCommand());
		commands.put("mute", new MuteCommand());
		commands.put("ping", new PingCommand());
		commands.put("warn", new WarnCommand());	
		commands.put("ban", new BanCommand());
		commands.put("poll", new PollCommand());
		commands.put("uptime", new UptimeCommand());
		commands.put("delete", new DeleteCommand());
		commands.put("user", new UserCommand());
		commands.put("avatar", new AvatarCommand());
		commands.put("leave", new LeaveCommand());
		commands.put("say", new SayCommand());
		commands.put("sguilds", new SupportGuildsCommand());
		commands.put("g", new SearchCommand());
		commands.put("yt", new SearchCommand());	
		commands.put("reddit", new RedditCommand());
		commands.put("ytchannel", new YouTubeChannelCommand());
		commands.put("ud", new UrbanDictionaryCommand());
		commands.put("8ball", new EightBallCommand());
		commands.put("gif", new GifCommand());
		commands.put("slowmode", new SlowmodeCommand());
		commands.put("roles", new RolesCommand());
		commands.put("eval", new EvalCommand());
		
	}
	
	public static final void handleCommand(final CommandParser.CommandContainer cmd) {
		
		if (commands.containsKey(cmd.invoke)) {
			
			final boolean safe = commands.get(cmd.invoke).called(cmd.event);
			
			if (!safe) {
				
				commands.get(cmd.invoke).executed(safe, cmd.event);
				return;
				
			}
			
			commands.get(cmd.invoke).action(cmd.event);
			commands.get(cmd.invoke).executed(safe, cmd.event);
			
		}
	}	
    
}
