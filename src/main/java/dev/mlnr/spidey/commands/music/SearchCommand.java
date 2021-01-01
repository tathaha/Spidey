package dev.mlnr.spidey.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SearchCommand extends Command
{
    public SearchCommand()
    {
        super("search", new String[]{"ytsearch"}, "Searches a query on YouTube", "search <query>", Category.MUSIC, Permission.UNKNOWN, 1, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var musicPlayer = MusicUtils.checkQueryInput(args, ctx);
        if (musicPlayer == null)
            return;
        MusicUtils.loadQuery(musicPlayer, "ytsearch:" + args[0], new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(final AudioTrack track) {}

            @Override
            public void playlistLoaded(final AudioPlaylist playlist)
            {
                final var selectionEmbedBuilder = MusicUtils.createMusicResponseBuilder();
                selectionEmbedBuilder.setAuthor("Searching for " + args[0]);

                final var tracks = playlist.getTracks();
                StringUtils.createSelection(selectionEmbedBuilder, tracks, ctx, "track", track ->
                {
                    final var trackInfo = track.getInfo();
                    return "[`" + trackInfo.title + "`](" + trackInfo.uri + ") (**" + MusicUtils.formatDuration(trackInfo.length) + "**)";
                }, choice ->
                {
                    final var url = tracks.get(choice).getInfo().uri;
                    final var loader = new AudioLoader(musicPlayer, url, ctx, false);
                    MusicUtils.loadQuery(musicPlayer, url, loader);
                });
            }

            @Override
            public void noMatches()
            {
                ctx.replyError("No matches found for **" + args[0] + "**", Emojis.DISLIKE);
            }

            @Override
            public void loadFailed(final FriendlyException exception)
            {
                ctx.replyError("There was an error while searching your query", Emojis.DISLIKE);
            }
        });
    }
}