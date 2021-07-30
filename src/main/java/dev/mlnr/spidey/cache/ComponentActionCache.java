package dev.mlnr.spidey.cache;

import com.markozajc.akiwrapper.Akiwrapper;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.interactions.buttons.AkinatorGame;
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
		removeAction(componentAction, true);
	}

	public void removeAction(ComponentAction componentAction, boolean delete) {
		actionMap.remove(componentAction.getId());
		if (delete) {
			componentAction.getCtx().deleteReply();
		}
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

	public void createAkinator(CommandContext ctx, Akiwrapper akiwrapper) {
		var i18n = ctx.getI18n();
		var user = ctx.getUser();
		var title = i18n.get("commands.akinator.game_title", user.getAsTag());

		var embedBuilder = new EmbedBuilder().setTitle(title).setColor(Utils.SPIDEY_COLOR);
		var question = i18n.get("commands.akinator.question", 1, akiwrapper.getCurrentQuestion().getQuestion());
		embedBuilder.setDescription(question);

		var buttonsId = user.getId();

		var yes = Button.success(buttonsId + ":YES", i18n.get("commands.akinator.yes"));
		var no = Button.danger(buttonsId + ":NO", i18n.get("commands.akinator.no"));
		var dontKnow = Button.primary(buttonsId + ":DONT_KNOW", i18n.get("commands.akinator.dont_know"));
		var probably = Button.primary(buttonsId + ":PROBABLY", i18n.get("commands.akinator.probably"));
		var probablyNot = Button.primary(buttonsId + ":PROBABLY_NOT", i18n.get("commands.akinator.probably_not"));
		var dummy = Button.secondary(buttonsId + ":DUMMY", " ").asDisabled();
		var undo = Button.primary(buttonsId + ":UNDO", i18n.get("commands.akinator.undo"));
		var wastebasket = Button.primary(buttonsId + ":REMOVE", Emoji.fromUnicode(Emojis.WASTEBASKET));

		var originalLayout = Utils.splitComponents(yes, no, dontKnow, probably, probablyNot, undo, dummy, dummy, dummy, wastebasket);
		var guessLayout = Utils.splitComponents(dummy, yes, dummy, no, dummy, dummy, undo, dummy, wastebasket, dummy);

		addAction(buttonsId, new AkinatorGame(buttonsId, ctx, akiwrapper, embedBuilder, originalLayout, guessLayout, this));

		ctx.getEvent().getHook().sendMessageEmbeds(embedBuilder.build()).addActionRows(originalLayout).queue();
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