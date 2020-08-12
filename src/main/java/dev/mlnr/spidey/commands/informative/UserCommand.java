package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class UserCommand extends Command
{
	public UserCommand()
	{
		super("user", new String[]{}, "Shows info about you or entered user", "user (User#Discriminator, @user, user id or username/nickname)", Category.INFORMATIVE, Permission.UNKNOWN, 1, 0);
	}

	@Override
	public final void execute(final String[] args, final Message msg)
	{
		final var author = msg.getAuthor();
		final var channel =  msg.getTextChannel();
		final var user = args.length == 0 ? author : Utils.getUserFromArgument(args[0], channel, msg);
		if (user == null)
		{
			Utils.returnError("User not found", msg);
			return;
		}
		final var guild = msg.getGuild();
		final var member = guild.getMember(user);
		final var nick = member.getNickname();
		final var eb = Utils.createEmbedBuilder(author);
		
		eb.setAuthor("USER INFO - " + user.getAsTag());
		eb.setColor(0xFEFEFE);
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		eb.addField("ID", "" + user.getIdLong(), false);

		if (nick != null)
			eb.addField("Nickname for this guild", nick, false);

		eb.addField("Account created", Utils.getTime(user.getTimeCreated().toInstant().toEpochMilli()), true);
		eb.addField("User joined", Utils.getTime(member.getTimeJoined().toInstant().toEpochMilli()), false);

		if (guild.getBoosters().contains(member))
			eb.addField("Boosting since", Utils.getTime(member.getTimeBoosted().toInstant().toEpochMilli()), false);

		final var roles = member.getRoles();
		if (!roles.isEmpty())
		{
			final var sb = new StringBuilder();
			var rc = 0;
			for (final var role : roles)
				sb.append(role.getName()).append(++rc != roles.size() ? ", " : "");
			eb.addField("Roles [**" + rc + "**]", sb.length() > 1024 ? "Limit exceeded" : sb.toString(), false);
		}
		Utils.sendMessage(channel, eb.build());
	}
}