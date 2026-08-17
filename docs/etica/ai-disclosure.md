# Declaración de uso de IA generativa — SGROAS

**Proyecto:** SGROAS — Sistema de Gestión de Recursos Operativos, Administrativos y de Seguridad
**Documento:** Declaración de transparencia sobre el uso de herramientas de IA generativa
**Fecha:** 16 de agosto de 2026

## 1. Fase

Este proyecto (entregas 1A, 1B, 2 y entrega final) utilizó herramientas de IA
generativa en las siguientes fases del ciclo de vida:

| Fase | Alcance del uso |
|---|---|
| Análisis y diseño | Redacción de borradores de historias de usuario, casos de uso y SRS; sugerencia de estructura del repositorio |
| Implementación | Asistencia en la generación y revisión de código (Spring Boot, Angular), procedimientos SQL y scripts de prueba |
| Verificación | Revisión de casos de prueba, análisis de cobertura y de resultados de mediciones |
| Documentación | Redacción y revisión de capítulos del informe académico y documentación de apoyo |
| Estadística | Desarrollo de scripts de análisis (k6, SUS) y generación de figuras reproducibles |

## 2. Propósito

El propósito del uso de IA generativa fue incrementar la productividad del equipo
y la calidad de los artefactos, manteniendo siempre la verificación humana:

- Generar borradores iniciales que el equipo revisó, corrigió y validó contra los
  requisitos reales del sistema.
- Asistir en tareas repetitivas (estructura de documentos, plantillas, scripts de
  análisis) liberando tiempo para la validación empírica.
- **No** se utilizó para fabricar datos, cifras, referencias o evidencias: todos
  los resultados reportados (k6, SUS, JaCoCo, Lighthouse, ZAP) provienen de
  mediciones reales ejecutadas y archivadas en `docs/mediciones/`.

## 3. Herramientas utilizadas

- Asistentes de código y edición integrados en el IDE (autocompletado y refactor).
- Herramientas conversacionales de IA generativa para redacción y revisión.

## 4. Revisión del equipo

Todo artefacto asistido por IA fue revisado por al menos un integrante del equipo
antes de su integración:

- Los scripts de análisis (k6 y SUS) fueron ejecutados y sus salidas contrastadas
  contra los datos crudos versionados.
- El código generado se sometió a los mismos estándares que el código escrito a
  mano (análisis estático, pruebas, revisión por pares).
- Las referencias del informe fueron verificadas individualmente; ninguna cita se
  aceptó sin verificar su existencia real y DOI.

## 5. Transparencia y reproducibilidad

- Toda figura y tabla numérica del informe es regenerable con los scripts
  versionados en `scripts/`.
- Este documento se revisa en cada entrega y se actualiza si cambian las
  herramientas o el alcance del uso.
