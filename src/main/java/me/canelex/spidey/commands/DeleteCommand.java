package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.PermissionError;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DeleteCommand implements ICommand {

	private int count;

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final int maxArgs = 3;
		final Message msg = e.getMessage();
		final TextChannel ch = e.getChannel();

		msg.delete().complete();
		if (e.getMember() != null && !Utils.hasPerm(e.getMember(), Permission.BAN_MEMBERS))  {

			Utils.sendMessage(ch, PermissionError.getErrorMessage("BAN_MEMBERS"), false);
			return;

		}
		final String[] args = msg.getContentRaw().trim().split("\\s+", maxArgs);

		if (args.length < 2) {

			Utils.returnError("Wrong syntax", msg);
			return;

		}
		if (msg.getMentionedUsers().isEmpty()) {

			int amount;
			try {
				amount = Integer.parseUnsignedInt(args[1]);
			} catch (final NumberFormatException ignored) {
				Utils.returnError("Entered value is either negative or not a number", msg);
				return;
			}
			if (amount == 0) {
				Utils.returnError("Please enter a number from 1-100", msg);
				return;
			}
			if (amount == 100) {
				amount = 99;
			}

			ch.getIterableHistory().cache(false).takeAsync(amount).thenAccept(msgs -> {
				count = msgs.size();
				CompletableFuture future;
				if (count == 1) {
					future = msgs.get(0).delete().submit();
				} else {
					final List<CompletableFuture<Void>> list = ch.purgeMessages(msgs);
					future = CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
				}
				future.thenRunAsync(() -> e.getChannel().sendMessage(Utils.generateSuccess(count, null)).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS)));
			});

		}

		else {

			int amount;
			final User user = msg.getMentionedUsers().get(0);
			try {
				amount = Integer.parseUnsignedInt(args[2]);
			}
			catch (final NumberFormatException ignored) {
				Utils.returnError("Entered value is either negative or not a number", msg);
				return;
			}
			if (amount == 0) {
				Utils.returnError("Please enter a number from 1-100", msg);
				return;
			}
			if (amount == 100) {
				amount = 99;
			}
			int a = amount;
			ch.getIterableHistory().cache(false).takeAsync(100).thenAccept(msgs -> {
				final List<Message> newList = msgs.stream().filter(m -> m.getAuthor().equals(user)).limit(a).collect(Collectors.toList());
				CompletableFuture future;
				if (newList.size() == 1) {
					future = newList.get(0).delete().submit();
				} else {
					final List<CompletableFuture<Void>> requests = ch.purgeMessages(newList);
					future = CompletableFuture.allOf(requests.toArray(new CompletableFuture[0]));
				}
				future.thenRunAsync(() -> e.getChannel().sendMessage(Utils.generateSuccess(newList.size(), user)).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS)));
			});
		}
	}

	@Override
	public final String getDescription() { return "Deletes messages (by mentioned user)"; }
	@Override
	public final boolean isAdmin() { return true; }
	@Override
	public final String getInvoke() { return "d"; }
	@Override
	public final Category getCategory() { return Category.MODERATION; }
	@Override
	public final String getUsage() { return "s!d <count/@someone> <count>"; }

}