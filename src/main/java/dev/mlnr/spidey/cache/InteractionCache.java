package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.Interaction;
import dev.mlnr.spidey.objects.interactions.buttons.Paginator;
import dev.mlnr.spidey.objects.interactions.buttons.PurgeProcessor;
import dev.mlnr.spidey.objects.interactions.dropdowns.Dropdown;
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

public class InteractionCache {
	private final ExpiringMap<String, Interaction> interactionMap = ExpiringMap.builder()
			.variableExpiration()
			.asyncExpirationListener((interactionId, interactionObject) -> ((Interaction) interactionObject).getCtx().deleteReply())
			.build();

	public void addInteraction(String id, Interaction interaction) {
		var type = interaction.getType();
		interactionMap.put(id, interaction, type.getExpirationPolicy(), type.getExpirationDuration(), type.getExpirationUnit());
	}

	public Interaction getInteraction(String id) {
		return interactionMap.get(id);
	}

	public void removeInteraction(Interaction interaction) {
		interactionMap.remove(interaction.getId());
		interaction.getCtx().deleteReply();
	}

	// buttons

	public void createPaginator(CommandContext ctx, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer) {
		var embedBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);

		embedBuilder.setFooter(ctx.getI18n().get("paginator.page", 1, totalPages));
		pagesConsumer.accept(0, embedBuilder);

		var paginatorId = StringUtils.randomString(30);
		var paginator = new Paginator(paginatorId, ctx, totalPages, pagesConsumer, this);
		addInteraction(paginatorId, paginator);

		var left = Button.primary(paginatorId + ":BACKWARDS", Emoji.fromUnicode(Emojis.BACKWARDS));
		var right = Button.primary(paginatorId + ":FORWARD", Emoji.fromUnicode(Emojis.FORWARD));
		var wastebasket = Button.primary(paginatorId + ":REMOVE", Emoji.fromUnicode(Emojis.WASTEBASKET));
		ctx.replyWithComponents(embedBuilder, left, right, wastebasket);
	}

	public void createPurgePrompt(PurgeProcessor purgeProcessor, String content, CommandContext ctx) {
		var purgeProcessorId = purgeProcessor.getId();
		addInteraction(purgeProcessorId, purgeProcessor);

		var accept = Button.success(purgeProcessorId + ":ACCEPT", Emoji.fromUnicode(Emojis.CHECK));
		var wastebasket = Button.primary(purgeProcessorId + ":REMOVE", Emoji.fromUnicode(Emojis.WASTEBASKET));
		var deny = Button.danger(purgeProcessorId + ":DENY", Emoji.fromUnicode(Emojis.CROSS));
		ctx.replyWithComponents(content, accept, wastebasket, deny);
	}

	// dropdowns

	public void createDropdown(CommandContext ctx, MusicPlayer musicPlayer, SelectOption[] options) {
		var dropdownId = StringUtils.randomString(30);
		var dropdown = new Dropdown(dropdownId, ctx, musicPlayer, this);
		addInteraction(dropdownId, dropdown);

		var choose = ctx.getI18n().get("selection.choose");
		var menu = SelectionMenu.create(dropdownId)
				.setPlaceholder(choose)
				.setRequiredRange(1, 1)
				.addOptions(options).build();
		ctx.replyWithComponents(choose, menu);
	}
}