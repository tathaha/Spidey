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
}