package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import dev.mlnr.spidey.objects.interactions.buttons.Paginator;
import dev.mlnr.spidey.objects.interactions.buttons.PurgeProcessor;
import dev.mlnr.spidey.objects.interactions.dropdowns.YouTubeSearchDropdown;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import dev.mlnr.spidey.utils.StringUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.jodah.expiringmap.ExpiringMap;

import java.util.function.BiConsumer;

public class ComponentActionCache {
	private final ExpiringMap<String, ComponentAction> actionMap = ExpiringMap.builder()
			.variableExpiration()
			.asyncExpirationListener((actionId, actionObject) -> ((ComponentAction) actionObject).getCtx().deleteReply())
			.build();

	public void addAction(String id, ComponentAction componentAction) {
		var type = componentAction.getType();
		actionMap.put(id, componentAction, type.getExpirationPolicy(), type.getExpirationDuration(), type.getExpirationUnit());
	}

	public ComponentAction getAction(String id) {
		return actionMap.get(id);
	}

	public void removeAction(ComponentAction componentAction) {
		actionMap.remove(componentAction.getId());
		componentAction.getCtx().deleteReply();
	}

	// buttons

	public void createPaginator(CommandContext ctx, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer) {
		var embedBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);

		embedBuilder.setFooter(ctx.getI18n().get("paginator.page", 1, totalPages));
		pagesConsumer.accept(0, embedBuilder);

		var paginatorId = StringUtils.randomString(30);
		var paginator = new Paginator(paginatorId, ctx, totalPages, pagesConsumer, this);
		addAction(paginatorId, paginator);

		var left = Button.primary(paginatorId + ":BACKWARDS", Emoji.fromUnicode(Emojis.BACKWARDS));
		var right = Button.primary(paginatorId + ":FORWARD", Emoji.fromUnicode(Emojis.FORWARD));
		var wastebasket = Button.primary(paginatorId + ":REMOVE", Emoji.fromUnicode(Emojis.WASTEBASKET));
		ctx.replyWithComponents(embedBuilder, left, right, wastebasket);
	}

	public void createPurgePrompt(PurgeProcessor purgeProcessor, String content, CommandContext ctx) {
		var purgeProcessorId = purgeProcessor.getId();
		addAction(purgeProcessorId, purgeProcessor);

		var accept = Button.success(purgeProcessorId + ":ACCEPT", Emoji.fromUnicode(Emojis.CHECK));
		var wastebasket = Button.primary(purgeProcessorId + ":REMOVE", Emoji.fromUnicode(Emojis.WASTEBASKET));
		var deny = Button.danger(purgeProcessorId + ":DENY", Emoji.fromUnicode(Emojis.CROSS));
		ctx.replyWithComponents(content, accept, wastebasket, deny);
	}

	// dropdowns

	public void createYouTubeSearchDropdown(CommandContext ctx, MusicPlayer musicPlayer, SelectOption[] options) {
		var dropdownId = StringUtils.randomString(30);
		var dropdown = new YouTubeSearchDropdown(dropdownId, ctx, musicPlayer, this);
		addAction(dropdownId, dropdown);

		var i18n = ctx.getI18n();
		var choose = i18n.get("selection.text", i18n.get("selection.track"));
		var menu = SelectionMenu.create(dropdownId)
				.setPlaceholder(choose)
				.setRequiredRange(1, 1)
				.addOptions(options).build();
		ctx.replyWithComponents(choose, menu);
	}
}