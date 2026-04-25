package com.howmuch.guess.api;

import com.howmuch.guess.config.BackendProperties;
import java.io.IOException;
import java.time.Duration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class BackendBridgeService {

	private static final Duration TIMEOUT = Duration.ofSeconds(120);

	private final BackendProperties props;
	private final WebClient webClient;

	public BackendBridgeService(BackendProperties props) {
		this.props = props;
		String u = safeBaseUrl();
		if (u.isEmpty()) {
			this.webClient = null;
		} else {
			this.webClient = WebClient.builder().baseUrl(u).build();
		}
	}

	public boolean isConfigured() {
		return webClient != null;
	}

	public String getEffectiveBaseUrl() {
		return safeBaseUrl();
	}

	private String safeBaseUrl() {
		if (props.getBaseUrl() == null) {
			return "";
		}
		return props.getBaseUrl().strip().replaceAll("/+$", "");
	}

	public ResponseEntity<String> forwardGuess(MultipartFile file, String prompt) {
		if (webClient == null) {
			return ResponseEntity.status(503)
					.contentType(MediaType.APPLICATION_JSON)
					.body("{\"error\":\"BACKEND_NOT_CONFIGURED\",\"message\":\"Set HOWMUCH_BACKEND_BASE_URL (backend branch deploy URL)\"}");
		}
		if (file == null || file.isEmpty()) {
			return ResponseEntity.status(400)
					.contentType(MediaType.APPLICATION_JSON)
					.body("{\"error\":\"NO_FILE\"}");
		}
		String p = prompt == null ? "" : prompt;
		ByteArrayResource part;
		try {
			part = new ByteArrayResource(file.getBytes()) {
				@Override
				public String getFilename() {
					String n = file.getOriginalFilename();
					return n != null && !n.isEmpty() ? n : "upload";
				}
			};
		} catch (IOException e) {
			return ResponseEntity.status(500)
					.contentType(MediaType.APPLICATION_JSON)
					.body("{\"error\":\"READ_FAILED\"}");
		}
		MediaType partType = toPartType(file);
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", part, partType);
		builder.part("prompt", p);
		try {
			String body = webClient
					.post()
					.uri("/api/guess")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(BodyInserters.fromMultipartData(builder.build()))
					.retrieve()
					.bodyToMono(String.class)
					.block(TIMEOUT);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body != null ? body : "{}");
		} catch (WebClientResponseException e) {
			String b = e.getResponseBodyAsString();
			return ResponseEntity.status(e.getStatusCode())
					.contentType(MediaType.APPLICATION_JSON)
					.body(b != null && !b.isEmpty() ? b : "{\"error\":\"BACKEND_ERROR\"}");
		} catch (WebClientRequestException e) {
			return ResponseEntity.status(502)
					.contentType(MediaType.APPLICATION_JSON)
					.body("{\"error\":\"BACKEND_UNREACHABLE\"}");
		}
	}

	private static MediaType toPartType(MultipartFile file) {
		String ct = file.getContentType();
		if (ct != null && !ct.isEmpty()) {
			try {
				return MediaType.parseMediaType(ct);
			} catch (Exception ignored) {
				// fall through
			}
		}
		return MediaType.APPLICATION_OCTET_STREAM;
	}
}
