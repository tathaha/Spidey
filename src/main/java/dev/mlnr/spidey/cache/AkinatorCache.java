package dev.mlnr.spidey.cache;

import com.markozajc.akiwrapper.AkiwrapperBuilder;
import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.akinator.AkinatorData;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.entities.User;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AkinatorCache {
	private final Map<Long, AkinatorData> akinatorMap = ExpiringMap.builder()
			.expirationPolicy(ExpirationPolicy.ACCESSED)
			.expiration(2, TimeUnit.MINUTES)
			.build();

	public void createAkinator(User user, AkinatorContext ctx) {
		var i18n = ctx.getI18n();
		var channel = ctx.getChannel();
		try {
			var akinator = new AkiwrapperBuilder().build();
			akinatorMap.put(user.getIdLong(), new AkinatorData(akinator));
			var embedBuilder = Utils.createEmbedBuilder(user).setAuthor(i18n.get("commands.akinator.other.of", user.getAsTag())).setColor(Utils.SPIDEY_COLOR);
			embedBuilder.setDescription(i18n.get("commands.akinator.other.question", 1) + " " + akinator.getCurrentQuestion().getQuestion());
			Utils.sendMessage(channel, embedBuilder.build());
		}
		catch (Exception ex) {
			Utils.returnError(i18n.get("commands.akinator.other.couldnt_create"), ctx.getMessage());
		}
	}

	public AkinatorData getAkinatorData(long userId) {
		return akinatorMap.get(userId);
	}

	public boolean hasAkinator(long userId) {
		return akinatorMap.containsKey(userId);
	}

	public void removeAkinator(long userId) {
		akinatorMap.remove(userId);
	}
}