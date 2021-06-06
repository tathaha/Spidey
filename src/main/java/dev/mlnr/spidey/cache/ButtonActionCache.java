package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.buttons.Paginator;
import dev.mlnr.spidey.objects.buttons.ButtonAction;
import dev.mlnr.spidey.objects.buttons.PurgeProcessor;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.StringUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;
import net.jodah.expiringmap.ExpiringMap;

import java.util.function.BiConsumer;

public class ButtonActionCache {
	private final ExpiringMap<String, ButtonAction> buttonActionMap = ExpiringMap.builder()
			.variableExpiration()
			.asyncExpirationListener((buttonActionId, buttonActionObject) -> {
				var buttonAction = (ButtonAction) buttonActionObject;
				buttonAction.getType().getRemoveAction().accept(buttonAction);
			})
			.build();

	public void addButtonAction(String id, ButtonAction buttonAction) {
		var type = buttonAction.getType();
		buttonActionMap.put(id, buttonAction, type.getExpirationPolicy(), type.getExpirationDuration(), type.getExpirationUnit());
	}

	public ButtonAction getButtonAction(String id) {
		return buttonActionMap.get(id);
	}

	public void removeButtonAction(String id) {
		buttonActionMap.remove(id);
	}

	// paginator

	public void createPaginator(CommandContext ctx, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer) {
		var embedBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);

		embedBuilder.setFooter(ctx.getI18n().get("paginator.page", 1, totalPages));
		pagesConsumer.accept(0, embedBuilder);

		var paginatorId = StringUtils.randomString(30);
		var paginator = new Paginator(paginatorId, ctx, totalPages, pagesConsumer, this);
		var buttonAction = new ButtonAction(paginatorId, ctx, ButtonAction.ActionType.PAGINATION, paginator);
		addButtonAction(paginatorId, buttonAction);

		var left = Button.primary(paginatorId + ":BACKWARDS", Emoji.fromUnicode(Emojis.BACKWARDS));
		var right = Button.primary(paginatorId + ":FORWARD", Emoji.fromUnicode(Emojis.FORWARD));
		var wastebasket = Button.primary(paginatorId + ":REMOVE", Emoji.fromUnicode(Emojis.WASTEBASKET));
		ctx.replyWithButtons(embedBuilder, left, right, wastebasket);
	}

	public void createPurgePrompt(PurgeProcessor purgeProcessor, String content, CommandContext ctx) {
		var purgeProcessorId = purgeProcessor.getId();
		var buttonAction = new ButtonAction(purgeProcessorId, ctx, ButtonAction.ActionType.PURGE_PROMPT, purgeProcessor);
		addButtonAction(purgeProcessorId, buttonAction);

		var accept = Button.success(purgeProcessorId + ":ACCEPT", Emoji.fromUnicode(Emojis.CHECK));
		var wastebasket = Button.primary(purgeProcessorId + ":REMOVE", Emoji.fromUnicode(Emojis.WASTEBASKET));
		var deny = Button.danger(purgeProcessorId + ":DENY", Emoji.fromUnicode(Emojis.CROSS));
		ctx.replyWithButtons(content, accept, wastebasket, deny);
	}
}