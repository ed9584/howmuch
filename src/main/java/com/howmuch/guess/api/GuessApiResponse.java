package com.howmuch.guess.api;

public record GuessApiResponse(
		boolean ok,
		String item,
		int imageCount,
		long totalBytes
) {
}
