package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.Paginator;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.utils.StringUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class PaginatorCache {
	private final Map<String, Paginator> paginatorMap = ExpiringMap.builder()
			.expirationPolicy(ExpirationPolicy.CREATED)
			.expiration(10, TimeUnit.MINUTES)
			.asyncExpirationListener((paginatorId, paginator) -> removePaginator(((Paginator) paginator)))
			.build();

	public void createPaginator(CommandContext ctx, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer) {
		var embedBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);
		var i18n = ctx.getI18n();

		embedBuilder.setFooter(i18n.get("paginator.page", 1, totalPages));
		pagesConsumer.accept(0, embedBuilder);

		var paginatorId = StringUtils.randomString(10);
		paginatorMap.put(paginatorId, new Paginator(paginatorId, ctx, totalPages, i18n, this, pagesConsumer));

		var left = Button.primary(paginatorId + ":1", Emoji.ofUnicode(Emojis.BACKWARDS));
		var right = Button.primary(paginatorId + ":2", Emoji.ofUnicode(Emojis.FORWARD));
		var wastebasket = Button.primary(paginatorId + ":3", Emoji.ofUnicode(Emojis.WASTEBASKET));
		ctx.replyWithButtons(embedBuilder, left, right, wastebasket);
	}

	public Paginator getPaginator(String paginatorId) {
		return paginatorMap.get(paginatorId);
	}

	public void removePaginator(Paginator paginator) {
		paginator.getCtx().deleteReply();
		paginatorMap.remove(paginator.getId());
	}

	public boolean isPaginator(String paginatorId) {
		return paginatorMap.containsKey(paginatorId);
	}
}