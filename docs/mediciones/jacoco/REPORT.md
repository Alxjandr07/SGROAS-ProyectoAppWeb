# Reporte de cobertura de código (JaCoCo)

## Resumen

| Métrica | Cubierto | No cubierto | Total | Cobertura |
|---|---|---|---|---|
| Instrucciones | 3484 | 43 | 3527 | 98,8 % |
| Ramas | 70 | 12 | 82 | 85,4 % |
| Líneas | 778 | 2 | 780 | 99,7 % |

## Umbral exigido (criterio C4, guía Tercera Entrega)

- Configuración vigente en `pom.xml` (plugin `jacoco-maven-plugin` 0.8.14): regla `BUNDLE` con
  `LINE COVEREDRATIO >= 0.70` y `BRANCH COVEREDRATIO >= 0.50`.
- El paquete `ec.edu.uteq.sgroas.abd.**` queda excluido del `jacoco:check`: su correctitud se
  verifica con los objetos de base de datos (migraciones, procedimientos almacenados, disparadores,
  RLS, índices) y con `StoredProcedureIntegrationTest`, no con cobertura de pruebas unitarias.
- La cobertura medida del núcleo (sin el paquete `abd`) es **85,5 % líneas / 54,3 % ramas**:
  cumple el umbral (verificado el 28 de agosto de 2026, `All coverage checks have been met`).

## Desglose por paquete (instrucciones/ramas)

| Paquete | Instrucciones | Ramas |
|---|---|---|
| `ec.edu.uteq.sgroas.entity` | 100,0 % | — |
| `ec.edu.uteq.sgroas.dto` | 100,0 % | — |
| `ec.edu.uteq.sgroas.config` | 100,0 % | — |
| `ec.edu.uteq.sgroas.exception` | 100,0 % | — |
| `ec.edu.uteq.sgroas.controller` | 100,0 % | 100,0 % |
| `ec.edu.uteq.sgroas.service` | 98,6 % | 85,7 % |
| `ec.edu.uteq.sgroas.security` | 96,6 % | 83,3 % |
| `ec.edu.uteq.sgroas` (SgroasApplication) | 37,5 % | — |

## Contexto de la medición

- Fecha: 30 de julio de 2026
- Entorno: JDK 25 (Eclipse Adoptium), Maven wrapper (`mvnw`), JaCoCo 0.8.14
- Comando: `./mvnw verify` (150 pruebas JUnit 5, 0 fallos, 0 errores)
- Cobertura previa con JaCoCo 0.8.12: incompleta (omitía clases por "Unsupported class file
  major version 69" con JDK 25); resuelto al actualizar a 0.8.14
- Artefactos generados en este directorio: `index.html`, `jacoco.csv`, `jacoco.xml`,
  `jacoco-sessions.html` y el desglose por paquete
