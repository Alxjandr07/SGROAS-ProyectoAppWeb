workspace "SGROAS - Level 3 - Components" {

    model {
        sgroas = softwareSystem "SGROAS" "Fleet management platform" {
            api = container "Spring Boot REST API" "" "Spring Boot 3.5 + Java 21" {
                auth = component "Authentication Module" "Login, signup, refresh token" "Spring Security + JWT"
                conductor = component "Drivers Module" "Driver CRUD" "Spring MVC + JPA"
                vehiculo = component "Vehicles Module" "Vehicle CRUD" "Spring MVC + JPA"
                ruta = component "Routes Module" "Route CRUD" "Spring MVC + JPA"
                asignacion = component "Assignments Module" "Route-driver-vehicle assignment CRUD" "Spring MVC + JPA"
                incidente = component "Incidents Module" "Incident CRUD" "Spring MVC + JPA"
                sp = component "Stored Procedures" "Reports and aggregations in DB" "PostgreSQL PL/pgSQL"
            }

            db = container "PostgreSQL" "" "PostgreSQL 16" {
                usuarios = component "usuarios table" ""
                conductores = component "conductores table" ""
                vehiculos = component "vehiculos table" ""
                rutas = component "rutas table" ""
                asignaciones = component "asignacion_rutas table" ""
                incidentes = component "incidentes table" ""
            }

            conductor -> conductores "CRUD"
            vehiculo -> vehiculos "CRUD"
            ruta -> rutas "CRUD"
            asignacion -> asignaciones "CRUD"
            asignacion -> conductores "FK"
            asignacion -> vehiculos "FK"
            asignacion -> rutas "FK"
            incidente -> incidentes "CRUD"
            incidente -> asignaciones "FK"
            auth -> usuarios "CRUD"
        }
    }

    views {
        component api components "Level 3 - Component Diagram" {
            include *
            autoLayout
        }
    }
}
