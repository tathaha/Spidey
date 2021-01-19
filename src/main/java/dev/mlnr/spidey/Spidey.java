package dev.mlnr.spidey;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import dev.mlnr.spidey.utils.EventWaiter;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class Spidey
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Spidey.class);
    private static final ScheduledExecutorService SCHEDULER = ThreadingConfig.newScheduler(1, () -> "Spidey", "Scheduler");
    private static final EventWaiter WAITER = new EventWaiter(SCHEDULER, true);
    private static JDA jda;

    private Spidey() {}

    public static void main(String[] args)
    {
        try
        {
            jda = JDABuilder.create(System.getenv("Spidey"),
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
                        ROLE_TAGS,
                        ACTIVITY,
                        CLIENT_STATUS // i disable these last 2 cacheflags explicitly so i don't get warnings
                    )
                    .setMemberCachePolicy(MemberCachePolicy.VOICE)
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .addEventListeners(new Events(), WAITER)
                    .setActivity(Activity.watching("myself load.."))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGatewayEncoding(GatewayEncoding.ETF)
                    .setAudioSendFactory(new NativeAudioSendFactory())
                    .build()
                    .awaitReady();
            RestAction.setDefaultFailure(null);
        }
        catch (Exception e)
        {
            LOGGER.error("There was an error while building JDA!", e);
        }
    }

    public static EventWaiter getWaiter()
    {
        return WAITER;
    }

    public static ScheduledExecutorService getScheduler()
    {
        return SCHEDULER;
    }

    public static JDA getJDA()
    {
        return jda;
    }
}