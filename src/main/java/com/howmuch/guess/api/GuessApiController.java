package com.howmuch.guess.api;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class GuessApiController {

	@PostMapping(value = "/api/guess", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> guess(
			@RequestParam(value = "item", required = false) String item,
			@RequestParam(value = "images", required = false) List<MultipartFile> images
	) {
		String itemTrim = item == null ? "" : item.strip();
		List<MultipartFile> list = images == null ? List.of() : images;
		long nonEmpty = list.stream().filter(f -> f != null && !f.isEmpty()).count();
		if (nonEmpty < 1) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new GuessApiErrorBody("NO_IMAGES"));
		}
		if (list.size() > 10) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new GuessApiErrorBody("TOO_MANY_IMAGES"));
		}

		long totalBytes = 0L;
		for (MultipartFile f : list) {
			if (f != null && !f.isEmpty()) {
				totalBytes += f.getSize();
			}
		}
		GuessApiResponse body = new GuessApiResponse(
				true,
				itemTrim.isEmpty() ? null : itemTrim,
				(int) nonEmpty,
				totalBytes
		);
		return ResponseEntity.ok(body);
	}

	@PostMapping(value = "/api/guess/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> guessVideo(
			@RequestParam(value = "item", required = false) String item,
			@RequestParam(value = "videos", required = false) List<MultipartFile> videos
	) {
		String itemTrim = item == null ? "" : item.strip();
		List<MultipartFile> list = videos == null ? List.of() : videos;
		long nonEmpty = list.stream().filter(f -> f != null && !f.isEmpty()).count();
		if (nonEmpty < 1) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new GuessApiErrorBody("NO_VIDEOS"));
		}
		if (list.size() > 5) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new GuessApiErrorBody("TOO_MANY_VIDEOS"));
		}

		long totalBytes = 0L;
		for (MultipartFile f : list) {
			if (f != null && !f.isEmpty()) {
				totalBytes += f.getSize();
			}
		}
		VideoGuessApiResponse body = new VideoGuessApiResponse(
				true,
				itemTrim.isEmpty() ? null : itemTrim,
				(int) nonEmpty,
				totalBytes
		);
		return ResponseEntity.ok(body);
	}
}
