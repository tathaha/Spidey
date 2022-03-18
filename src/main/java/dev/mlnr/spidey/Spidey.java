package dev.mlnr.spidey;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import dev.mlnr.blh.core.api.BLHBuilder;
import dev.mlnr.blh.core.api.BotList;
import dev.mlnr.blh.jda.BLHJDAUpdater;
import dev.mlnr.spidey.events.ReadyEvents;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class Spidey {
	private static final Logger logger = LoggerFactory.getLogger(Spidey.class);
	private static final String BOTLIST_ENV_PREFIX = "BOTLIST_";
	private final ShardManager shardManager;

	private final DatabaseManager databaseManager = new DatabaseManager();

	public Spidey() throws LoginException, InterruptedException {
		I18n.loadLanguages();
		MusicUtils.registerSources();

		RestAction.setDefaultFailure(null);
		MessageAction.setDefaultMentions(EnumSet.noneOf(Message.MentionType.class));

		shardManager = DefaultShardManagerBuilder.create(System.getenv("Spidey"),
				GUILD_BANS,
				GUILD_MEMBERS,
				GUILD_MESSAGES,
				GUILD_EMOJIS,
				GUILD_VOICE_STATES)
			.disableCache(
				MEMBER_OVERRIDES,
				ROLE_TAGS,
				ACTIVITY,
				CLIENT_STATUS,
				ONLINE_STATUS
			)
			.setMemberCachePolicy(MemberCachePolicy.VOICE)
			.setChunkingFilter(ChunkingFilter.NONE)
			.addEventListeners(new ReadyEvents(this))
			.setActivity(Activity.watching("myself load"))
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setGatewayEncoding(GatewayEncoding.ETF)
			.setAudioSendFactory(new NativeAudioSendFactory())
			.build();

		var botLists = System.getenv().entrySet()
			.stream()
			.filter(entry -> entry.getKey().startsWith(BOTLIST_ENV_PREFIX))
			.collect(Collectors.toMap(entry -> BotList.valueOf(entry.getKey().substring(BOTLIST_ENV_PREFIX.length())), Map.Entry::getValue));
		new BLHBuilder(new BLHJDAUpdater(shardManager), botLists).setDevModePredicate(botId -> botId != Utils.SPIDEY_ID)
			.setSuccessLoggingEnabled(false)
			.setUnavailableEventsEnabled(false)
			.setErrorLoggingThreshold(2)
			.setNoUpdateNecessaryLoggingEnabled(false)
			.setAutoPostDelay(5, TimeUnit.MINUTES)
			.build();
	}

	public static void main(String[] args) {
		try {
			new Spidey();
		}
		catch (Exception e) {
			logger.error("There was an error while building JDA", e);
		}
	}

	public ShardManager getShardManager() {
		return shardManager;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
}