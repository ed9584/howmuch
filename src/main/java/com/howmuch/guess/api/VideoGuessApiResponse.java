package com.howmuch.guess.api;

public record VideoGuessApiResponse(
		boolean ok,
		String item,
		int videoCount,
		long totalBytes
) {
}
