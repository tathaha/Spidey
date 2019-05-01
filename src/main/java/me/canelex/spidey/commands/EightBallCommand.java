package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class EightBallCommand implements ICommand {

	private static final String[] a = {
			"As I see it, yes",
			"Better not tell you now",
			"Cannot predict now",
			"Don't count on it",
			"If you say so",
			"In your dreams",
			"It is certain",
			"Most likely",
			"My CPU is saying no",
			"My CPU is saying yes",
			"Out of psychic coverage range",
			"Signs point to yes",
			"Sure, sure",
			"Very doubtful",
			"When life gives you lemon, you drink it",
			"Without a doubt",
			"Wow, Much no, very yes, so maybe",
			"Yes, definitely",
			"Yes, unless you run out of memes",
			"You are doomed",
			"You can't handle the truth"};

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		API.sendMessage(e.getChannel(), ":crystal_ball: " + a[(new Random().nextInt() * a.length)], false);

	}

	@Override
	public final String help() {

		return "Returns a random answer to your question";

	}

}