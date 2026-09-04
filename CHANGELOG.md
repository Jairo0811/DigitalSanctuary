# Changelog

All notable changes to Digital Sanctuary are documented in this file.

## 1.1.0 - 2026-09-04

### Added

- Added Room integrity tests covering book/annotation/bookmark/knowledge-link cascades.
- Added an explicit migration test that opens a handcrafted schema v3 database through Room v4.
- Added ReaderEngine regression coverage with generated EPUB fixtures, malformed EPUB handling and oversized XHTML protection.

### Changed

- Bumped the Room schema from v3 to v4 with referential integrity between books, annotations, bookmarks and knowledge links.
- Added non-destructive `MIGRATION_3_4`, preserving valid relationships and discarding only orphan rows that cannot satisfy the new foreign keys.
- Added cascade deletion from books to annotations/bookmarks and from annotations to knowledge links.
- Hardened EPUB parsing with a 2 MiB per-entry limit and a 500 chapter ceiling.
- Hardened PDF metadata/rendering paths so invalid or unavailable documents fail safely instead of propagating reader exceptions.
- Extended Android CI push validation to `feature/**` branches.
- Bumped Android version metadata to `versionCode 110` / `versionName 1.1.0`.
- Updated the debug APK artifact name to `digital-sanctuary-v1.1.0-debug-apk`.

## 1.0.1 - 2026-09-04

### Changed

- Migrated all Kotlin source packages from `com.example.*` to `com.jairomatias.digitalsanctuary.*`.
- Added and versioned the Gradle Wrapper for reproducible local and CI builds.
- Updated Android CI to run tests and builds through `./gradlew`.
- Bumped Android version metadata to `versionCode 101` / `versionName 1.0.1`.
- Updated the debug APK artifact name to `digital-sanctuary-v1.0.1-debug-apk`.
- Replaced deprecated Compose icon/divider APIs found during CI compilation.

### Security and privacy

- Disabled Android automatic backup for local reading and knowledge data.
- Replaced template backup/data-extraction rules with explicit exclusions.
- Added bounded OkHttp connect, write, read and total call timeouts for AI requests.
- Kept Gemini credentials out of release builds and retained `AI_PROXY_URL` as the production path.

### Testing

- Made proxy/API configuration injectable in `GeminiAiAssistant` for deterministic tests.
- Added Robolectric + MockWebServer coverage for successful proxy responses and HTTP failure handling.
- Revalidated the package migration and Gradle Wrapper with `testDebugUnitTest` and `assembleDebug`.

## 1.0.0 - 2026-08-17

### Added

- Completed the original five-phase roadmap.
- Added complete library management, EPUB/PDF reader, persistent progress/bookmarks, Knowledge Hub and optional Gemini AI assistance.
- Added Room schema v3 with explicit migrations.
- Added Android CI, Robolectric, Compose UI testing and Roborazzi visual regression support.
