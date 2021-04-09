package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UploadEmoteCommand extends Command {

	public UploadEmoteCommand() {
		super("uploademote", new String[]{"upemote"}, Category.UTILITY, Permission.MANAGE_EMOTES, 0, 4);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		var guild = ctx.getGuild();
		var i18n = ctx.getI18n();
		if (!guild.getSelfMember().hasPermission(getRequiredPermission())) {
			ctx.replyErrorLocalized("commands.uploademote.other.no_perms");
			return false;
		}
		if (args.length == 0) {
			ctx.replyErrorLocalized("commands.uploademote.other.provide_url");
			return false;
		}
		var name = "";
		if (args.length == 2) {
			name = args[1];
		}
		else {
			var tmpIndex = args[0].lastIndexOf('/') + 1;
			try {
				var index = args[0].lastIndexOf('.');
				var tmp = args[0].substring(tmpIndex, index); // possible name, if it doesn't throw, check for the extension
				var ext = args[0].substring(index + 1);
				if (Icon.IconType.fromExtension(ext) == Icon.IconType.UNKNOWN) {
					ctx.replyErrorLocalized("commands.uploademote.other.provide_format");
					return false;
				}
				name = tmp;
			}
			catch (IndexOutOfBoundsException ex) {
				name = args[0].substring(tmpIndex);
			}
		}
		if (!(name.length() >= 2 && name.length() <= 32)) {
			ctx.replyErrorLocalized("commands.uploademote.other.name_length");
			return false;
		}
		else if (!Utils.TEXT_PATTERN.matcher(name).matches()) {
			ctx.replyErrorLocalized("commands.uploademote.other.valid_format");
			return false;
		}
		var image = new ByteArrayOutputStream();
		try {
			var con = (HttpURLConnection) new URL(args[0]).openConnection(); // TODO execute the request using Requester class
			con.setRequestProperty("User-Agent", "dev.mlnr.spidey");
			try (var stream = con.getInputStream()) {
				var chunk = new byte[4096];
				var bytesRead = 0;
				while ((bytesRead = stream.read(chunk)) > 0) {
					image.write(chunk, 0, bytesRead);
				}
			}
			finally {
				con.disconnect();
			}
		}
		catch (MalformedURLException ex) {
			ctx.replyErrorLocalized("commands.uploademote.other.provide_url");
			return false;
		}
		catch (IOException ex) {
			ctx.replyErrorLocalized("internal_error", "upload the emote", ex.getMessage());
			return false;
		}
		var byteArray = image.toByteArray();
		if (byteArray.length > 256000) {
			ctx.replyErrorLocalized("commands.uploademote.other.size");
			return false;
		}
		var maxEmotes = guild.getMaxEmotes();
		var animated = byteArray[0] == 'G' && byteArray[1] == 'I' && byteArray[2] == 'F' && byteArray[3] == '8' && byteArray[4] == '9' && byteArray[5] == 'a';
		var used = guild.getEmoteCache().applyStream(stream -> stream.filter(emote -> !emote.isManaged())
				.collect(Collectors.partitioningBy(Emote::isAnimated))
				.get(animated).size());
		if (maxEmotes == used) {
			ctx.replyErrorLocalized("commands.uploademote.other.maximum_size");
			return false;
		}
		guild.createEmote(name, Icon.from(byteArray)).queue(emote -> {
			var left = maxEmotes - used - 1;
			ctx.replyLocalized("commands.uploademote.other.success.text", emote.getAsMention(),
					animated ? i18n.get("commands.uploademote.other.success.animated") : i18n.get("commands.uploademote.other.success.emote"),
					(left == 0 ? i18n.get("commands.uploademote.other.success.none") : left));
		}, failure -> ctx.replyErrorLocalized("internal_error", "upload the emote", failure.getMessage()));
		return true;
	}
}
