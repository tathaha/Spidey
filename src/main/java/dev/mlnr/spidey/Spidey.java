package dev.mlnr.spidey;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import dev.mlnr.spidey.events.ReadyEvents;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class Spidey {
	private static final Logger logger = LoggerFactory.getLogger(Spidey.class);
	private final JDA jda;

	private final DatabaseManager databaseManager = new DatabaseManager();

	public Spidey() throws LoginException, InterruptedException {
		I18n.loadLanguages();
		MusicUtils.registerSources();

		RestAction.setDefaultFailure(null);
		MessageAction.setDefaultMentions(EnumSet.noneOf(Message.MentionType.class));

		jda = JDABuilder.create(System.getenv("Spidey"),
				GUILD_BANS,
				GUILD_INVITES,
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
	}

	public static void main(String[] args) {
		try {
			new Spidey();
		}
		catch (Exception e) {
			logger.error("There was an error while building JDA", e);
		}
	}

	public JDA getJDA() {
		return jda;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
}