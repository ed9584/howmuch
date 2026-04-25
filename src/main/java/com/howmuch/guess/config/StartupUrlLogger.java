package com.howmuch.guess.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Prints local URL on startup (see application server.port / context-path). */
@Component
class StartupUrlLogger {

	private static final Logger log = LoggerFactory.getLogger(StartupUrlLogger.class);

	@Value("${server.port:8080}")
	private int port;

	@Value("${server.servlet.context-path:}")
	private String contextPath;

	@EventListener(ApplicationReadyEvent.class)
	public void logLocalUrl() {
		String p = (contextPath == null || contextPath.isEmpty()) ? "" : contextPath;
		log.info("Web: http://127.0.0.1:{}{}/", port, p);
	}
}
