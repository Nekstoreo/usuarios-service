# Documentación Técnica: Microservicio de Usuarios

Este documento describe la arquitectura, diseño e implementación del microservicio de usuarios (`usuarios-service`). El servicio es responsable de la gestión de usuarios, roles y la autenticación dentro del ecosistema de Plazoleta.

## Arquitectura

El microservicio sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)**. Este patrón se eligió para desacoplar la lógica de negocio (Dominio) de las dependencias externas (Infraestructura), permitiendo que el núcleo de la aplicación permanezca agnóstico al framework y a la base de datos.

El código se organiza en tres capas principales:

1.  **Dominio (Domain)**: Núcleo del negocio.
2.  **Aplicación (Application)**: Orquestación y transformación de datos.
3.  **Infraestructura (Infrastructure)**: Implementación técnica y comunicación con el exterior.

---

## 1. Capa de Dominio (Domain)

Es el núcleo del servicio y no tiene dependencias de frameworks externos (como Spring Boot) ni de librerías de persistencia. Contiene las reglas de negocio y los modelos puros.

### Modelos
Clases POJO que representan las entidades del negocio:
*   **User**: Representa la información de un usuario (nombre, apellido, documento, correo, celular, rol, credenciales).
*   **Role**: Representa los roles disponibles en el sistema (Administrador, Propietario, Empleado, Cliente).

### Puertos (Ports)
Interfaces que definen los contratos de comunicación.
*   **API (Inbound Ports)**: Definen qué funcionalidades ofrece el dominio hacia afuera.
    *   `IUserServicePort`: Operaciones relacionadas con usuarios (crear, buscar).
    *   `IAuthServicePort`: Operaciones de autenticación.
*   **SPI (Outbound Ports)**: Definen qué necesita el dominio del exterior (persistencia, seguridad).
    *   `IUserPersistencePort`: Contrato para guardar y recuperar usuarios.
    *   `IRolePersistencePort`: Contrato para obtener roles.
    *   `IPasswordEncoderPort`: Contrato para encriptar y verificar contraseñas.
    *   `IJwtPort`: Contrato para la generación de tokens.

### Casos de Uso (Use Cases)
Implementaciones de los puertos API (`IUserServicePort`, `IAuthServicePort`).
*   **UserUseCase**: Contiene la lógica para crear usuarios, validar reglas de negocio (ej. validación de edad, formato de celular) y coordinar con los puertos de persistencia y encriptación.
*   **AuthUseCase**: Maneja la lógica de login, validación de credenciales y generación de tokens JWT.

**Inyección de Dependencias**: Los casos de uso reciben sus dependencias (puertos SPI) a través del constructor, lo que facilita las pruebas unitarias y mantiene la pureza del dominio.

---

## 2. Capa de Aplicación (Application)

Actúa como intermediario entre la infraestructura (entrada) y el dominio. Su responsabilidad principal es la orquestación y la conversión de datos.

### Handlers
Componentes que manejan el flujo de las solicitudes. Implementan interfaces (`IUserHandler`, `IAuthHandler`).
*   **UserHandler / AuthHandler**: Reciben las peticiones desde los controladores, convierten los DTOs (Data Transfer Objects) a modelos de dominio usando Mappers, invocan los Casos de Uso y retornan la respuesta convertida nuevamente a DTO.
*   **Transaccionalidad**: Aunque la lógica está en el dominio, la demarcación transaccional suele coordinarse a este nivel o en la implementación de los adaptadores de persistencia.

### DTOs (Data Transfer Objects)
Objetos planos utilizados para la comunicación con el cliente API (Request/Response), evitando exponer los modelos de dominio directamente.

### Mappers
Interfaces de **MapStruct** encargadas de la conversión bidireccional entre DTOs y Modelos de Dominio. Se utilizan para evitar código repetitivo de mapeo.

---

## 3. Capa de Infraestructura (Infrastructure)

Contiene la implementación concreta de los detalles técnicos. Se divide en *Input* (entrada de datos) y *Output* (salida/persistencia).

### Input (Driving Adapters)
Puntos de entrada al microservicio.
*   **Rest Controllers** (`UserRestController`, `AuthRestController`): Exponen los endpoints HTTP (REST). Reciben las peticiones JSON, las validan (anotaciones `@Valid`) y delegan el procesamiento a los *Handlers* de la capa de aplicación.

### Output (Driven Adapters)
Implementaciones de los puertos SPI del dominio.
*   **JPA Adapters**: Implementan `IUserPersistencePort` y `IRolePersistencePort`. Utilizan `Spring Data JPA` y repositorios (`UserRepository`, `RoleRepository`) para interactuar con la base de datos MySQL.
    *   Convierten Modelos de Dominio a Entidades JPA (`UserEntity`, `RoleEntity`) antes de persistir, y viceversa al leer.
*   **Security Adapters**: Implementaciones para el manejo de criptografía y tokens.
    *   Implementación de `IPasswordEncoderPort` usando `BCryptPasswordEncoder`.
    *   Implementación de `IJwtPort` para la generación y firma de tokens JWT.

### Configuración (Configuration)
Clases de configuración de Spring Boot.
*   **BeanConfiguration**: Es crucial para la arquitectura hexagonal. Aquí se crean los *Beans* de los Casos de Uso (`UserUseCase`, `AuthUseCase`) manualmente, inyectándoles las implementaciones concretas de los puertos SPI (Adaptadores). Esto permite que Spring gestione los casos de uso sin que estos dependan de anotaciones de Spring (`@Service`, `@Autowired`) en el código fuente del dominio.
*   **SecurityConfiguration**: Configura el filtro de seguridad, define qué rutas son públicas (ej. login, registro) y cuáles requieren autenticación. Configura la gestión de sesiones como *stateless*.
*   **OpenApiConfiguration**: Configuración de Swagger/OpenAPI para la documentación de los endpoints.

---

## Decisiones de Diseño y Tecnologías

*   **Spring Boot**: Framework base por su robustez y facilidad de configuración.
*   **MapStruct**: Elegido sobre otros mapeadores por su rendimiento (genera código en tiempo de compilación) y seguridad de tipos.
*   **Lombok**: Utilizado para reducir el código boilerplate (Getters, Setters, Builders) en DTOs y Entidades.
*   **Spring Security & JWT**: Se utiliza autenticación basada en Tokens (JWT) para mantener el servicio sin estado (Stateless), estándar en microservicios.
*   **Inyección por Constructor**: Se prioriza sobre la inyección por campo (`@Autowired`) para asegurar que los componentes sean inmutables y completamente inicializados, facilitando el testing.
*   **Separación de Modelos**: La existencia de DTOs, Modelos de Dominio y Entidades JPA asegura que los cambios en la base de datos o en la API no afecten directamente a la lógica de negocio, y viceversa.
