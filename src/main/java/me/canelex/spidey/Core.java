package me.canelex.spidey;

import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.EventWaiter;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Core
{
	protected static final Map<String, Command> commands = new HashMap<>();
	private static final Logger LOG = LoggerFactory.getLogger(Core.class);
	private static JDA jda;
	private static final EventWaiter waiter = new EventWaiter();

	public static void main(final String[] args)
	{
		try
		{
			jda = JDABuilder.create(System.getenv("SpideyDev"), EnumSet.of(GUILD_BANS, GUILD_INVITES, GUILD_MEMBERS, GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS, GUILD_EMOJIS))
							.disableCache(CacheFlag.MEMBER_OVERRIDES)
							.addEventListeners(new Events(), waiter)
							.setStatus(OnlineStatus.DO_NOT_DISTURB)
							.build()
							.awaitReady();
		}
		catch (final Exception e)
		{
			LOG.error("There was an error while building JDA!", e);
		}
        Utils.startup();
	}

	public static JDA getJDA()
	{
		return jda;
	}

	public static EventWaiter getWaiter()
	{
		return waiter;
	}

	public static Map<String, Command> getCommands()
	{
		return commands;
	}
}