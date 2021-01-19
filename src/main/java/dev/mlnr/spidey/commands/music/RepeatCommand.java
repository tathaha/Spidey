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
        super("repeat", new String[]{"loop"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(String[] args, CommandContext ctx)
    {
        var i18n = ctx.getI18n();
        if (!MusicUtils.canInteract(ctx.getMember()))
        {
            ctx.replyError(i18n.get("music.messages.failure.cant_interact", "set the repeat mode"));
            return;
        }
        var guild = ctx.getGuild();
        var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);
        if (musicPlayer == null)
        {
            ctx.replyError(i18n.get("music.messages.failure.no_music"));
            return;
        }
        var trackScheduler = musicPlayer.getTrackScheduler();
        if (args.length == 0)
        {
            if (trackScheduler.getRepeatMode() == null)
            {
                ctx.replyError(i18n.get("commands.repeat.other.provide"));
                return;
            }
            trackScheduler.setRepeatMode(null);
            ctx.reactLike();
            ctx.reply(i18n.get("commands.repeat.other.reset"));
            return;
        }
        try
        {
            var repeatMode = TrackScheduler.RepeatMode.valueOf(args[0].toUpperCase());
            trackScheduler.setRepeatMode(repeatMode);
            ctx.reactLike();
            ctx.reply(i18n.get("commands.repeat.other.set", args[0]));
        }
        catch (IllegalArgumentException ex)
        {
            ctx.replyError(i18n.get("commands.repeat.other.doesnt_exist"), Emojis.DISLIKE);
        }
    }
}