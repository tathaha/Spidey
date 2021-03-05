package dev.mlnr.spidey.objects.command.category;

public enum Category implements ICategory {
    MODERATION("\uD83D\uDD28 Moderation"),
    UTILITY("\uD83D\uDEE0 Utility"),
    INFORMATIVE("\u2139 Informative"),
    NSFW("\uD83D\uDD1E NSFW"),
    FUN("\uD83D\uDE03 Fun"),
    MUSIC("\uD83C\uDFB6 Music");

    private final String friendlyName;

    Category(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    public enum Settings implements ICategory {
        CHANNELS("\u270D\uFE0F Channels"),
        FILTERS("\uD83D\uDEB1 Chat Filters"),
        GENERAL("\u2699\uFE0F General"),
        MISC("\uD83D\uDCA1 Miscellaneous"),
        MUSIC("\uD83C\uDFB6 Music");

        private final String friendlyName;

        Settings(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String getFriendlyName() {
            return this.friendlyName;
        }
    }
}