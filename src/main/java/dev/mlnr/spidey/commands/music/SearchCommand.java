package dev.mlnr.spidey.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

import static dev.mlnr.spidey.utils.Utils.deleteMessage;

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
                final var originalTracks = playlist.getTracks();
                final var tracks = originalTracks.subList(0, Math.min(originalTracks.size(), 10));
                final var descriptionEmbedBuilder = MusicUtils.createMusicResponseBuilder();
                final var descriptionBuilder = descriptionEmbedBuilder.getDescriptionBuilder();

                descriptionEmbedBuilder.setAuthor("Searching for " + args[0]);
                for (var i = 0; i < tracks.size(); i++)
                {
                    final var trackInfo = tracks.get(i).getInfo();
                    descriptionBuilder.append(i + 1).append(". [`").append(trackInfo.title).append("`](").append(trackInfo.uri).append(") (**").append(MusicUtils.formatDuration(trackInfo.length)).append("**)\n");
                }
                descriptionBuilder.append("\n\nType a number to select a track or `cancel` to cancel the selection.");
                descriptionEmbedBuilder.setDescription(descriptionBuilder.toString());

                final var channel = ctx.getTextChannel();
                channel.sendMessage(descriptionEmbedBuilder.build()).queue(searchMessage ->
                {
                    final var message = ctx.getMessage();
                    Spidey.getWaiter().waitForEvent(GuildMessageReceivedEvent.class, event -> event.getChannel().equals(channel) && event.getAuthor().equals(ctx.getAuthor()), event ->
                    {
                        final var choiceMessage = event.getMessage();
                        final var content = choiceMessage.getContentRaw();
                        if (content.equalsIgnoreCase("cancel"))
                        {
                            deleteMessage(message);
                            deleteMessage(searchMessage);
                            deleteMessage(choiceMessage);
                            return;
                        }
                        var choice = 0;
                        try
                        {
                            choice = Integer.parseUnsignedInt(content);
                        }
                        catch (final NumberFormatException ex)
                        {
                            ctx.replyError("Entered value is either negative or not a number");
                            return;
                        }
                        final var size = tracks.size();
                        if (choice == 0 || choice > size)
                        {
                            ctx.replyError("Please enter a number from 1-" + size);
                            return;
                        }
                        choice--;
                        final var url = tracks.get(choice).getInfo().uri;
                        final var loader = new AudioLoader(musicPlayer, url, ctx, false);
                        MusicUtils.loadQuery(musicPlayer, url, loader);
                        deleteMessage(searchMessage);
                    }, 1, TimeUnit.MINUTES, () ->
                    {
                        ctx.replyError("Sorry, you took too long");
                        deleteMessage(message);
                        deleteMessage(searchMessage);
                    });
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