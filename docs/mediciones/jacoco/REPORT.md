# Reporte de cobertura de código (JaCoCo)

## Resumen

> **Nota de reconciliación (31-ago-2026):** la tabla anterior de este reporte mostraba 98,8 %/85,4 %/99,7 % de una medición parcial. Se reemplaza por los valores reconciliados con `jacoco.csv`/`jacoco.xml` generados por `./mvnw verify` (commit `66fa254`).

| Métrica | Full bundle (con `abd`) | | | | Core (sin `abd`, sujeto a `jacoco:check`) | | | |
|---|---|---|---|---|---|---|---|---|
| | Cubierto | No cubierto | Total | Cobertura | Cubierto | No cubierto | Total | Cobertura |
| Instrucciones | 4129 | 3009 | 7138 | **57,8 %** | 4129 | 1320 | 5449 | **75,8 %** |
| Ramas | 76 | 176 | 252 | **30,2 %** | 76 | 64 | 140 | **54,3 %** |
| Líneas | 858 | 414 | 1272 | **67,5 %** | 858 | 146 | 1004 | **85,5 %** |

## Umbral exigido (criterio P1, guía Entrega Final)

- Configuración vigente en `pom.xml` (plugin `jacoco-maven-plugin` 0.8.14): regla `BUNDLE` con
  `LINE COVEREDRATIO >= 0.70` y `BRANCH COVEREDRATIO >= 0.50`.
- El paquete `ec.edu.uteq.sgroas.abd.**` queda excluido del `jacoco:check`: su correctitud se
  verifica con los objetos de base de datos (migraciones, procedimientos almacenados, disparadores,
  RLS, índices) y con `StoredProcedureIntegrationTest`, no con cobertura de pruebas unitarias.
- La cobertura medida del núcleo (sin el paquete `abd`) es **75,8 % instrucciones / 54,3 % ramas / 85,5 % líneas**:
   cumple el umbral LINE 0,70 y BRANCH 0,50 (verificado el 28 de agosto de 2026, `All coverage checks have been met`).

## Desglose por paquete (instrucciones/ramas) — reconciliado con `jacoco.csv`

| Paquete | Instrucciones | Ramas | Líneas |
|---|---|---|---|
| `ec.edu.uteq.sgroas` (SgroasApplication) | 37,5 % | — | 33,3 % |
| `ec.edu.uteq.sgroas.abd.controller` | 0,0 % | 0,0 % | 0,0 % *excluido del check* |
| `ec.edu.uteq.sgroas.abd.dto` | 0,0 % | — | 0,0 % *excluido* |
| `ec.edu.uteq.sgroas.abd.service` | 0,0 % | 0,0 % | 0,0 % *excluido* |
| `ec.edu.uteq.sgroas.config` | 96,3 % | — | 93,8 % |
| `ec.edu.uteq.sgroas.controller` | 82,5 % | 18,2 % | 81,0 % |
| `ec.edu.uteq.sgroas.dto` | 100,0 % | — | 100,0 % |
| `ec.edu.uteq.sgroas.entity` | 100,0 % | — | 100,0 % |
| `ec.edu.uteq.sgroas.exception` | 86,7 % | — | 86,4 % |
| `ec.edu.uteq.sgroas.security` | 96,6 % | 83,3 % | 100,0 % |
| `ec.edu.uteq.sgroas.service` | 61,8 % | 51,2 % | 81,8 % |

## Contexto de la medición

- Fecha: 30 de julio de 2026
- Entorno: JDK 25 (Eclipse Adoptium), Maven wrapper (`mvnw`), JaCoCo 0.8.14
- Comando: `./mvnw verify` (150 pruebas JUnit 5, 0 fallos, 0 errores)
- Cobertura previa con JaCoCo 0.8.12: incompleta (omitía clases por "Unsupported class file
  major version 69" con JDK 25); resuelto al actualizar a 0.8.14
- Artefactos generados en este directorio: `index.html`, `jacoco.csv`, `jacoco.xml`,
  `jacoco-sessions.html` y el desglose por paquete
