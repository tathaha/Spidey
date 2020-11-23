package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.music.TrackScheduler;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class RepeatCommand extends Command
{
    public RepeatCommand()
    {
        super("repeat", new String[]{"loop"}, "Sets/resets the repeat mode", "repeat (song/queue or blank to reset)", Category.MUSIC, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);
        if (musicPlayer == null)
        {
            ctx.replyError("There is no music playing");
            return;
        }
        if (!MusicUtils.canInteract(ctx.getMember()))
        {
            ctx.replyError("You have to be a DJ/Server Manager to set the repeat mode");
            return;
        }
        final var trackScheduler = musicPlayer.getTrackScheduler();
        if (args.length == 0)
        {
            if (trackScheduler.getRepeatMode() != null)
            {
                ctx.replyError("Please provide the repeat mode; **song**/**queue**");
                return;
            }
            trackScheduler.setRepeatMode(null);
            ctx.reactLike();
            ctx.reply("The repeat mode has been **reset**.");
            return;
        }
        try
        {
            final var repeatMode = TrackScheduler.RepeatMode.valueOf(args[0].toUpperCase());
            trackScheduler.setRepeatMode(repeatMode);
            ctx.reactLike();
            ctx.reply("The repeat mode has been set to **" + args[0] + "**.");
        }
        catch (final IllegalArgumentException ex)
        {
            ctx.replyError("There is no such repeat mode", Emojis.DISLIKE);
        }
    }
}