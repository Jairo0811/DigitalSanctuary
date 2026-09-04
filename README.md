<p align="center">
  <img src="branding/cover.png" alt="Digital Sanctuary — Read, Think, Preserve" width="720" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/UNAPEC-ISO--710-003B70?style=for-the-badge" alt="UNAPEC ISO-710">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.1-111827?style=for-the-badge" alt="Version 1.0.1">
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

**Digital Sanctuary** documenta su continuidad académica mediante relaciones verificables entre estudiantes y profesores. Al tratarse de un proyecto académico individual, no existe un compañero recurrente dentro de este proyecto; la continuidad verificable corresponde al profesor **Ing. Pedro José Ramirez Rodriguez**.

#### 👥 Continuidad por estudiante

Digital Sanctuary fue desarrollado como proyecto académico individual por **Francis Jairo Matías Rosario (A00115261)**. Por esa razón, no existe un equipo de compañeros dentro de este proyecto que pueda utilizarse para establecer una continuidad estudiantil con otro repositorio.

#### 👨‍🏫 Continuidad por profesor

Digital Sanctuary cierra una secuencia docente de **tres proyectos** desarrollados en diferentes etapas de la carrera con el profesor **Ing. Pedro José Ramirez Rodriguez**.

| Orden | Asignatura | Proyecto | Período |
|---:|---|---|---|
| 1 | Bases de Datos 1 (INF-164) | [**NutriFlow**](https://github.com/Jairo0811/NutriFlow) | Mayo - Agosto 2024 |
| 2 | Fundamentos de Seguridad de Software (ISO-915) | [**CertiChain**](https://github.com/Jairo0811/CertiChain) | Septiembre - Diciembre 2025 |
| 3 | Desarrollo de Software con Tecnología Propietaria 2 (ISO-710) | **Digital Sanctuary** | Mayo - Agosto 2026 |

La secuencia es **formativa y cronológica**: parte de fundamentos de datos y modelado, continúa con seguridad de software y blockchain, y culmina en una aplicación Android nativa orientada a lectura, conocimiento e integración de IA. Los tres proyectos son independientes y no constituyen dependencias técnicas ni versiones de una misma aplicación.

---

## 🛠️ Mantenimiento v1.0.1

La versión **1.0.1** conserva intacto el alcance funcional de v1.0.0 y corrige deuda técnica detectada después del cierre del roadmap:

- paquetes Kotlin migrados de `com.example.*` al namespace definitivo `com.jairomatias.digitalsanctuary.*`;
- Gradle Wrapper versionado para builds reproducibles (`gradlew` / `gradlew.bat`);
- CI migrado para compilar y probar mediante `./gradlew`;
- `versionCode` 101 y `versionName` 1.0.1;
- backups automáticos de datos locales desactivados por privacidad;
- reglas explícitas de exclusión para cloud backup y device transfer;
- cliente de IA con timeouts de conexión, escritura, lectura y llamada total;
- configuración de proxy/API inyectable para pruebas;
- pruebas HTTP del asistente de IA con `MockWebServer`.

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

## 🔐 Seguridad y privacidad

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

A partir de v1.0.1:

- Android Auto Backup se mantiene desactivado para evitar que biblioteca, progreso, notas y Knowledge Hub salgan del dispositivo sin una política de sincronización explícita;
- `backup_rules.xml` y `data_extraction_rules.xml` excluyen almacenamiento interno, preferencias y base de datos;
- el cliente HTTP de IA aplica límites temporales para evitar llamadas bloqueadas indefinidamente;
- las respuestas de error de proxy/Gemini se convierten en `AiResult.Error` y no derriban el flujo principal.

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

### 📱 Android / Frontend

<p>
  <img src="https://skillicons.dev/icons?i=kotlin,androidstudio" alt="Kotlin y Android Studio" />
</p>

<p>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Material%203-6750A4?style=flat-square&logo=materialdesign&logoColor=white" alt="Material 3" />
</p>

- Kotlin
- Jetpack Compose
- Material 3
- ViewModel, StateFlow y Coroutines
- Coil

### ⚙️ Servicios e integración

<p>
  <img src="https://img.shields.io/badge/Retrofit-HTTP-48B983?style=flat-square" alt="Retrofit" />
  <img src="https://img.shields.io/badge/OkHttp-HTTP-3E4348?style=flat-square" alt="OkHttp" />
  <img src="https://img.shields.io/badge/Moshi-JSON-7C4DFF?style=flat-square" alt="Moshi" />
</p>

- Repository Pattern
- Retrofit, OkHttp y Moshi
- Android Storage Access Framework
- `PdfRenderer`
- EPUB mediante ZIP/XHTML
- **No requiere un backend propio obligatorio** para biblioteca, lector o conocimiento local.

### 🗄️ Datos

<p>
  <img src="https://skillicons.dev/icons?i=sqlite" alt="SQLite" />
  <img src="https://img.shields.io/badge/Room-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Room" />
</p>

- Room
- SQLite
- KSP
- Migraciones explícitas

### 🤖 IA

<p>
  <img src="https://img.shields.io/badge/Google%20Gemini-8E75B2?style=flat-square&logo=googlegemini&logoColor=white" alt="Google Gemini" />
</p>

- Google Gemini mediante la abstracción `AiAssistant`
- AI Proxy opcional para producción
- Gemini directo únicamente como fallback de desarrollo

### 🧪 Testing y DevOps

<p>
  <img src="https://skillicons.dev/icons?i=gradle,git,github,githubactions" alt="Gradle, Git, GitHub y GitHub Actions" />
</p>

- JUnit
- Robolectric
- Compose UI Test
- Roborazzi
- MockWebServer
- Gradle Wrapper 9.3.1
- JDK 21
- GitHub Actions

---

## 🧪 Calidad y CI

El workflow `Android CI` valida cambios sobre `main`, ramas `agent/**`, ramas `chore/**` y pull requests hacia `main` mediante el Gradle Wrapper versionado:

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
```

La suite incluye pruebas unitarias, Robolectric, regresión visual con Roborazzi y pruebas HTTP del asistente de IA mediante MockWebServer. El pipeline genera además un APK debug como artifact para smoke testing en dispositivo.

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

Las mejoras posteriores se gestionan como nuevas versiones y no como fases pendientes del roadmap académico original. La v1.0.1 es una versión de mantenimiento técnico y seguridad.

---

## 📦 Identidad de aplicación

```text
Application ID: com.jairomatias.digitalsanctuary
Namespace:      com.jairomatias.digitalsanctuary
Version:        1.0.1
Version code:   101
```

El árbol de código también utiliza ahora el namespace definitivo `com.jairomatias.digitalsanctuary.*`, eliminando los paquetes de plantilla `com.example.*`.

La identidad visual del launcher utiliza una marca propia inspirada en un **libro abierto dentro de un santuario**, reemplazando los recursos genéricos iniciales de Android.

---

## 🚀 Ejecución

Requisitos recomendados:

- Android Studio compatible con el stack actual;
- JDK 21 para reproducir el entorno de CI y los tests Robolectric SDK 36;
- dispositivo/emulador Android con API mínima 24.

El repositorio incluye Gradle Wrapper, por lo que no es necesario instalar una versión global de Gradle.

Para compilar:

```bash
./gradlew assembleDebug
```

En Windows PowerShell/CMD:

```powershell
.\gradlew.bat assembleDebug
```

Para pruebas:

```bash
./gradlew testDebugUnitTest
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
