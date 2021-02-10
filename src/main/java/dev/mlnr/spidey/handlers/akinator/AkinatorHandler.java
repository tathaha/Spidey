package dev.mlnr.spidey.handlers.akinator;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;
import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.akinator.AkinatorData;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.stream.Collectors;

import static dev.mlnr.spidey.utils.Utils.returnError;
import static dev.mlnr.spidey.utils.Utils.sendMessage;

public class AkinatorHandler {

	private AkinatorHandler() {}

	public static void handle(User user, AkinatorContext ctx) {
		var userId = user.getIdLong();
		var akinatorCache = ctx.getAkinatorCache();
		var akinatorData = akinatorCache.getAkinatorData(userId);
		var akinator = akinatorData.getAkinator();
		var message = ctx.getMessage();
		var content = message.getContentStripped().toLowerCase();
		var author = message.getAuthor();
		var i18n = ctx.getI18n();
		var embedBuilder = Utils.createEmbedBuilder(author)
				.setAuthor(i18n.get("commands.akinator.other.of", author.getAsTag())).setColor(Utils.SPIDEY_COLOR);
		var channel = message.getTextChannel();
		Akiwrapper.Answer answer = null;

		if (content.equals(i18n.get("commands.akinator.other.answers.yes.text")) || content.equals(i18n.get("commands.akinator.other.answers.yes.alias"))) {
			answer = Akiwrapper.Answer.YES;
		}
		else if (content.equals(i18n.get("commands.akinator.other.answers.no.text")) || content.equals(i18n.get("commands.akinator.other.answers.no.alias"))) {
			answer = Akiwrapper.Answer.NO;
		}
		else if (content.equals(i18n.get("commands.akinator.other.answers.dont_know.text")) || content.equals(i18n.get("commands.akinator.other.answers.dont_know.alias"))) {
			answer = Akiwrapper.Answer.DONT_KNOW;
		}
		else if (content.equals(i18n.get("commands.akinator.other.answers.probably.text")) || content.equals(i18n.get("commands.akinator.other.answers.probably.alias"))) {
			answer = Akiwrapper.Answer.PROBABLY;
		}
		else if (content.equals(i18n.get("commands.akinator.other.answers.probably_not.text")) || content.equals(i18n.get("commands.akinator.other.answers.probably_not.alias"))) {
			answer = Akiwrapper.Answer.PROBABLY_NOT;
		}
		else if (content.equals(i18n.get("commands.akinator.other.answers.undo.text"))) {
			var previousQuestion = akinator.undoAnswer();
			if (previousQuestion == null) {
				returnError(i18n.get("commands.akinator.other.answers.undo.error"), message);
				return;
			}
			sendQuestion(previousQuestion, embedBuilder, ctx);
			return;
		}
		else if (content.equals(i18n.get("commands.akinator.other.cancel.text"))) {
			akinatorCache.removeAkinator(userId);
			sendMessage(channel, i18n.get("commands.akinator.other.cancel.success"));
			return;
		}
		if (answer == null) {
			return;
		}
		var yesno = i18n.get("commands.akinator.other.yesno");
		if (akinatorData.isPrompted()) {
			if (answer == Akiwrapper.Answer.NO) {
				akinatorCache.removeAkinator(userId);
			}
			else if (answer == Akiwrapper.Answer.YES) {
				akinatorCache.createAkinator(user, ctx);
			}
			else {
				returnError(yesno, message);
			}
			return;
		}
		var currentGuess = akinatorData.getCurrentGuess();
		if (currentGuess != null) {
			if (answer == Akiwrapper.Answer.NO) {
				akinatorData.addDeclined(currentGuess);
				newGuess(akinatorData, embedBuilder, ctx);
			}
			else if (answer == Akiwrapper.Answer.YES) {
				win(embedBuilder, akinatorData, ctx);
			}
			else {
				returnError(yesno, message);
			}
			return;
		}
		var nextQuestion = akinator.answerCurrentQuestion(answer);
		var guess = getGuess(akinatorData);
		if (guess != null) {
			sendGuess(guess, embedBuilder, ctx);
			akinatorData.setCurrentGuess(guess);
			return;
		}
		if (nextQuestion == null || akinator.getCurrentQuestion() == null) {
			embedBuilder.setDescription(i18n.get("commands.akinator.other.end.lose"));
			embedBuilder.appendDescription(" ").appendDescription(i18n.get("commands.akinator.other.end.again"));
			akinatorData.prompt();
			sendMessage(channel, embedBuilder.build());
			return;
		}
		sendQuestion(nextQuestion, embedBuilder, ctx);
	}

	private static void newGuess(AkinatorData akinatorData, EmbedBuilder embedBuilder, AkinatorContext ctx) {
		var guesses = akinatorData.getAkinator().getGuesses().stream().filter(guess -> !akinatorData.isDeclined(guess)).collect(Collectors.toList());
		var newGuess = getGuess(akinatorData);
		if (newGuess == null) {
			if (guesses.isEmpty()) {
				akinatorData.setCurrentGuess(null);
				return;
			}
			newGuess = guesses.get(0);
		}
		akinatorData.setCurrentGuess(newGuess);
		sendGuess(newGuess, embedBuilder, ctx);
	}

	private static void sendGuess(Guess guess, EmbedBuilder embedBuilder, AkinatorContext ctx) {
		embedBuilder.setDescription(ctx.getI18n().get("commands.akinator.other.guess", guess.getProbability() * 100, guess.getName()));
		var image = guess.getImage();
		if (image != null)
			embedBuilder.setImage(image.toString());
		sendMessage(ctx.getChannel(), embedBuilder.build());
	}

	private static void sendQuestion(Question question, EmbedBuilder embedBuilder, AkinatorContext ctx) {
		embedBuilder.setDescription(ctx.getI18n().get("commands.akinator.other.question", question.getStep() + 1) + " " + question.getQuestion());
		sendMessage(ctx.getChannel(), embedBuilder.build());
	}

	private static void win(EmbedBuilder embedBuilder, AkinatorData akinatorData, AkinatorContext ctx) {
		var i18n = ctx.getI18n();
		embedBuilder.setDescription(i18n.get("commands.akinator.other.end.win"));
		embedBuilder.appendDescription(" ").appendDescription(i18n.get("commands.akinator.other.end.again"));
		akinatorData.prompt();
		sendMessage(ctx.getChannel(), embedBuilder.build());
	}

	private static Guess getGuess(AkinatorData akinatorData) {
		return akinatorData.getAkinator().getGuesses().stream().filter(guess -> guess.getProbability() > 0.85 && !akinatorData.isDeclined(guess)).findFirst().orElse(null);
	}
}