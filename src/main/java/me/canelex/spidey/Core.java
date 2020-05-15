package me.canelex.spidey;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.EventWaiter;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Core
{
	protected static final Map<String, ICommand> commands = new HashMap<>();
	private static final Logger LOG = LoggerFactory.getLogger(Core.class);
	private static JDA jda;
	private static final EventWaiter waiter = new EventWaiter();

	public static void main(final String[] args)
	{
		try
		{
			jda = JDABuilder.create(System.getenv("Spidey"), EnumSet.of(GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGE_REACTIONS))
					.disableCache(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES))
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

	public static Map<String, ICommand> getCommands()
	{
		return commands;
	}
}