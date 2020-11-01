package dev.mlnr.spidey;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import dev.mlnr.spidey.utils.EventWaiter;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class Core
{
	private static final Logger LOG = LoggerFactory.getLogger(Core.class);
	private static final ScheduledExecutorService EXECUTOR = ThreadingConfig.newScheduler(1, () -> "Spidey", "Misc");
	private static final EventWaiter waiter = new EventWaiter(EXECUTOR, true);

	private Core() {}

	public static void main(final String[] args)
	{
		try
		{
			JDABuilder.create(System.getenv("Spidey"),
						GUILD_BANS,
						GUILD_INVITES,
						GUILD_MEMBERS,
						GUILD_MESSAGES,
						GUILD_MESSAGE_REACTIONS,
						GUILD_EMOJIS,
						GUILD_VOICE_STATES
					)
					.disableCache(
						MEMBER_OVERRIDES,
						ACTIVITY,
						CLIENT_STATUS // i disable these last 2 cacheflags explicitly so i don't get warnings
					)
					.setChunkingFilter(ChunkingFilter.exclude(264445053596991498L)) // DBL
					.addEventListeners(new Events(), waiter)
					.setActivity(Activity.watching("myself load.."))
					.setStatus(OnlineStatus.DO_NOT_DISTURB)
					.setGatewayEncoding(GatewayEncoding.ETF)
					.setAudioSendFactory(new NativeAudioSendFactory())
					.build()
					.awaitReady();
			RestAction.setDefaultFailure(null);
		}
		catch (final Exception e)
		{
			LOG.error("There was an error while building JDA!", e);
		}
	}

	public static EventWaiter getWaiter()
	{
		return waiter;
	}

	public static ScheduledExecutorService getExecutor()
	{
		return EXECUTOR;
	}
}