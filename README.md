# howMuchAI (guess)

## Layout

```
src/main/java/com/howmuch/guess/
  GuessApplication.java
  config/        StartupUrlLogger
  page/          HomeController (Thymeleaf)
  api/           REST
src/main/resources/
  application.properties
  templates/index.html
  static/css  static/js  static/img
src/test/java/com/howmuch/guess/
```

## API (dev)

- `POST /api/guess` — `multipart`: `item` (optional, memo to AI), `images` (1..10)
- `POST /api/guess/video` — `multipart`: `item` (optional, memo to AI), `videos` (1..5)
- errors: `{ "code": "NO_IMAGES" | "TOO_MANY_IMAGES" | "NO_VIDEOS" | "TOO_MANY_VIDEOS" }`

## Multipart limits

See `application.properties` (`max-file-size` / `max-request-size`) for large videos.
