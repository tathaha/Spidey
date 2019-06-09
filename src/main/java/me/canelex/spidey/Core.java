package me.canelex.spidey;

import me.canelex.spidey.objects.command.CommandParser;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
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

			JDABuilder jda = new JDABuilder(AccountType.BOT)
                    .setToken(Secrets.TOKEN)
                    .addEventListeners(new Events())
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setActivity(Activity.listening("to your commands"));
			for (int i = 0; i < 10; i++) {
			    jda.useSharding(i, 10).build().awaitReady();
            }

		} catch (Exception e) {
			logger.error("Exception!", e);
		}

        Utils.initializeCommands();
	}

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

	static void handleCommand(final CommandParser.CommandContainer cmd) {

		if (commands.containsKey(cmd.invoke)) {
			EXECUTOR.submit(() -> commands.get(cmd.invoke).action(cmd.event));
		}

	}

}
