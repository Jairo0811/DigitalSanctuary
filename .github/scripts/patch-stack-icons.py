from pathlib import Path

p = Path('README.md')
s = p.read_text(encoding='utf-8')
start = s.index('## 🧱 Stack tecnológico')
end = s.index('\n---\n\n## 🧪 Calidad y CI', start)
new = '''## 🧱 Stack tecnológico

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
- Gradle 9.3.1
- JDK 21
- GitHub Actions
'''
s = s[:start] + new + s[end:]
p.write_text(s, encoding='utf-8')
