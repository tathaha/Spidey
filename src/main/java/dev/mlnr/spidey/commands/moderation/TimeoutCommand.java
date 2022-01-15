package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.timeout.DurationUnit;
import dev.mlnr.spidey.utils.CommandUtils;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unused")
public class TimeoutCommand extends Command {
	public TimeoutCommand() {
		super("timeout", "Times a member out", Category.MODERATION, Permission.MODERATE_MEMBERS, 2,
				new OptionData(OptionType.USER, "member", "The member to time out", true),
				new OptionData(OptionType.INTEGER, "length", "The amount of provided unit to time the member out for", true),
				new OptionData(OptionType.STRING, "unit", "The time unit to time the member out for", true)
						.addChoices(CommandUtils.getChoicesFromEnum(DurationUnit.class)),
				new OptionData(OptionType.STRING, "reason", "The reason for the time out"));
	}
	@Override
	public boolean execute(CommandContext ctx) {
		var requiredPermission = getRequiredPermission();
		if (!ctx.hasSelfPermission(requiredPermission)) {
			ctx.replyErrorNoPerm(requiredPermission, "time members out");
			return false;
		}
		var member = ctx.getMemberOption("member");
		if (member == null) {
			ctx.replyErrorLocalized("commands.timeout.user_not_member");
			return false;
		}
		if (!ctx.getGuild().getSelfMember().canInteract(member)) {
			ctx.replyErrorLocalized("commands.timeout.cant_interact");
			return false;
		}
		var length = ctx.getLongOption("length");
		var unit = ctx.getStringOption("unit");
		var now = OffsetDateTime.now();
		var expiry = now.plus(length, ChronoUnit.valueOf(unit)).minusSeconds(1); // -1 second accounts for the method call delay
		if (expiry.isAfter(now.plusDays(28))) {
			ctx.replyErrorLocalized("commands.timeout.above_max");
			return false;
		}
		var reason = ctx.getStringOption("reason");
		member.timeoutUntil(expiry).reason(reason).queue();
		ctx.replyLocalized("commands.timeout.success", member, StringUtils.formatDate(expiry));
		return true;
	}
}