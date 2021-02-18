package dev.mlnr.spidey.utils.requests.api;

public enum API {
	KSOFT_NSFW("https://api.ksoft.si/images/rand-reddit/%s?span=month", "Bearer " + System.getenv("ksoft"));

	private final String url;
	private final String key;

	API(String url, String key) {
		this.url = url;
		this.key = key;
	}

	public String getUrl() {
		return this.url;
	}

	public String getKey() {
		return this.key;
	}

	public enum Stats {
		TOP_GG("https://top.gg/api/bots/%s/stats", "server_count", System.getenv("topgg")),
		BOTLIST_SPACE("https://api.botlist.space/v1/bots/%s", "server_count", System.getenv("botlistspace")),
		DBOATS("https://discord.boats/api/bot/%s", "server_count", System.getenv("dboats")),
		DSERVICES("https://api.discordservices.net/bot/%s/stats", "servers", System.getenv("dservices")),
		DBOTS_GG("https://discord.bots.gg/api/v1/bots/%s/stats", "guildCount", System.getenv("dbotsgg")),
		DBL("https://discordbotlist.com/api/v1/bots/%s/stats", "guilds", System.getenv("dbl"));

		private final String url;
		private final String statsParam;
		private final String key;

		Stats(String url, String statsParam, String key) {
			this.url = url;
			this.statsParam = statsParam;
			this.key = key;
		}

		public String getUrl() {
			return this.url;
		}

		public String getStatsParam() {
			return this.statsParam;
		}

		public String getKey() {
			return this.key;
		}
	}
}