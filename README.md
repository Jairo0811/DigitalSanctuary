<p align="center">
  <img src="branding/cover.png" alt="Digital Sanctuary — Read, Think, Preserve" width="720" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/UNAPEC-ISO--710-003B70?style=for-the-badge" alt="UNAPEC ISO-710">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.1.0-111827?style=for-the-badge" alt="Version 1.1.0">
  <img src="https://img.shields.io/badge/Estado-Roadmap%20completado-16A34A?style=for-the-badge" alt="Roadmap completado">
  <img src="https://img.shields.io/badge/Plataforma-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
</p>

<p align="center">
  <a href="https://github.com/Jairo0811/DigitalSanctuary/actions/workflows/android-ci.yml">
    <img src="https://github.com/Jairo0811/DigitalSanctuary/actions/workflows/android-ci.yml/badge.svg" alt="Android CI">
  </a>
</p>

<p align="center"><strong>Un santuario digital para leer, pensar y preservar conocimiento.</strong></p>

**Digital Sanctuary** es una aplicación Android nativa que combina biblioteca personal, lector EPUB/PDF, gestión de conocimiento y asistencia opcional con Google Gemini. El producto evolucionó desde un prototipo académico creado con Google AI Studio hasta una aplicación Android estructurada con Kotlin, Jetpack Compose, Room y prácticas de ingeniería de software.

---

## 🎓 Información académica

| Información | Detalle |
|---|---|
| 👨‍🎓 Estudiante | **Francis Jairo Matías Rosario** |
| 🆔 Matrícula | **A00115261** |
| 📖 Asignatura | **Desarrollo de Software con Tecnología Propietaria 2 (ISO-710)** |
| 👨‍🏫 Profesor | **Ing. Pedro José Ramirez Rodriguez** |
| 🏫 Institución | **Universidad APEC (UNAPEC)** |
| 📅 Período académico | **Mayo - Agosto 2026** |
| 🤖 Prototipado original | **Google AI Studio** |
| 📱 Evolución posterior | **Aplicación Android nativa** |

> La entrega universitaria original fue un prototipo académico. La implementación Android funcional, el lector real, el Knowledge Hub, las pruebas, CI y la integración de IA corresponden a la evolución posterior del concepto.

### 🧭 Continuidad académica

Digital Sanctuary fue desarrollado como proyecto académico individual por **Francis Jairo Matías Rosario (A00115261)**. La continuidad verificable se establece mediante el profesor **Ing. Pedro José Ramirez Rodriguez**, con quien existe una secuencia de tres proyectos académicos:

| Orden | Asignatura | Proyecto | Período |
|---:|---|---|---|
| 1 | Bases de Datos 1 (INF-164) | [**NutriFlow**](https://github.com/Jairo0811/NutriFlow) | Mayo - Agosto 2024 |
| 2 | Fundamentos de Seguridad de Software (ISO-915) | [**CertiChain**](https://github.com/Jairo0811/CertiChain) | Septiembre - Diciembre 2025 |
| 3 | Desarrollo de Software con Tecnología Propietaria 2 (ISO-710) | **Digital Sanctuary** | Mayo - Agosto 2026 |

Los tres proyectos son independientes y representan una continuidad **formativa y cronológica**, no versiones técnicas de una misma aplicación.

---

## 🚀 Evolución v1.1.0

La versión **1.1.0** fortalece la aplicación después del cierre del roadmap funcional original.

### Integridad de datos

- Room actualizado de **schema v3 a v4**.
- Foreign keys entre `Book`, `Annotation`, `Bookmark` y `KnowledgeLink`.
- Eliminación en cascada de notas y bookmarks al borrar un libro.
- Eliminación en cascada de knowledge links al borrar sus anotaciones.
- `MIGRATION_3_4` no destructiva: conserva relaciones válidas y descarta únicamente filas huérfanas incompatibles con las nuevas restricciones.
- Prueba de migración que crea una base SQLite v3 real y la abre mediante Room v4.

### Reader hardening

- EPUB corruptos o ilegibles ya no propagan excepciones hacia la UI.
- Entradas XHTML/HTML limitadas a **2 MiB** para reducir riesgo de consumo de memoria excesivo.
- Límite defensivo de **500 capítulos** por EPUB.
- EPUB sin contenido legible muestran un fallback controlado.
- PDF inválidos, inaccesibles o sin páginas fallan de forma segura.
- Nuevas pruebas Robolectric generan EPUB válidos, corruptos y sobredimensionados en tiempo de ejecución.

### Ingeniería

- `versionCode` **110** y `versionName` **1.1.0**.
- Android CI también valida ramas `feature/**`.
- Artifact de prueba: `digital-sanctuary-v1.1.0-debug-apk`.

---

## ✨ Capacidades principales

### 📚 Library Management

- CRUD completo de libros.
- ISBN, editorial, número de páginas, descripción y portada.
- Estados `TO_READ`, `READING`, `COMPLETED`, `PAUSED` y `ABANDONED`.
- Favoritos y valoración.
- Búsqueda por título, autor, categoría, ISBN y editorial.
- Filtros por estado, categoría y favoritos.
- Orden por reciente, título, autor y progreso.
- Persistencia local con Room/SQLite.

### 📖 Reader

- Importación mediante Android Storage Access Framework.
- Soporte para **EPUB** y **PDF**.
- EPUB procesado localmente desde XHTML/HTML.
- PDF renderizado con `PdfRenderer` nativo de Android.
- Navegación por capítulos o páginas.
- Posición y progreso persistentes.
- Marcadores por ubicación.
- Notas vinculadas a la ubicación de lectura.
- Ajustes visuales orientados a lectura tranquila/e-ink.

### 🧠 Knowledge Hub

- Notas, highlights e insights asociados a libros.
- Tags normalizados.
- Búsqueda global sobre biblioteca y conocimiento.
- Relaciones entre notas mediante enlaces persistentes.
- Conteo de conexiones.
- Exportación a Markdown mediante Android Share Sheet.
- Creación de conocimiento directamente desde el Reader.

### ✨ Gemini AI Assistance

- Arquitectura desacoplada mediante `AiAssistant`.
- Resumen de fragmentos.
- Explicación de conceptos.
- Extracción de insights.
- Síntesis contextual de notas guardadas.
- Guardado de respuestas útiles como `AI Insight`.
- Límite de contexto para evitar solicitudes excesivas.

> Los PDF se renderizan localmente. La aplicación **no extrae ni envía automáticamente el contenido visual de páginas PDF a Gemini**.

---

## 🔐 Seguridad y privacidad

Digital Sanctuary no requiere una credencial Gemini dentro del repositorio.

Configuración recomendada para producción:

```env
AI_PROXY_URL=https://tu-backend.example.com/ai
```

El backend/proxy conserva la credencial real y el APK únicamente conoce el endpoint.

Fallback opcional para desarrollo local:

```env
GEMINI_API_KEY=tu_clave_local
```

`GEMINI_API_KEY` se compila únicamente en builds `debug`; los builds `release` mantienen esa clave vacía.

Protecciones vigentes:

- Android Auto Backup desactivado para biblioteca, progreso, notas y Knowledge Hub.
- Reglas explícitas de exclusión para cloud backup y device transfer.
- Timeouts de conexión, escritura, lectura y llamada total para IA.
- Errores de proxy/Gemini convertidos en `AiResult.Error`.
- Credenciales, `.env` y keystores fuera del control de versiones.

---

## 🏗️ Arquitectura

```text
Jetpack Compose UI
        │
        ▼
MainViewModel / StateFlow
        │
        ├──────────────► ReaderEngine
        │                 ├── EPUB (ZIP + HTML)
        │                 └── PDF (PdfRenderer)
        │
        ├──────────────► AiAssistant
        │                 ├── AI Proxy (producción)
        │                 └── Gemini API (debug local)
        │
        ▼
Repository
        │
        ▼
AppDao / Room
        │
        ▼
SQLite
```

Principios aplicados:

- separación UI / aplicación / datos;
- Repository Pattern;
- estado reactivo con StateFlow;
- operaciones de I/O con Coroutines;
- persistencia mediante migraciones explícitas;
- integridad referencial con foreign keys y cascadas;
- integración de IA detrás de una abstracción intercambiable;
- credenciales fuera del control de versiones.

---

## 🗄️ Modelo persistente

Room se encuentra en **schema version 4** y conserva migraciones explícitas:

```text
v1 → v2
Biblioteca avanzada, estados y metadatos

v2 → v3
Documentos reales, posición de lectura,
tags, bookmarks y knowledge links

v3 → v4
Integridad referencial, foreign keys,
cascadas y limpieza controlada de huérfanos
```

Entidades principales:

- `Book`
- `Annotation`
- `Bookmark`
- `KnowledgeLink`
- `AppSetting`

No se utiliza migración destructiva como estrategia normal de actualización.

---

## 🧱 Stack tecnológico

### 📱 Android / Frontend

- Kotlin
- Jetpack Compose
- Material 3
- ViewModel, StateFlow y Coroutines
- Coil

### ⚙️ Servicios e integración

- Repository Pattern
- Retrofit, OkHttp y Moshi
- Android Storage Access Framework
- `PdfRenderer`
- EPUB mediante ZIP/XHTML

### 🗄️ Datos

- Room
- SQLite
- KSP
- Migraciones explícitas
- Foreign keys y cascadas

### 🤖 IA

- Google Gemini mediante `AiAssistant`
- AI Proxy opcional para producción
- Gemini directo únicamente como fallback de desarrollo

### 🧪 Testing y DevOps

- JUnit
- Robolectric SDK 36
- Compose UI Test
- Roborazzi
- MockWebServer
- Pruebas reales de migración SQLite/Room
- Gradle Wrapper 9.3.1
- JDK 21
- GitHub Actions

---

## 🧪 Calidad y CI

El workflow `Android CI` valida cambios sobre `main`, ramas `agent/**`, `chore/**`, `feature/**` y pull requests hacia `main` mediante:

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
```

La suite cubre lógica de negocio, Robolectric, regresión visual, red/IA, integridad de Room, migración v3→v4 y Reader EPUB. El pipeline genera además un APK debug para smoke testing en dispositivo.

---

## 🗺️ Roadmap

| Fase | Alcance | Estado |
|---|---|:---:|
| 1 | Stabilization, Android base, Room, CI | ✅ |
| 2 | Library Management completo | ✅ |
| 3 | Reader EPUB/PDF, progreso y bookmarks | ✅ |
| 4 | Knowledge Hub, tags, enlaces, búsqueda y exportación | ✅ |
| 5 | Gemini AI Assistance | ✅ |

**Roadmap funcional v1.0.0 completado.**

Las mejoras posteriores se gestionan mediante versionado semántico. v1.0.1 fue mantenimiento técnico/seguridad y v1.1.0 fortalece Reader y persistencia sin reabrir fases académicas ya cerradas.

---

## 📦 Identidad de aplicación

```text
Application ID: com.jairomatias.digitalsanctuary
Namespace:      com.jairomatias.digitalsanctuary
Version:        1.1.0
Version code:   110
Room schema:    4
```

La identidad visual del launcher utiliza una marca propia inspirada en un **libro abierto dentro de un santuario**.

---

## 🚀 Ejecución

Requisitos recomendados:

- Android Studio compatible con el stack actual;
- JDK 21;
- dispositivo/emulador Android API 24 o superior.

El repositorio incluye Gradle Wrapper.

Compilar:

```bash
./gradlew assembleDebug
```

Windows PowerShell/CMD:

```powershell
.\gradlew.bat assembleDebug
```

Pruebas:

```bash
./gradlew testDebugUnitTest
```

La IA es opcional. Sin `AI_PROXY_URL` o clave de desarrollo, biblioteca, Reader y Knowledge Hub continúan funcionando.

---

## 👨‍💻 Autor y mantenimiento

**Francis Jairo Matías Rosario**  
Matrícula: **A00115261**  
Universidad APEC (UNAPEC)  
Ingeniería de Software

Digital Sanctuary forma parte de una colección de proyectos académicos evolucionados posteriormente con estándares de desarrollo profesional.

<p align="center"><strong>Read · Think · Preserve</strong></p>
