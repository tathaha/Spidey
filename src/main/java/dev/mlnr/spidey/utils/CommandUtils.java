package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.objects.command.ChoicesEnum;
import dev.mlnr.spidey.objects.command.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils {
	private static final long DEV_ID = 394607709741252621L;

	private CommandUtils() {}

	public static <E extends Enum<E> & ChoicesEnum> List<Choice> getChoicesFromEnum(Class<E> choiceEnum) {
		return Arrays.stream(choiceEnum.getEnumConstants())
				.map(choicesEnum -> new Choice(choicesEnum.getFriendlyName(), choicesEnum.name())).collect(Collectors.toList());
	}

	public static boolean hasPermission(Command command, Member member) {
		return member.hasPermission(command.getRequiredPermission());  // filter commands the member can use
	}

	public static boolean checkForDevCommand(Command command, User user) {
		return !command.isDevOnly() || user.getIdLong() == DEV_ID; // filter dev only commands
	}

	public static boolean canRunCommand(Command command, Member member) {
		return hasPermission(command, member) && checkForDevCommand(command, member.getUser());
	}
}