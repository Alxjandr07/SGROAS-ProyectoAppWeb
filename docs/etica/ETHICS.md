# Declaración Ética — SGROAS

## Fuentes

Todo el código fuente, documentación y artefactos de este proyecto son originales del equipo, salvo donde se indique lo contrario mediante atribución explícita.

## Licencias

- El código del proyecto se distribuye bajo licencia MIT (ver [LICENSE](../../LICENSE)).
- Las dependencias de terceros (Spring Boot, Angular, PostgreSQL, etc.) mantienen sus respectivas licencias open-source.

## Tratamiento de datos

- Este sistema maneja datos personales (nombres, cédulas, correos electrónicos, licencias de conducir) únicamente con fines operativos dentro de la cooperativa de transporte.
- No se comparten datos personales con terceros.
- Las contraseñas se almacenan usando BCrypt con factor de trabajo 10+.

## Anonimización

- Los participantes en las pruebas SUS fueron identificados mediante códigos (P01, P02, etc.) sin registrar nombres reales en los archivos de resultados.
- Los consentimientos informados se almacenan en [consentimientos/](consentimientos/) sin datos personales identificables.
- El registro de consentimientos con códigos y fechas está en [consentimientos/registro.md](consentimientos/registro.md).

## Consentimiento informado

- Todos los participantes de las pruebas de usabilidad (SUS) firmaron un consentimiento informado cuya plantilla está disponible en [consentimientos/plantilla.md](consentimientos/plantilla.md).
- Los participantes fueron informados de que podían retirarse en cualquier momento sin consecuencia alguna.
- Los formularios firmados se custodian fuera del repositorio público.

## Uso de IA generativa

- El proyecto utiliza IA generativa de forma transparente y verificada por el equipo; ver [ai-disclosure.md](ai-disclosure.md).
- Ningún dato, cifra o referencia del informe se fabrica con IA: todo proviene de mediciones reales versionadas en `docs/mediciones/`.
