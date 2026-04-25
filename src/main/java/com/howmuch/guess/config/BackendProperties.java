package com.howmuch.guess.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "howmuch.backend")
public class BackendProperties {

	/**
	 * [ed9584/howmuch](https://github.com/ed9584/howmuch) {@code backend} 브랜치로 배포한 서비스의 공개 URL(슬래시 없이).
	 * 환경 변수: {@code HOWMUCH_BACKEND_BASE_URL}
	 */
	private String baseUrl = "";

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
