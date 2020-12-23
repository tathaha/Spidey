# Spidey, a multipurpose discord bot

Brief list of features:
- customizable prefix
- logging joins/leaves/boosts (with showing what invite link a user used to join)
- music (which is still in development, but the playback itself already works)

Music deserves its own section:
- support for skipping non-music segments through [SponsorBlock](https://sponsor.ajay.app) (disabled by default as it can cause unexpected confusion, can be enabled using `s!segskipping`)
- fair queue which prevents users from queueing the same song over and over again (enabled by default), which also has customizable threshold (min 2 songs, max 10, default is 3 songs)
- DJ role (allows to stop the entire playback, enable/disable segment skipping, enable/disable fair queue or set the fair queue threshold)

Other:
- (edit)snipe commands to snipe deleted/edited messages, which are cached only for 10 minutes from the moment of creation (enabled by default for guilds with less than 10k people)
- nsfw commands (locked to nsfw channels)
- uploading emotes from link
- other miscellaneous commands, which you can discover by running `s!help`

Click [here](https://spidey.mlnr.dev) to invite Spidey to your guild!

Powered by [JDA](https://github.com/DV8FromTheWorld/JDA)

**Support guild**

[![](https://discord.com/api/guilds/772435739664973825/embed.png?style=banner2)](https://discord.gg/uJCw7B9fxZ)
