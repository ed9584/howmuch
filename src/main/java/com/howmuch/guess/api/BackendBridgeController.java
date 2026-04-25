package com.howmuch.guess.api;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <a href="https://github.com/ed9584/howmuch">ed9584/howmuch</a> {@code backend} 브랜치로 배포한
 * API({@code PriceGuessController}: {@code POST /api/guess}, {@code file}+{@code prompt})와 연결하는 프록시.
 */
@RestController
@RequestMapping("/api/backend")
@CrossOrigin
public class BackendBridgeController {

	private static final String REPO = "https://github.com/ed9584/howmuch";

	private final BackendBridgeService bridge;

	public BackendBridgeController(BackendBridgeService bridge) {
		this.bridge = bridge;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> info() {
		Map<String, Object> github = new LinkedHashMap<>();
		github.put("repo", REPO);
		github.put("branch", "backend");
		github.put("documented", Map.of("method", "POST", "path", "/api/guess", "parts", "file, prompt"));

		Map<String, String> proxy = new LinkedHashMap<>();
		proxy.put("path", "POST /api/backend/guess");
		proxy.put("parts", "file (MultipartFile), prompt (string, optional)");
		proxy.put("forwardsTo", "POST {HOWMUCH_BACKEND_BASE_URL}/api/guess");

		Map<String, Object> m = new LinkedHashMap<>();
		m.put("github", github);
		m.put("proxy", proxy);
		m.put("backendConfigured", bridge.isConfigured());
		m.put("backendBaseUrl", bridge.isConfigured() ? bridge.getEffectiveBaseUrl() : null);
		return m;
	}

	@PostMapping(value = "/guess", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> guess(
			@RequestPart("file") MultipartFile file,
			@RequestPart(value = "prompt", required = false) String prompt
	) {
		return bridge.forwardGuess(file, prompt);
	}
}
