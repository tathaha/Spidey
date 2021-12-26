package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UploadEmoteCommand extends Command {
	public UploadEmoteCommand() {
		super("uploademote", "Uploads the image from the provided url as an emote", Category.UTILITY, Permission.MANAGE_EMOTES_AND_STICKERS, 4,
				Utils.createConvenientOption(OptionType.STRING, "link", "The link of the emote", true),
				new OptionData(OptionType.STRING, "name", "The emote name"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var guild = ctx.getGuild();
		var i18n = ctx.getI18n();
		if (!guild.getSelfMember().hasPermission(getRequiredPermission())) {
			ctx.replyErrorLocalized("commands.uploademote.no_perms");
			return false;
		}
		var name = "";
		var link = ctx.getStringOption("link");
		var nameOption = ctx.getStringOption("name");
		if (nameOption != null) {
			name = nameOption;
		}
		else {
			var tmpIndex = link.lastIndexOf('/') + 1;
			try {
				var index = link.lastIndexOf('.');
				var tmp = link.substring(tmpIndex, index); // possible name, if it doesn't throw, check for the extension
				var ext = link.substring(index + 1);
				if (Icon.IconType.fromExtension(ext) == Icon.IconType.UNKNOWN) {
					ctx.replyErrorLocalized("commands.uploademote.provide_format");
					return false;
				}
				name = tmp;
			}
			catch (IndexOutOfBoundsException ex) {
				name = link.substring(tmpIndex);
			}
		}
		if (!(name.length() >= 2 && name.length() <= 32)) {
			ctx.replyErrorLocalized("commands.uploademote.name_length");
			return false;
		}
		else if (!Utils.TEXT_PATTERN.matcher(name).matches()) {
			ctx.replyErrorLocalized("commands.uploademote.valid_format");
			return false;
		}
		var image = new ByteArrayOutputStream();
		try {
			var con = (HttpURLConnection) new URL(link).openConnection(); // TODO execute the request using Requester class
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
			ctx.replyErrorLocalized("commands.uploademote.provide_url");
			return false;
		}
		catch (IOException ex) {
			ctx.replyErrorLocalized("internal_error", "upload the emote", ex.getMessage());
			return false;
		}
		var byteArray = image.toByteArray();
		if (byteArray.length > 256000) {
			ctx.replyErrorLocalized("commands.uploademote.size");
			return false;
		}
		var maxEmotes = guild.getMaxEmotes();
		var animated = byteArray[0] == 'G' && byteArray[1] == 'I' && byteArray[2] == 'F' && byteArray[3] == '8' && byteArray[4] == '9' && byteArray[5] == 'a';
		var used = guild.getEmoteCache().applyStream(stream -> stream.filter(emote -> !emote.isManaged())
				.collect(Collectors.partitioningBy(Emote::isAnimated))
				.get(animated).size());
		if (maxEmotes == used) {
			ctx.replyErrorLocalized("commands.uploademote.maximum_size");
			return false;
		}
		guild.createEmote(name, Icon.from(byteArray)).queue(emote -> {
			var left = maxEmotes - used - 1;
			ctx.replyLocalized("commands.uploademote.success.text", emote.getAsMention(),
					animated ? i18n.get("commands.uploademote.success.animated") : i18n.get("commands.uploademote.success.emote"),
					(left == 0 ? i18n.get("commands.uploademote.success.none") : left));
		}, failure -> ctx.replyErrorLocalized("internal_error", "upload the emote", failure.getMessage()));
		return true;
	}
}
