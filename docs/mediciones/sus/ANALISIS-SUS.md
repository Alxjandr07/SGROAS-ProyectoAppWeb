# Analisis de usabilidad (SUS) - Bloque C.3

**Tarea K2** | Fecha de generacion: 2026-08-17

## Instrumento

System Usability Scale (Brooke, 1996): 10 items Likert 1-5. Puntuacion por participante = (suma de contribuciones) * 2.5, donde items impares contribuyen `respuesta - 1` y pares `5 - respuesta`.

## Puntuacion por participante

| Codigo | Edad | Sexo | Exp. web | q1 | q2 | q3 | q4 | q5 | q6 | q7 | q8 | q9 | q10 | SUS |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| P01 | 23 | Masculino | Baja | 3 | 3 | 4 | 4 | 4 | 3 | 4 | 4 | 1 | 3 | 47.5 |
| P02 | 21 | Femenino | Media | 5 | 5 | 4 | 4 | 3 | 3 | 4 | 2 | 2 | 3 | 52.5 |
| P03 | 25 | Masculino | Alta | 3 | 3 | 3 | 3 | 3 | 2 | 1 | 2 | 4 | 4 | 50.0 |
| P04 | 23 | Femenino | Media | 4 | 3 | 3 | 3 | 3 | 3 | 2 | 2 | 3 | 3 | 52.5 |
| P05 | 20 | Femenino | Media | 3 | 4 | 3 | 2 | 4 | 2 | 3 | 2 | 3 | 4 | 55.0 |
| P06 | 22 | Masculino | Media | 3 | 1 | 4 | 2 | 4 | 3 | 4 | 2 | 4 | 3 | 70.0 |
| P07 | 20 | Femenino | Media | 4 | 2 | 4 | 1 | 5 | 2 | 5 | 1 | 5 | 1 | 90.0 |
| P08 | 20 | Femenino | Media | 4 | 2 | 3 | 3 | 4 | 4 | 4 | 2 | 4 | 2 | 65.0 |
| P09 | 21 | Masculino | Alta | 4 | 2 | 3 | 1 | 4 | 2 | 4 | 2 | 4 | 1 | 77.5 |
| P10 | 19 | Masculino | Media | 3 | 2 | 3 | 1 | 3 | 3 | 4 | 1 | 4 | 2 | 70.0 |
| P11 | 23 | Masculino | Media | 4 | 1 | 3 | 2 | 5 | 1 | 4 | 2 | 4 | 2 | 80.0 |
| P12 | 19 | Masculino | Baja | 3 | 2 | 4 | 2 | 4 | 2 | 3 | 1 | 4 | 2 | 72.5 |
| P13 | 21 | Femenino | Media | 3 | 1 | 4 | 1 | 4 | 1 | 4 | 1 | 4 | 1 | 85.0 |
| P14 | 21 | Masculino | Baja | 3 | 2 | 4 | 1 | 3 | 2 | 5 | 1 | 5 | 1 | 82.5 |
| P15 | 20 | Femenino | Media | 3 | 2 | 3 | 1 | 4 | 2 | 4 | 1 | 4 | 1 | 77.5 |

## Estadisticos descriptivos (n = 15)

| Metrica | Valor |
|---|---|
| Media | **68.5 / 100** |
| Desviacion tipica | 13.98 |
| Error estandar | 3.61 |
| IC 95% | **[60.8; 76.2]** |
| Minimo | 47.5 |
| Maximo | 90.0 |
| Calificacion adjetiva (Bangor et al., 2009) | Bueno |
| Zona de aceptabilidad | Marginal-Alta (50-70 → 68.5) |
| Umbral de usabilidad (>= 70) | NO CUMPLE (por 1.5 pts) |

## Estadisticos por item

Contribucion promedio de Brooke por item (mayor = mejor; items pares son enunciados negativos).

| Item | Media respuesta | DT respuesta | Contribucion /4 |
|---|---|---|---|
| q1 | 3.60 | 0.70 | 2.60 |
| q2 | 2.70 | 1.16 | 2.30 |
| q3 | 3.40 | 0.52 | 2.40 |
| q4 | 2.40 | 1.17 | 2.60 |
| q5 | 3.70 | 0.67 | 2.70 |
| q6 | 2.70 | 0.67 | 2.30 |
| q7 | 3.50 | 1.18 | 2.50 |
| q8 | 2.00 | 0.82 | 3.00 |
| q9 | 3.40 | 1.17 | 2.40 |
| q10 | 2.60 | 1.07 | 2.40 |

Items con peor contribucion promedio: q2, q6, q3.

Items con mejor contribucion promedio: q8, q5, q1.

## Interpretacion

La media (68.5) queda a 1.5 pts del umbral de 70 (zona marginal-alta). Con n=15 el IC95 [60.8;76.2] incluye el umbral, por lo que no se puede descartar usabilidad aceptable. Se documenta como area de mejora: los items con peor contribucion promedio se revisaran en la iteracion de diseno posterior a la defensa.

## Reproducibilidad

| Artefacto | Fuente | Script |
|---|---|---|
| Datos crudos | docs/mediciones/sus/sus-raw.csv (15 filas, Sí consistente UTF-8) | — |
| Datos por participante | P01.json..P15.json (15) | scripts/sus-analysis.py |
| Estadisticas | estadisticas-sus.json (n=15, media 68.5) | scripts/sus-analysis.py |
| Puntuacion Brooke | — | scripts/sus/brooke.py |
| Figura | figuras/fig-sus-por-participante.png (n=15) | scripts/sus-analysis.py |

Los consentimientos firmados se custodian fuera del repositorio (regla de etica); en el repo solo constan codigos P01..P15. Demografia cap.5 (edad/sexo/exp.) cruza 1:1 con sus-raw.csv.
