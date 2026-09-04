# Changelog

All notable changes to Digital Sanctuary are documented in this file.

## [1.0.1] - 2026-09-04

### Changed

- Migrated all Kotlin source packages from `com.example.*` to `com.jairomatias.digitalsanctuary.*`.
- Added and versioned the Gradle Wrapper for reproducible local and CI builds.
- Updated Android CI to run tests and builds through `./gradlew`.
- Bumped Android version metadata to `versionCode 101` / `versionName 1.0.1`.
- Updated the debug APK artifact name to `digital-sanctuary-v1.0.1-debug-apk`.

### Security and privacy

- Disabled Android automatic backup for local reading and knowledge data.
- Replaced template backup/data-extraction rules with explicit exclusions.
- Added bounded OkHttp connect, write, read and total call timeouts for AI requests.
- Kept Gemini credentials out of release builds and retained `AI_PROXY_URL` as the production path.

### Testing

- Made proxy/API configuration injectable in `GeminiAiAssistant` for deterministic tests.
- Added MockWebServer coverage for successful proxy responses and HTTP failure handling.
- Revalidated the package migration and Gradle Wrapper with `testDebugUnitTest` and `assembleDebug`.

## [1.0.0] - 2026-08-17

### Added

- Completed the original five-phase roadmap.
- Added complete library management, EPUB/PDF reader, persistent progress/bookmarks, Knowledge Hub and optional Gemini AI assistance.
- Added Room schema v3 with explicit migrations.
- Added Android CI, Robolectric, Compose UI testing and Roborazzi visual regression support.

[1.0.1]: https://github.com/Jairo0811/DigitalSanctuary/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Jairo0811/DigitalSanctuary/releases/tag/v1.0.0
