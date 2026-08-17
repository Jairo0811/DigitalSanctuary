<h1 align="center">Digital Sanctuary</h1>

<p align="center">
  <strong>Un santuario digital para leer, pensar y preservar conocimiento.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/UNAPEC-ISO--710-003B70?style=for-the-badge" alt="UNAPEC ISO-710">
  <img src="https://img.shields.io/badge/Estado-En%20desarrollo-F5A623?style=for-the-badge" alt="Estado en desarrollo">
  <img src="https://img.shields.io/badge/Plataforma-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
</p>

<p align="center">
  <strong>Kotlin · Jetpack Compose · Room · Material 3 · Coroutines</strong>
</p>

> 🎓 **Origen académico:** Digital Sanctuary nace como un **prototipo académico** desarrollado durante la asignatura **Desarrollo de Software con Tecnología Propietaria 2 (ISO-710)** de la **Universidad APEC (UNAPEC)**, en el período **Mayo - Agosto 2026**. La entrega académica original representaba la idea, experiencia y pantallas del producto; la etapa actual convierte ese prototipo en una aplicación Android funcional.

---

## 📖 Descripción

**Digital Sanctuary** es una aplicación móvil Android enfocada en lectura digital y gestión personal del conocimiento. Su objetivo es ofrecer un espacio tranquilo y minimalista donde el usuario pueda organizar sus libros, seguir su progreso de lectura, registrar anotaciones e ideas y convertir lo leído en conocimiento reutilizable.

El concepto combina elementos de un lector digital, un gestor de biblioteca y un sistema de Personal Knowledge Management (PKM).

La evolución actual busca transformar el prototipo académico en software real mediante una implementación nativa para Android con persistencia local, navegación, gestión de notas, seguimiento de progreso y una experiencia visual inspirada en lectores e-ink.

---

## 🕰️ Evolución del proyecto

```text
2026 — UNAPEC · ISO-710
        │
        ├── Concepto de producto
        ├── Diseño de experiencia
        └── Prototipo académico
                │
                │ Sin producto final funcional
                ▼
2026 — Digital Sanctuary
        │
        ├── Kotlin
        ├── Jetpack Compose
        ├── Room
        ├── Material 3
        └── GitHub Actions
                │
                ▼
        Aplicación Android funcional
```

Esta separación permite conservar el valor histórico del trabajo universitario sin atribuirle funcionalidades que todavía no estaban implementadas en la entrega original.

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
| 📁 Entrega original | Prototipo académico |
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
- integración opcional de IA para resúmenes, explicación de fragmentos y generación de insights.

---

## 🧱 Stack tecnológico

| Área | Tecnología |
|---|---|
| Plataforma | Android |
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Design System | Material 3 |
| Persistencia | Room / SQLite |
| Estado | ViewModel + StateFlow |
| Asincronía | Kotlin Coroutines |
| Imágenes | Coil |
| Networking preparado | Retrofit + OkHttp + Moshi |
| Pruebas | JUnit, Robolectric, Compose UI Test, Roborazzi |
| CI | GitHub Actions |

---

## 🏗️ Arquitectura actual

```text
Jetpack Compose UI
        │
        ▼
MainViewModel
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
| Prototipo académico original | ✅ |
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
| IA contextual | ⏳ |

> **Leyenda:** ✅ disponible · 🔄 en progreso · ⏳ planificado

---

## 🗺️ Hoja de ruta

### Fase 1 — Stabilization

- [x] Estructura Android base.
- [x] Persistencia local con Room.
- [x] Navegación principal.
- [x] Gestión inicial de biblioteca y notas.
- [x] Documentar origen académico.
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

### Fase 5 — AI Assistance

- [ ] Resumir fragmentos.
- [ ] Explicar conceptos.
- [ ] Generar insights sugeridos.
- [ ] Consultar notas y biblioteca de forma contextual.

---

## 📌 Nota sobre el alcance académico

Digital Sanctuary **no se presenta como una aplicación completamente funcional desarrollada durante la asignatura ISO-710**. El proyecto universitario original fue un prototipo. La implementación Android funcional corresponde a la evolución posterior del concepto.

Esta distinción se mantiene explícitamente para preservar la trazabilidad académica y técnica del proyecto.

---

## 👨‍💻 Autor

**Francis Jairo Matías Rosario**  
Matrícula: **A00115261**  
Universidad APEC (UNAPEC)
