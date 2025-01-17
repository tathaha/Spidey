package dev.mlnr.spidey.objects.interactions.components.buttons;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.commands.slash.ChoicesEnum;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.interactions.components.ComponentAction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AkinatorGame extends ComponentAction {
	private final Akiwrapper akiwrapper;
	private final EmbedBuilder embedBuilder;
	private final List<ActionRow> originalLayout;
	private final List<ActionRow> guessLayout;

	private final List<Long> declinedGuesses;
	private Guess currentGuess;
	private boolean prompted;

	public static void create(AkinatorGame.Context context) {
		new AkinatorGame(context);
	}

	private AkinatorGame(AkinatorGame.Context context) {
		super(context.getId(), context.getCtx(), ComponentAction.ActionType.AKINATOR, context.getComponentActionCache());
		this.akiwrapper = context.getAkiwrapper();
		this.embedBuilder = context.getEmbedBuilder();
		this.originalLayout = context.getOriginalLayout();
		this.guessLayout = context.getGuessLayout();

		this.declinedGuesses = new ArrayList<>();
	}

	public void answerCurrentQuestion(Answer answer) {
		if (answer == Answer.UNDO) {
			var previousQuestion = akiwrapper.undoAnswer();
			if (previousQuestion != null) {
				if (prompted) {
					resetGuess();
				}
				askQuestion(previousQuestion);
			}
			return;
		}
		if (answer == Answer.REMOVE) {
			uncacheAndDelete();
			return;
		}
		if (currentGuess != null) {
			if (answer == Answer.YES) {
				end(true);
				return;
			}
			declinedGuesses.add(currentGuess.getIdLong());
			var guess = getNewGuessWithProbability();
			var newGuess = guess == null ? getNewGuess() : guess;
			if (newGuess != null && newGuess.getProbability() < 0.65) {
				resetGuess();
				askQuestion(akiwrapper.getCurrentQuestion());
			}
			else if (newGuess == null) {
				end(false);
			}
			else {
				sendGuess(newGuess);
			}
			return;
		}
		var wrapperAnswer = answer.getWrapperRepresentative();
		var nextQuestion = akiwrapper.answerCurrentQuestion(wrapperAnswer);
		askQuestion(nextQuestion);
	}

	public void askQuestion(Question question) {
		var i18n = ctx.getI18n();
		var guess = getNewGuessWithProbability();
		if (guess != null) {
			sendGuess(guess);
			return;
		}
		if (question == null) {
			end(false);
			return;
		}
		embedBuilder.setDescription(i18n.get("commands.akinator.question", question.getStep() + 1, question.getQuestion()));
		ctx.editReply(embedBuilder);
	}

	public void sendGuess(Guess guess) {
		var imageUrl = guess.getImage();
		embedBuilder.setDescription(ctx.getI18n().get("commands.akinator.guess", guess.getProbability() * 100, guess.getName()));
		embedBuilder.setImage(imageUrl == null ? null : imageUrl.toString());
		ctx.editComponents(embedBuilder, guessLayout);
		currentGuess = guess;
		prompted = true;
	}

	public void resetGuess() {
		embedBuilder.setImage(null);
		currentGuess = null;
		prompted = false;
		ctx.editComponents(embedBuilder, originalLayout);
	}

	public void end(boolean win) {
		var i18n = ctx.getI18n();
		embedBuilder.setDescription(win ? i18n.get("commands.akinator.win") : i18n.get("commands.akinator.lose"));
		embedBuilder.setImage(null);
		ctx.editComponents(embedBuilder, Collections.emptyList());
		uncache();
	}

	public Guess getNewGuess() {
		return akiwrapper.getGuesses().stream().filter(g -> !declinedGuesses.contains(g.getIdLong())).findFirst().orElse(null);
	}

	public Guess getNewGuessWithProbability() {
		return akiwrapper.getGuesses().stream().filter(g -> g.getProbability() > 0.85 && !declinedGuesses.contains(g.getIdLong())).findFirst().orElse(null);
	}

	public enum Type implements ChoicesEnum {
		ANIMAL("Animal"),
		MOVIE_TV_SHOW("Movie/TV show"),
		PLACE("Place"),
		CHARACTER("Character"),
		OBJECT("Object");

		private final String friendlyName;

		Type(String friendlyName) {
			this.friendlyName = friendlyName;
		}

		@Override
		public String getFriendlyName() {
			return friendlyName;
		}
	}

	public enum Answer {
		YES,
		NO,
		DONT_KNOW,
		PROBABLY,
		PROBABLY_NOT,
		UNDO,
		REMOVE;

		public Akiwrapper.Answer getWrapperRepresentative() {
			return Akiwrapper.Answer.valueOf(name());
		}
	}

	public static class Context extends ComponentAction.Context {
		private final Akiwrapper akiwrapper;
		private final EmbedBuilder embedBuilder;
		private final List<ActionRow> originalLayout;
		private final List<ActionRow> guessLayout;

		public Context(String id, SlashCommandContext ctx, Akiwrapper akiwrapper, EmbedBuilder embedBuilder, List<ActionRow> originalLayout,
		               List<ActionRow> guessLayout, ComponentActionCache componentActionCache) {
			super(id, ctx, componentActionCache);
			this.akiwrapper = akiwrapper;
			this.embedBuilder = embedBuilder;
			this.originalLayout = originalLayout;
			this.guessLayout = guessLayout;
		}

		public Akiwrapper getAkiwrapper() {
			return akiwrapper;
		}

		public EmbedBuilder getEmbedBuilder() {
			return embedBuilder;
		}

		public List<ActionRow> getOriginalLayout() {
			return originalLayout;
		}

		public List<ActionRow> getGuessLayout() {
			return guessLayout;
		}
	}
}