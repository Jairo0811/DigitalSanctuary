<p align="center">
  <img src="branding/cover.png" alt="Digital Sanctuary — Read, Think, Preserve" width="720" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-111827?style=for-the-badge" alt="Version 1.0.0">
  <img src="https://img.shields.io/badge/Estado-Roadmap%20completado-16A34A?style=for-the-badge" alt="Roadmap completado">
  <img src="https://img.shields.io/badge/Plataforma-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/UNAPEC-ISO--710-003B70?style=for-the-badge" alt="UNAPEC ISO-710">
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

Digital Sanctuary continúa una trayectoria académica previa con el profesor **Ing. Pedro José Ramirez Rodriguez**. En **Mayo - Agosto 2024**, la asignatura **Bases de Datos 1 (INF-164)** produjo el trabajo que posteriormente evolucionó hacia [NutriFlow](https://github.com/Jairo0811/NutriFlow). En **Mayo - Agosto 2026**, **ISO-710** dio origen al prototipo de Digital Sanctuary.

---

## ✨ Capacidades de v1.0.0

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
- EPUB procesado localmente desde XHTML/HTML contenido en el archivo.
- PDF renderizado con `PdfRenderer` nativo de Android.
- Navegación por capítulos o páginas.
- Posición y progreso persistentes.
- Marcadores por ubicación.
- Notas vinculadas a la ubicación de lectura.
- Ajustes de tamaño de texto y experiencia visual orientada a lectura tranquila/e-ink.

### 🧠 Knowledge Hub

- Notas, highlights e insights asociados a libros.
- Tags normalizados.
- Búsqueda global sobre biblioteca y conocimiento.
- Relaciones entre notas mediante enlaces persistentes.
- Conteo de conexiones.
- Exportación de la base de conocimiento a Markdown mediante Android Share Sheet.
- Creación de conocimiento directamente desde el Reader.

### ✨ Gemini AI Assistance

- Arquitectura desacoplada mediante `AiAssistant`.
- Resumen de fragmentos.
- Explicación de conceptos.
- Extracción de insights.
- Síntesis contextual de notas guardadas.
- Guardado de respuestas útiles como `AI Insight` dentro del Knowledge Hub.
- Límite de contexto para evitar solicitudes excesivas.

> Los PDF se renderizan localmente. La versión actual **no extrae ni envía automáticamente el contenido visual de páginas PDF a Gemini**.

---

## 🔐 Seguridad de IA

Digital Sanctuary no requiere una credencial Gemini dentro del repositorio.

La configuración recomendada para producción es:

```env
AI_PROXY_URL=https://tu-backend.example.com/ai
```

El backend/proxy conserva la credencial real y el APK únicamente conoce el endpoint.

Para desarrollo local existe un fallback opcional:

```env
GEMINI_API_KEY=tu_clave_local
```

Ese fallback se compila **solo en builds `debug`**. Los builds `release` mantienen `GEMINI_API_KEY` vacío y deben utilizar `AI_PROXY_URL`.

Nunca se deben versionar `.env`, keystores ni credenciales reales.

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
- integración de IA detrás de una abstracción intercambiable;
- credenciales fuera del control de versiones.

---

## 🗄️ Modelo persistente

Room se encuentra en **schema version 3** y conserva migraciones explícitas:

```text
v1 → v2
Biblioteca avanzada, estados y metadatos

v2 → v3
Documentos reales, posición de lectura,
tags, bookmarks y knowledge links
```

Entidades principales:

- `Book`
- `Annotation`
- `Bookmark`
- `KnowledgeLink`
- `AppSetting`

No se utiliza migración destructiva como estrategia de actualización.

---

## 🧱 Stack tecnológico

<p align="center">
  <img src="https://skillicons.dev/icons?i=kotlin,androidstudio,sqlite,gradle,git,github,githubactions" alt="Kotlin, Android Studio, SQLite, Gradle, Git, GitHub y GitHub Actions" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Room-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Room" />
  <img src="https://img.shields.io/badge/Material%203-6750A4?style=flat-square&logo=materialdesign&logoColor=white" alt="Material 3" />
  <img src="https://img.shields.io/badge/Google%20Gemini-8E75B2?style=flat-square&logo=googlegemini&logoColor=white" alt="Google Gemini" />
</p>

| Área | Tecnologías |
|---|---|
| Android | Kotlin, Jetpack Compose, Material 3 |
| Estado | ViewModel, StateFlow, Coroutines |
| Datos | Room, SQLite, KSP |
| Reader | Storage Access Framework, PdfRenderer, ZIP/XHTML |
| IA | Gemini, OkHttp, AI Proxy opcional |
| Networking | OkHttp, Retrofit, Moshi |
| Imágenes | Coil |
| Testing | JUnit, Robolectric, Compose UI Test, Roborazzi |
| CI | GitHub Actions, JDK 21, Gradle 9.3.1 |

---

## 🧪 Calidad y CI

El workflow `Android CI` valida cada cambio sobre `main` y ramas `agent/**` mediante:

```bash
gradle testDebugUnitTest --stacktrace
gradle assembleDebug --stacktrace
```

La suite incluye pruebas unitarias, Robolectric y regresión visual con Roborazzi. El objetivo de integración es que ninguna fase se fusione a `main` sin pruebas y APK debug construidos correctamente.

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

Las mejoras posteriores se gestionarán como nuevas versiones y no como fases pendientes del roadmap original.

---

## 📦 Identidad de aplicación

```text
Application ID: com.jairomatias.digitalsanctuary
Namespace:      com.jairomatias.digitalsanctuary
Version:        1.0.0
```

La identidad visual del launcher utiliza una marca propia inspirada en un **libro abierto dentro de un santuario**, reemplazando los recursos genéricos iniciales de Android.

---

## 🚀 Ejecución

Requisitos recomendados:

- Android Studio compatible con el stack actual;
- JDK 21 para reproducir el entorno de CI y los tests Robolectric SDK 36;
- dispositivo/emulador Android con API mínima 24.

Para compilar:

```bash
gradle assembleDebug
```

Para pruebas:

```bash
gradle testDebugUnitTest
```

La IA es opcional: sin `AI_PROXY_URL` o clave de desarrollo, el resto de Digital Sanctuary continúa funcionando y la interfaz informa que AI Assistance no está configurada.

---

## 👨‍💻 Autor y mantenimiento

**Francis Jairo Matías Rosario**  
Matrícula: **A00115261**  
Universidad APEC (UNAPEC)  
Ingeniería de Software

Digital Sanctuary forma parte de una colección de proyectos académicos evolucionados posteriormente con estándares de desarrollo profesional.

<p align="center"><strong>Read · Think · Preserve</strong></p>
