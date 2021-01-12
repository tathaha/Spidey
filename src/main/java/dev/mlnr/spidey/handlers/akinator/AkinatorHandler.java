package dev.mlnr.spidey.handlers.akinator;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.entities.Guess;
import dev.mlnr.spidey.cache.AkinatorCache;
import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.akinator.AkinatorData;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.stream.Collectors;

import static dev.mlnr.spidey.utils.Utils.returnError;
import static dev.mlnr.spidey.utils.Utils.sendMessage;

public class AkinatorHandler
{
    private AkinatorHandler() {}

    public static void handle(final long userId, final AkinatorContext ctx)
    {
        final var akinatorData = AkinatorCache.getAkinatorData(userId);
        final var akinator = akinatorData.getAkinator();
        final var message = ctx.getMessage();
        final var content = message.getContentStripped().toLowerCase();
        final var author = message.getAuthor();
        final var i18n = ctx.getI18n();
        final var embedBuilder = Utils.createEmbedBuilder(author)
                .setAuthor(i18n.get("commands.akinator.other.of", author.getAsTag())).setColor(Utils.SPIDEY_COLOR);
        final var cancel = i18n.get("commands.akinator.other.cancel.text");
        final var channel = message.getTextChannel();
        Akiwrapper.Answer answer = null;

        if (content.equals(i18n.get("commands.akinator.other.answers.yes.text")) || content.equals(i18n.get("commands.akinator.other.answers.yes.alias")))
            answer = Akiwrapper.Answer.YES;
        if (content.equals(i18n.get("commands.akinator.other.answers.no.text")) || content.equals(i18n.get("commands.akinator.other.answers.no.alias")))
            answer = Akiwrapper.Answer.NO;
        if (content.equals(i18n.get("commands.akinator.other.answers.dont_know.text")) || content.equals(i18n.get("commands.akinator.other.answers.dont_know.alias")))
            answer = Akiwrapper.Answer.DONT_KNOW;
        if (content.equals(i18n.get("commands.akinator.other.answers.probably.text")) || content.equals(i18n.get("commands.akinator.other.answers.probably.alias")))
            answer = Akiwrapper.Answer.PROBABLY;
        if (content.equals(i18n.get("commands.akinator.other.answers.probably_not.text")) || content.equals(i18n.get("commands.akinator.other.answers.probably_not.alias")))
            answer = Akiwrapper.Answer.PROBABLY_NOT;
        else if (content.equals(cancel))
        {
            AkinatorCache.removeAkinator(userId);
            sendMessage(channel, i18n.get("commands.akinator.other.cancel.success"));
            return;
        }
        if (answer == null)
        {
            returnError(i18n.get("commands.akinator.other.answer", cancel), message);
            return;
        }
        final var currentGuess = akinatorData.getCurrentGuess();
        if (currentGuess != null)
        {
            if (answer == Akiwrapper.Answer.NO)
            {
                akinatorData.addDeclined(currentGuess);
                newGuess(akinatorData, embedBuilder, ctx);
            }
            else if (answer == Akiwrapper.Answer.YES)
                win(embedBuilder, ctx);
            else
                returnError(i18n.get("commands.akinator.other.yesno"), message);
            return;
        }
        final var nextQuestion = akinator.answerCurrentQuestion(answer);
        final var guess = getGuess(akinatorData);
        if (guess != null)
        {
            sendGuess(guess, embedBuilder, ctx);
            akinatorData.setCurrentGuess(guess);
            return;
        }
        if (nextQuestion == null || akinator.getCurrentQuestion() == null)
        {
            embedBuilder.setDescription(i18n.get("commands.akinator.other.end.lose"));
            embedBuilder.appendDescription(" ").appendDescription(i18n.get("commands.akinator.other.end.again"));
            sendMessage(channel, embedBuilder.build());
            return;
        }
        embedBuilder.setDescription(i18n.get("commands.akinator.other.question", nextQuestion.getStep() + 1) + " " + nextQuestion.getQuestion());
        sendMessage(channel, embedBuilder.build());
    }

    private static void newGuess(final AkinatorData akinatorData, final EmbedBuilder embedBuilder, AkinatorContext ctx)
    {
        final var guesses = akinatorData.getAkinator().getGuesses().stream().filter(guess -> !akinatorData.isDeclined(guess)).collect(Collectors.toList());
        var newGuess = getGuess(akinatorData);
        if (newGuess != null)
        {
            akinatorData.setCurrentGuess(newGuess);
            sendGuess(newGuess, embedBuilder, ctx);
            return;
        }
        if (!guesses.isEmpty())
        {
            newGuess = guesses.get(0);
            sendGuess(newGuess, embedBuilder, ctx);
            akinatorData.setCurrentGuess(newGuess);
            return;
        }
        akinatorData.setCurrentGuess(null);
    }

    private static void sendGuess(final Guess guess, final EmbedBuilder embedBuilder, AkinatorContext ctx)
    {
        embedBuilder.setDescription(ctx.getI18n().get("commands.akinator.other.guess", guess.getProbability() * 100, guess.getName()));
        embedBuilder.setImage(guess.getImage().toString());
        sendMessage(ctx.getChannel(), embedBuilder.build());
    }

    private static void win(final EmbedBuilder embedBuilder, final AkinatorContext ctx)
    {
        final var i18n = ctx.getI18n();
        embedBuilder.setDescription(i18n.get("commands.akinator.other.end.win"));
        embedBuilder.appendDescription(" ").appendDescription(i18n.get("commands.akinator.other.end.again"));
        sendMessage(ctx.getChannel(), embedBuilder.build());
    }

    private static Guess getGuess(final AkinatorData akinatorData)
    {
        return akinatorData.getAkinator().getGuesses().stream().filter(guess -> guess.getProbability() > 0.85 && !akinatorData.isDeclined(guess))
                .findFirst().orElse(null);
    }
}