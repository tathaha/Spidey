package dev.mlnr.spidey.utils.requests.api;

public enum API {
	KSOFT("https://api.ksoft.si/images/rand-reddit/%s?span=month&remove_nsfw=%s", "Bearer " + System.getenv("ksoft"));

	private final String url;
	private final String token;

	API(String url, String token) {
		this.url = url;
		this.token = token;
	}

	public String getUrl() {
		return this.url;
	}

	public String getToken() {
		return this.token;
	}
}