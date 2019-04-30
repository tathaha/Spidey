package me.canelex.spidey;

import me.canelex.spidey.commands.*;
import me.canelex.spidey.objects.command.CommandParser;
import me.canelex.spidey.objects.command.ICommand;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Core {

	static final CommandParser parser = new CommandParser();
	protected static final Map<String, ICommand> commands = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(Core.class);

	public static void main(final String[] args) {

		try {

			new JDABuilder(AccountType.BOT)
					.setToken(Secrets.TOKEN)
					.addEventListeners(new Events())
					.setActivity(Activity.streaming("discord.gg/cnAgKrv", "https://twitch.tv/canelex_"))
					.build().awaitReady();

		}

		catch (Exception e) {

			logger.error("Exception!", e);

		}

		setupCommands();

	}

	private static void setupCommands() {

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
		commands.put("d", new DeleteCommand());
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
		commands.put("invite", new InviteCommand());

	}

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();


	static void handleCommand(final CommandParser.CommandContainer cmd) {

		if (commands.containsKey(cmd.invoke)) {

			EXECUTOR.submit(() -> {
				final boolean safe = commands.get(cmd.invoke).called(cmd.event);

				commands.get(cmd.invoke).action(cmd.event);
				commands.get(cmd.invoke).executed(safe, cmd.event);
			});

		}

	}

}
