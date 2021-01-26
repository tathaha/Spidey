package dev.mlnr.spidey.cache;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResponseCache {
	private final Map<Long, Long> responseMap = ExpiringMap.builder()
			.expirationPolicy(ExpirationPolicy.CREATED)
			.expiration(15, TimeUnit.MINUTES)
			.build();
	
	private static ResponseCache responseCache;

	public static synchronized ResponseCache getInstance() {
		if (responseCache == null)
			responseCache = new ResponseCache();
		return responseCache;
	}

	public Long getResponseMessageId(long invokeMessageId) {
		return responseMap.get(invokeMessageId);
	}

	public void setResponseMessageId(long invokeMessageId, long responseMessageId) {
		responseMap.put(invokeMessageId, responseMessageId);
	}

	public void removeResponseMessageId(long invokeMessageId) {
		responseMap.remove(invokeMessageId);
	}
}