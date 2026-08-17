from pathlib import Path
import re
p=Path('README.md')
s=p.read_text(encoding='utf-8')
section='''## 🧭 Continuidad académica

**Digital Sanctuary** documenta su continuidad académica mediante relaciones verificables entre estudiantes y profesores. En la colección actual no se ha identificado un compañero recurrente, pero sí existe una **continuidad docente** con [**NutriFlow**](https://github.com/Jairo0811/NutriFlow).

### 👥 Continuidad por estudiante

Digital Sanctuary fue desarrollado como proyecto académico individual por **Francis Jairo Matías Rosario (A00115261)**. Por esa razón, no existe un equipo de compañeros dentro de este proyecto que pueda utilizarse para establecer una continuidad estudiantil con otro repositorio.

### 👨‍🏫 Continuidad por profesor

El profesor **Ing. Pedro José Ramirez Rodriguez** aparece en dos momentos distintos de la trayectoria académica documentada en UNAPEC: primero en **Bases de Datos 1 (INF-164)**, donde surgió el prototipo que posteriormente evolucionó hacia NutriFlow, y dos años después en **Desarrollo de Software con Tecnología Propietaria 2 (ISO-710)** con Digital Sanctuary.

| Orden | Asignatura | Proyecto | Período | Profesor recurrente |
|---:|---|---|---|---|
| 1 | Bases de Datos 1 (INF-164) | [**NutriFlow**](https://github.com/Jairo0811/NutriFlow) | Mayo - Agosto 2024 | **Ing. Pedro José Ramirez Rodriguez** |
| 2 | Desarrollo de Software con Tecnología Propietaria 2 (ISO-710) | **Digital Sanctuary** | Mayo - Agosto 2026 | **Ing. Pedro José Ramirez Rodriguez** |

La relación es **formativa y cronológica**: los proyectos son independientes y la continuidad se fundamenta en el mismo profesor en dos etapas diferentes de la carrera.
'''
pattern=r'## 🧭 Continuidad académica.*?(?=\n---\n\n## 🕰️ Evolución del proyecto)'
new=re.sub(pattern,section.rstrip()+'\n',s,flags=re.S)
if new==s: raise SystemExit('Continuity section not found')
p.write_text(new,encoding='utf-8')
