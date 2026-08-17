# ADR-005b: API REST con DTOs y Validacion por Records

**Estado:** Aceptado

**Contexto:** La API expone endpoints CRUD para 6 entidades. Se necesita una interfaz consistente, auto-documentada y con validacion robusta de datos de entrada.

**Decision:** Se disena una API RESTful con:
- DTOs como Java Records (inmutables, concisos).
- Validacion con Jakarta Validation (`@NotBlank`, `@Email`, `@Pattern`, etc.).
- `ConductorRequest`/`ConductorResponse` pattern para cada entidad, separando el contrato de entrada y salida.
- Paginacion via `Pageable` de Spring Data.
- Manejo global de excepciones con `@RestControllerAdvice`.
- Documentacion OpenAPI 3.0 via SpringDoc.

**Consecuencias:**
- **Positivas:** Contratos claros y desacoplados de la entidad JPA. Validacion declarativa y centralizada. Documentacion automatica de la API. Records Java reducen boilerplate.
- **Negativas:** Mapeo request â†’ entidad â†’ response requiere codigo manual en servicios. Los endpoints nuevos duplican el patron DTO.
- **Riesgos:** Cambios en la entidad requieren actualizar DTOs y mapeos manualmente.

**Opciones consideradas:**
1. DTOs con Records + Jakarta Validation (seleccionado)
2. Exponer entidades JPA directamente (descartado por acoplamiento y seguridad)
3. GraphQL (descartado por complejidad innecesaria para CRUD simple)