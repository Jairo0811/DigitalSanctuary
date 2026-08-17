<h1 align="center">Digital Sanctuary</h1>

<p align="center">
  <strong>Un santuario digital para leer, pensar y preservar conocimiento.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/UNAPEC-ISO--710-003B70?style=for-the-badge" alt="UNAPEC ISO-710">
  <img src="https://img.shields.io/badge/Estado-En%20desarrollo-F5A623?style=for-the-badge" alt="Estado en desarrollo">
  <img src="https://img.shields.io/badge/Plataforma-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Prototipado-Google%20AI%20Studio-4285F4?style=for-the-badge&logo=google&logoColor=white" alt="Google AI Studio">
</p>

<p align="center">
  <strong>Google AI Studio · Kotlin · Jetpack Compose · Room · Material 3 · Coroutines</strong>
</p>

> 🎓 **Origen académico:** Digital Sanctuary nace como un **prototipo académico creado con Google AI Studio** durante la asignatura **Desarrollo de Software con Tecnología Propietaria 2 (ISO-710)** de la **Universidad APEC (UNAPEC)**, en el período **Mayo - Agosto 2026**. Google AI Studio fue utilizado como herramienta de generación y prototipado asistido por inteligencia artificial para materializar la propuesta inicial. La etapa actual toma esa base y la evoluciona hacia una aplicación Android funcional, mantenible y verificable.

---

## 📖 Descripción

**Digital Sanctuary** es una aplicación móvil Android enfocada en lectura digital y gestión personal del conocimiento. Su objetivo es ofrecer un espacio tranquilo y minimalista donde el usuario pueda organizar sus libros, seguir su progreso de lectura, registrar anotaciones e ideas y convertir lo leído en conocimiento reutilizable.

El concepto combina elementos de un lector digital, un gestor de biblioteca y un sistema de Personal Knowledge Management (PKM).

La primera materialización técnica del concepto fue generada y prototipada mediante **Google AI Studio**. El repositorio conserva esa procedencia tecnológica: la base Android inicial y su estructura fueron posteriormente trasladadas a GitHub para continuar el desarrollo como un proyecto de software convencional, aplicando control de versiones, CI, pruebas, persistencia y evolución incremental.

La implementación actual utiliza Android nativo con Kotlin y Jetpack Compose. El objetivo ya no es únicamente demostrar visualmente el concepto, sino convertir el prototipo generado con asistencia de IA en software real y mantenible.

---

## 🕰️ Evolución del proyecto

```text
2026 — UNAPEC · ISO-710
        │
        ├── Concepto de producto
        ├── Diseño de experiencia
        ├── Google AI Studio
        └── Prototipo generado con asistencia de IA
                │
                ▼
2026 — Digital Sanctuary · Evolución
        │
        ├── Código fuente versionado en GitHub
        ├── Kotlin
        ├── Jetpack Compose
        ├── Room / SQLite
        ├── Material 3
        ├── Pruebas automatizadas
        └── GitHub Actions
                │
                ▼
        Aplicación Android funcional
```

Esta separación conserva la procedencia del prototipo y permite distinguir entre la generación inicial realizada mediante Google AI Studio y el trabajo de ingeniería aplicado posteriormente para convertirlo en una aplicación funcional.

---

## 🎓 Información académica

| Información | Detalle |
|---|---|
| 👨‍🎓 Estudiante | Francis Jairo Matías Rosario |
| 🆔 Matrícula | A00115261 |
| 📖 Asignatura | Desarrollo de Software con Tecnología Propietaria 2 (ISO-710) |
| 👨‍🏫 Profesor | Ing. Pedro José Ramirez Rodriguez |
| 🏫 Institución | Universidad APEC (UNAPEC) |
| 📅 Período académico | Mayo - Agosto 2026 |
| 🤖 Herramienta de prototipado | Google AI Studio |
| 📁 Entrega original | Prototipo académico generado con asistencia de IA |
| 📱 Implementación actual | Aplicación Android funcional en desarrollo |

---

## 🎯 Visión del producto

Digital Sanctuary busca cubrir cuatro necesidades principales:

| Necesidad | Respuesta de Digital Sanctuary |
|---|---|
| Organizar una biblioteca personal | Catálogo de libros y seguimiento de progreso |
| Leer con menos distracciones | Interfaz minimalista inspirada en e-ink |
| Capturar ideas importantes | Notas y anotaciones estructuradas |
| Convertir lectura en conocimiento | Clasificación de ideas como Thesis, Insight y Source |

El flujo conceptual del producto es:

```text
Libro → Lectura → Anotación → Insight → Nota → Conocimiento
```

---

## ✨ Funcionalidades

### Actualmente implementadas

- biblioteca local de libros;
- seguimiento de progreso de lectura;
- selección de libro activo;
- persistencia local con Room;
- anotaciones clasificadas como Thesis, Insight y Source;
- notas asociadas a libros;
- configuración persistente;
- navegación entre Library, Reading, Notes y Settings;
- personalización inicial de lectura;
- interfaz construida con Jetpack Compose y Material 3.

### Próximas fases

- edición y eliminación completa de libros;
- importación de EPUB/PDF;
- marcadores y posición real de lectura;
- búsqueda y filtros;
- biblioteca por categorías;
- relaciones entre notas;
- búsqueda global de conocimiento;
- exportación de notas;
- mejoras de accesibilidad;
- modo e-ink avanzado;
- integración opcional de Gemini para resúmenes, explicación de fragmentos y generación de insights.

---

## 🧱 Stack tecnológico

Digital Sanctuary combina herramientas de **IA generativa para prototipado** con un stack Android nativo para su implementación y evolución como producto.

| Área | Tecnología | Uso en el proyecto |
|---|---|---|
| Generación y prototipado asistido por IA | **Google AI Studio** | Creación y materialización inicial del prototipo académico |
| IA generativa | **Google Gemini / Gemini API (prevista)** | Base tecnológica asociada a AI Studio; integración funcional de IA planificada |
| Plataforma | **Android** | Plataforma móvil objetivo |
| Lenguaje | **Kotlin** | Lenguaje principal de la aplicación |
| UI declarativa | **Jetpack Compose** | Construcción de pantallas y componentes |
| Design System | **Material 3** | Componentes y fundamentos visuales |
| Persistencia | **Room + SQLite** | Biblioteca, anotaciones y configuración local |
| Arquitectura de estado | **ViewModel + StateFlow** | Estado reactivo de la interfaz |
| Asincronía | **Kotlin Coroutines** | Operaciones asíncronas y acceso a datos |
| Carga de imágenes | **Coil** | Portadas e imágenes remotas |
| Networking | **Retrofit + OkHttp** | Infraestructura HTTP preparada para servicios externos |
| Serialización | **Moshi** | Conversión JSON para integraciones HTTP |
| Procesamiento de anotaciones Room | **KSP** | Generación de código en tiempo de compilación |
| Pruebas unitarias | **JUnit + Robolectric** | Validación de lógica Android/JVM |
| Pruebas de interfaz | **Compose UI Test** | Validación de componentes Compose |
| Pruebas visuales | **Roborazzi** | Capturas y regresión visual |
| Automatización | **Gradle** | Build, dependencias y tareas del proyecto |
| Integración continua | **GitHub Actions** | Validación automática del build y pruebas |
| Control de versiones | **Git + GitHub** | Evolución y trazabilidad del código fuente |

### Papel de Google AI Studio

**Google AI Studio forma parte del origen tecnológico del proyecto**, pero no sustituye el stack de ejecución de la aplicación. Su función inicial fue servir como entorno de generación/prototipado asistido por IA. El producto resultante se está transformando en un proyecto Android convencional cuya ejecución depende de Kotlin, Jetpack Compose, Room y el resto del ecosistema Android.

Esto permite describir correctamente el proyecto como:

> **Prototipado y generación inicial con Google AI Studio + desarrollo Android nativo con Kotlin y Jetpack Compose.**

La capacidad de IA contextual mediante Gemini se considera una fase posterior. No se presenta como una funcionalidad productiva mientras su integración no esté implementada y validada.

---

## 🏗️ Arquitectura actual

```text
Google AI Studio
  (prototipo inicial)
        │
        ▼
Código Android versionado
        │
        ▼
Jetpack Compose UI
        │
        ▼
MainViewModel / StateFlow
        │
        ▼
Repository
        │
        ▼
Room / AppDao
        │
        ▼
SQLite
```

Estructura principal:

```text
app/src/main/java/com/example/
├── data/
│   ├── Annotation.kt
│   ├── AppDao.kt
│   ├── AppDatabase.kt
│   ├── Book.kt
│   ├── Repository.kt
│   └── Setting.kt
│
├── ui/
│   ├── MainViewModel.kt
│   ├── components/
│   ├── screens/
│   │   ├── LibraryScreen.kt
│   │   ├── MainScreen.kt
│   │   ├── NotesScreen.kt
│   │   ├── ReaderScreen.kt
│   │   └── SettingsScreen.kt
│   └── theme/
│
└── MainActivity.kt
```

> El paquete `com.example` pertenece todavía a la base inicial del prototipo y será sustituido por el namespace definitivo del proyecto en una fase de estabilización.

---

## ✅ Estado actual

Digital Sanctuary se encuentra en **Fase 1 — Stabilization**.

| Componente | Estado |
|---|:---:|
| Prototipo académico en Google AI Studio | ✅ |
| Código Android trasladado/versionado en GitHub | ✅ |
| Proyecto Android nativo | ✅ |
| Kotlin + Jetpack Compose | ✅ |
| Biblioteca local | ✅ |
| Room / SQLite | ✅ |
| Notas y anotaciones | ✅ |
| Seguimiento de progreso | ✅ |
| Configuraciones persistentes | ✅ |
| GitHub Actions | 🔄 |
| Namespace definitivo | ⏳ |
| Gestión completa de libros | ⏳ |
| Importación EPUB/PDF | ⏳ |
| Reader de contenido real | ⏳ |
| Knowledge Graph / relaciones | ⏳ |
| Integración funcional con Gemini | ⏳ |

> **Leyenda:** ✅ disponible · 🔄 en progreso · ⏳ planificado

---

## 🗺️ Hoja de ruta

### Fase 1 — Stabilization

- [x] Prototipo inicial mediante Google AI Studio.
- [x] Estructura Android base.
- [x] Persistencia local con Room.
- [x] Navegación principal.
- [x] Gestión inicial de biblioteca y notas.
- [x] Documentar origen académico y tecnológico.
- [ ] Validar build limpio en CI.
- [ ] Sustituir namespace de plantilla.
- [ ] Integrar identidad visual definitiva.

### Fase 2 — Library Management

- [ ] Crear libros.
- [ ] Editar libros.
- [ ] Eliminar libros.
- [ ] Buscar y filtrar.
- [ ] Categorías y estados de lectura.
- [ ] Mejorar portadas y metadatos.

### Fase 3 — Reader

- [ ] Importar EPUB/PDF.
- [ ] Renderizar contenido real.
- [ ] Guardar posición de lectura.
- [ ] Marcadores.
- [ ] Ajustes de tipografía y contraste.
- [ ] Modo e-ink avanzado.

### Fase 4 — Knowledge Hub

- [ ] Highlights y notas vinculadas.
- [ ] Relaciones entre conceptos.
- [ ] Etiquetas.
- [ ] Búsqueda global.
- [ ] Exportación de conocimiento.

### Fase 5 — Gemini AI Assistance

- [ ] Definir integración segura con Gemini API.
- [ ] Resumir fragmentos.
- [ ] Explicar conceptos.
- [ ] Generar insights sugeridos.
- [ ] Consultar notas y biblioteca de forma contextual.
- [ ] Mantener claves y credenciales fuera del cliente cuando corresponda.

---

## 📌 Nota sobre el alcance académico

Digital Sanctuary **no se presenta como una aplicación completamente funcional desarrollada durante la asignatura ISO-710**. La entrega universitaria fue un prototipo creado con asistencia de **Google AI Studio**. La implementación Android funcional corresponde a la evolución posterior del concepto.

Esta distinción mantiene explícita tanto la trazabilidad académica como la procedencia de las herramientas de IA utilizadas en la creación del prototipo.

---

## 👨‍💻 Autor

**Francis Jairo Matías Rosario**  
Matrícula: **A00115261**  
Universidad APEC (UNAPEC)
