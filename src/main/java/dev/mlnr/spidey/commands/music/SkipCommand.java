package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SkipCommand extends Command
{
    public SkipCommand()
    {
        super("skip", new String[]{}, "Skips a song", "skip", Category.MUSIC, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
        if (musicPlayer == null)
        {
            ctx.replyError("There is no music playing");
            return;
        }
        final var playingTrack = musicPlayer.getPlayingTrack();
        if (playingTrack == null)
        {
            ctx.replyError("There is no song playing");
            return;
        }
        if (MusicUtils.canInteract(ctx.getMember(), playingTrack))
        {
            musicPlayer.skip();
            ctx.reactLike();
            return;
        }
        if (!MusicUtils.isMemberConnected(ctx))
        {
            ctx.replyError("You need to be connected to the same channel as me in order to vote for skipping", Emojis.DISLIKE);
            return;
        }
        final var trackScheduler = musicPlayer.getTrackScheduler();
        final var author = ctx.getAuthor();
        final var mention = author.getAsMention();
        if (trackScheduler.hasSkipVoted(author))
        {
            trackScheduler.removeSkipVote(author);
            ctx.reactLike();
            ctx.reply("You've removed your vote to skip the current track. [" + mention + "]", null);
            return;
        }
        trackScheduler.addSkipVote(author);
        final var skipVotes = trackScheduler.getSkipVotes();
        final var requiredSkipVotes = trackScheduler.getRequiredSkipVotes();
        if (skipVotes < requiredSkipVotes)
        {
            ctx.reactLike();
            ctx.reply("You've voted to skip the current track. Votes: **" + skipVotes + "**/**" + requiredSkipVotes + "** [" + mention + "]", null);
            return;
        }
        musicPlayer.skip();
        ctx.reactLike();
    }
}