from pathlib import Path
import re

path = Path("README.md")
text = path.read_text(encoding="utf-8")

section = """## 🧭 Continuidad académica

**Digital Sanctuary** continúa una trayectoria académica previa con el profesor **Ing. Pedro José Ramirez Rodriguez** en la Universidad APEC (UNAPEC). La relación con [**NutriFlow**](https://github.com/Jairo0811/NutriFlow) es **formativa y cronológica**: los proyectos pertenecen a asignaturas distintas y no comparten dependencia técnica, pero documentan la presencia del mismo docente en dos momentos diferentes de la carrera.

La secuencia comenzó en **Mayo - Agosto de 2024** con **Bases de Datos 1 (INF-164)** y el prototipo académico que posteriormente evolucionó hacia NutriFlow. Dos años después, en **Mayo - Agosto de 2026**, continuó con **Desarrollo de Software con Tecnología Propietaria 2 (ISO-710)** y Digital Sanctuary.

| Orden | Código | Asignatura | Proyecto | Período | Enfoque académico |
|---:|---|---|---|---|---|
| 1 | INF-164 | Bases de Datos 1 | [**NutriFlow**](https://github.com/Jairo0811/NutriFlow) | Mayo - Agosto 2024 | Fundamentos de datos, modelado y prototipado de una solución nutricional |
| 2 | ISO-710 | Desarrollo de Software con Tecnología Propietaria 2 | **Digital Sanctuary** | Mayo - Agosto 2026 | Prototipado con Google AI Studio y evolución hacia una aplicación Android funcional |

Vistos en conjunto, ambos proyectos muestran una progresión desde fundamentos de datos y prototipado hasta construcción de software móvil moderno. Cada repositorio conserva su identidad académica original; la continuidad se fundamenta en el **mismo profesor**."""

updated = re.sub(
    r"## 🔗 Continuidad académica.*?(?=\n\n---\n\n## 🕰️ Evolución)",
    section,
    text,
    flags=re.S,
)
if updated == text:
    raise SystemExit("Continuity block not found")
path.write_text(updated, encoding="utf-8")
