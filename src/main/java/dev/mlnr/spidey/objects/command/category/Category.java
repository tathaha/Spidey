package dev.mlnr.spidey.objects.command.category;

public enum Category implements ICategory {
    MODERATION("\uD83D\uDD28", "Moderation"),
    UTILITY("\uD83D\uDEE0", "Utility"),
    INFORMATIVE("\u2139", "Informative"),
    NSFW("\uD83D\uDD1E", "NSFW"),
    FUN("\uD83D\uDE03", "Fun"),
    MUSIC("\uD83C\uDFB6", "Music");

    private final String emoji;
    private final String friendlyName;

    Category(String emoji, String friendlyName) {
        this.emoji = emoji;
        this.friendlyName = friendlyName;
    }

    @Override
    public String getName() {
        return friendlyName;
    }

    @Override
    public String getFriendlyName() {
        return emoji + " " + friendlyName;
    }

    @Override
    public CategoryFlag getFlag() {
        return CategoryFlag.BASE;
    }

    public enum Settings implements ICategory {
        GENERAL("\u2699\uFE0F", "General"),
        MISC("\uD83D\uDCA1", "Miscellaneous"),
        MUSIC("\uD83C\uDFB6", "Music");

        private final String emoji;
        private final String friendlyName;

        Settings(String emoji, String friendlyName) {
            this.emoji = emoji;
            this.friendlyName = friendlyName;
        }

        @Override
        public String getName() {
            return friendlyName;
        }

        @Override
        public String getFriendlyName() {
            return emoji + " " + friendlyName;
        }

        @Override
        public CategoryFlag getFlag() {
            return CategoryFlag.SETTINGS;
        }
    }
}