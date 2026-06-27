# api_restaurant — Guía Completa de Implementación

> **Stack:** Java 21 · Spring Boot 3.3.x · Gradle · PostgreSQL · Spring Security · JWT · Lombok · JPA · Swagger

---

## 1. ARQUITECTURA RECOMENDADA

Arquitectura **por capas** (Layered Architecture) con separación estricta de responsabilidades:

```
Cliente (QR / App) → Controller → Service → Repository → Base de Datos
                          ↕            ↕
                        DTO         Entity
                          ↕
                       Mapper
```

### Principios aplicados
- **Controller:** solo recibe y responde HTTP, delega todo al Service
- **Service:** contiene toda la lógica de negocio, valida reglas
- **Repository:** extiende JpaRepository, solo acceso a datos
- **Entity:** mapea tablas de la base de datos con JPA
- **DTO:** objetos de transferencia para entrada y salida
- **Mapper:** convierte entre Entity ↔ DTO (manual o MapStruct)
- **Exception:** manejo centralizado con `@RestControllerAdvice`
- **Security:** filtros JWT, configuración de seguridad
- **Config:** beans de configuración general (Swagger, CORS, etc.)

---

## 2. ESTRUCTURA COMPLETA DE CARPETAS

```
api_restaurant/
├── build.gradle
├── settings.gradle
├── docker-compose.yml
├── .env
└── src/
    └── main/
        ├── resources/
        │   └── application.yml
        └── java/
            └── com/restaurante/api/
                ├── SystemRestauranteApiApplication.java
                │
                ├── config/
                │   ├── SwaggerConfig.java
                │   ├── CorsConfig.java
                │   └── AuditoriaConfig.java
                │
                ├── security/
                │   ├── JwtUtil.java
                │   ├── JwtFilter.java
                │   ├── SecurityConfig.java
                │   └── UserDetailsServiceImpl.java
                │
                ├── exception/
                │   ├── GlobalExceptionHandler.java
                │   ├── RecursoNoEncontradoException.java
                │   ├── ReglaNegocioException.java
                │   ├── PagoInvalidoException.java
                │   └── ErrorResponse.java
                │
                ├── util/
                │   ├── GeneradorQR.java
                │   └── ConstantesNegocio.java
                │
                ├── entity/
                │   ├── enums/
                │   │   ├── EstadoMesa.java
                │   │   ├── EstadoReserva.java
                │   │   ├── EstadoPedido.java
                │   │   ├── EstadoPago.java
                │   │   ├── TipoPago.java
                │   │   ├── EstadoDelivery.java
                │   │   └── RolUsuario.java
                │   ├── Usuario.java
                │   ├── Rol.java
                │   ├── Mesa.java
                │   ├── CodigoQR.java
                │   ├── Reserva.java
                │   ├── Pago.java
                │   ├── Pedido.java
                │   ├── DetallePedido.java
                │   ├── ProductoMenu.java
                │   ├── Categoria.java
                │   ├── OrdenCocina.java
                │   ├── Delivery.java
                │   ├── Factura.java
                │   └── Cancelacion.java
                │
                ├── dto/
                │   ├── request/
                │   │   ├── RegistroUsuarioRequest.java
                │   │   ├── LoginRequest.java
                │   │   ├── CrearMesaRequest.java
                │   │   ├── CrearReservaRequest.java
                │   │   ├── PagoRequest.java
                │   │   ├── CrearPedidoRequest.java
                │   │   ├── DetallePedidoRequest.java
                │   │   ├── ActualizarEstadoPedidoRequest.java
                │   │   ├── DeliveryRequest.java
                │   │   └── CancelacionRequest.java
                │   └── response/
                │       ├── AuthResponse.java
                │       ├── MesaResponse.java
                │       ├── ReservaResponse.java
                │       ├── PagoResponse.java
                │       ├── PedidoResponse.java
                │       ├── MenuResponse.java
                │       ├── FacturaResponse.java
                │       ├── DeliveryResponse.java
                │       └── ReporteResponse.java
                │
                ├── repository/
                │   ├── UsuarioRepository.java
                │   ├── MesaRepository.java
                │   ├── ReservaRepository.java
                │   ├── PagoRepository.java
                │   ├── PedidoRepository.java
                │   ├── ProductoMenuRepository.java
                │   ├── OrdenCocinaRepository.java
                │   ├── DeliveryRepository.java
                │   ├── FacturaRepository.java
                │   └── CancelacionRepository.java
                │
                ├── mapper/
                │   ├── UsuarioMapper.java
                │   ├── MesaMapper.java
                │   ├── ReservaMapper.java
                │   ├── PedidoMapper.java
                │   └── FacturaMapper.java
                │
                └── controller/
                    ├── AuthController.java
                    ├── MesaController.java
                    ├── ReservaController.java
                    ├── PagoController.java
                    ├── PedidoController.java
                    ├── CocinaController.java
                    ├── DeliveryController.java
                    ├── FacturaController.java
                    ├── CancelacionController.java
                    └── ReporteController.java
```

---

## 3. PASO A PASO — CREAR EL PROYECTO DESDE CERO

### Paso 1: Crear el proyecto con Spring Initializr

Ve a [https://start.spring.io](https://start.spring.io) y configura:

| Campo | Valor |
|---|---|
| Project | Gradle - Groovy |
| Language | Java |
| Spring Boot | 3.3.x |
| Group | pe.edu.upeu |
| Artifact | api |
| Name | api_restaurant |
| Java | 21 |

Dependencias a seleccionar:
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL Driver
- Lombok
- Validation
- Spring Boot DevTools

Descarga el ZIP, extráelo y ábrelo en VS Code.

### Paso 2: Extensiones recomendadas en VS Code

```
Extension Pack for Java
Spring Boot Extension Pack
Lombok Annotations Support
REST Client (Huachao Mao)
Docker
GitLens
```

### Paso 3: Verifica Java 21

```bash
java -version
# debe mostrar: openjdk 21.x.x
```

---

## 4. DEPENDENCIAS — build.gradle

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.4'
    id 'io.spring.dependency-management' version '1.1.6'
}

group = 'pe.edu.upeu'
version = '1.0.0'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Core
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Base de datos
    runtimeOnly 'org.postgresql:postgresql'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Swagger / OpenAPI
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'

    // QR Code (ZXing)
    implementation 'com.google.zxing:core:3.5.3'
    implementation 'com.google.zxing:javase:3.5.3'

    // Dev Tools
    developmentOnly 'org.springframework.boot:spring-boot-devtools'

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## 5. CONFIGURACIÓN — application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: api_restaurant

  datasource:
    url: jdbc:postgresql://localhost:5432/restaurante_db
    username: restaurante_user
    password: restaurante_pass
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update          # usar 'validate' en producción
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: America/Lima     # ajusta a tu zona

# JWT
jwt:
  secret: "clave-super-secreta-de-256-bits-para-produccion-cambiar"
  expiracion-ms: 86400000       # 24 horas en milisegundos

# Swagger
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    try-it-out-enabled: true

# Logging
logging:
  level:
    pe.edu.upeu: DEBUG
    org.springframework.security: INFO
```

---

## 6. DOCKER COMPOSE — PostgreSQL

### docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: restaurante_postgres
    environment:
      POSTGRES_DB: restaurante_db
      POSTGRES_USER: restaurante_user
      POSTGRES_PASSWORD: restaurante_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U restaurante_user -d restaurante_db"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

### init.sql (opcional, crea extensiones)

```sql
-- Habilita UUID nativo en PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Habilita búsqueda de texto avanzada
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
```

### Comandos Docker

```bash
# Levantar la base de datos
docker-compose up -d

# Ver logs
docker-compose logs -f postgres

# Detener
docker-compose down

# Detener y eliminar volúmenes (¡borra datos!)
docker-compose down -v

# Conectarse a PostgreSQL desde terminal
docker exec -it restaurante_postgres psql -U restaurante_user -d restaurante_db

# Verificar que la DB está activa
docker-compose ps
```

---

## 7. ENUMS PRINCIPALES

```java
// entity/enums/EstadoMesa.java
public enum EstadoMesa {
    DISPONIBLE,
    RESERVADA,
    OCUPADA,
    PENDIENTE_PAGO,
    EN_LIMPIEZA,
    BLOQUEADA
}

// entity/enums/EstadoReserva.java
public enum EstadoReserva {
    PENDIENTE_PAGO,     // creada pero sin pago confirmado
    CONFIRMADA,         // pago adelantado confirmado
    ACTIVA,             // cliente llegó, mesa ocupada
    COMPLETADA,
    CANCELADA,
    NO_ASISTIO
}

// entity/enums/EstadoPedido.java
public enum EstadoPedido {
    PENDIENTE,
    ENVIADO_COCINA,
    EN_PREPARACION,
    LISTO,
    ENTREGADO,
    CANCELADO
}

// entity/enums/EstadoPago.java
public enum EstadoPago {
    PENDIENTE,
    PROCESANDO,
    CONFIRMADO,
    RECHAZADO,
    REEMBOLSADO
}

// entity/enums/TipoPago.java
public enum TipoPago {
    ADELANTO_RESERVA,
    CONSUMO_FINAL,
    DELIVERY
}

// entity/enums/EstadoDelivery.java
public enum EstadoDelivery {
    PENDIENTE,
    ASIGNADO,
    EN_CAMINO,
    ENTREGADO,
    FALLIDO
}

// entity/enums/RolUsuario.java
public enum RolUsuario {
    ADMIN,
    MESERO,
    COCINERO,
    REPARTIDOR,
    CLIENTE
}
```

---

## 8. ENTIDADES JPA

### Usuario.java

```java
package pe.edu.upeu.api_restaurant.entity;

import pe.edu.upeu.api_restaurant.entity.enums.RolUsuario;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private String nombre;

    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime creadoEn;
}
```

### Mesa.java

```java
package pe.edu.upeu.api_restaurant.entity;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidad;

    private String ubicacion;    // ej: "Terraza", "Interior", "VIP"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoMesa estado = EstadoMesa.DISPONIBLE;

    @OneToOne(mappedBy = "mesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CodigoQR codigoQR;
}
```

### CodigoQR.java

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "codigos_qr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoQR {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @Column(nullable = false, unique = true)
    private String token;        // UUID único por mesa

    @Column(nullable = false)
    private String urlAcceso;   // URL que contiene el token

    private String imagenBase64; // imagen del QR en base64

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
```

### Reserva.java

```java
package pe.edu.upeu.api_restaurant.entity;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(nullable = false)
    private Integer numerPersonas;

    // Validación de negocio: toda reserva requiere adelanto obligatorio
    @Column(nullable = false)
    private BigDecimal montoAdelanto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.PENDIENTE_PAGO;

    private String notasEspeciales;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Pago pagoAdelanto;

    @PrePersist
    protected void alCrear() {
        creadaEn = LocalDateTime.now();
    }
}
```

### Pago.java

```java
package pe.edu.upeu.api_restaurant.entity;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPago;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    // Relación opcional: puede ser adelanto de reserva o pago de consumo
    @OneToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPago tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    // Datos simulados de la pasarela (preparado para real)
    private String referenciaPasarela;   // ID externo de la pasarela
    private String metodoPago;           // "tarjeta", "efectivo", "yape", etc.
    private String ultimos4Digitos;
    private LocalDateTime fechaPago;
    private String respuestaPasarela;    // JSON de respuesta cuando sea real

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void alCrear() {
        creadoEn = LocalDateTime.now();
    }
}
```

### Pedido.java

```java
package pe.edu.upeu.api_restaurant.entity;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesero_id")
    private Usuario mesero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(nullable = false)
    private BigDecimal total;

    private String notasAdicionales;

    @Column(nullable = false)
    private boolean esDelivery;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void alCrear() {
        creadoEn = LocalDateTime.now();
    }
}
```

### DetallePedido.java

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoMenu producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private BigDecimal subtotal;

    private String observaciones;
}
```

### ProductoMenu.java

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    private String imagenUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean disponible = true;

    private Integer tiempoPreparacionMinutos;
}
```

### Factura.java

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String numeroFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal igv;          // 18% en Perú, ajustar según país

    @Column(nullable = false)
    private BigDecimal total;

    private String rucCliente;
    private String razonSocialCliente;

    @Column(nullable = false)
    private LocalDateTime emitidaEn;

    @PrePersist
    protected void alEmitir() {
        emitidaEn = LocalDateTime.now();
    }
}
```

---

## 9. DTOs DE ENTRADA (REQUEST)

### RegistroUsuarioRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;

import pe.edu.upeu.api_restaurant.entity.enums.RolUsuario;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistroUsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;
}
```

### CrearReservaRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CrearReservaRequest {

    @NotNull(message = "El ID de mesa es obligatorio")
    private UUID mesaId;

    @NotNull(message = "La fecha/hora de inicio es obligatoria")
    @Future(message = "La reserva debe ser para una fecha futura")
    private LocalDateTime fechaHoraInicio;

    @NotNull(message = "La fecha/hora de fin es obligatoria")
    private LocalDateTime fechaHoraFin;

    @NotNull(message = "El número de personas es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 persona")
    private Integer numerPersonas;

    // Validación de negocio: adelanto obligatorio para toda reserva
    @NotNull(message = "El monto de adelanto es obligatorio")
    @DecimalMin(value = "0.01", message = "El adelanto debe ser mayor a 0")
    private BigDecimal montoAdelanto;

    private String notasEspeciales;
}
```

### PagoRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequest {

    // ID del recurso que se paga (reserva o pedido)
    private UUID reservaId;
    private UUID pedidoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipo;

    // Datos simulados de pasarela (en producción viene del formulario real)
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;      // "tarjeta", "efectivo", "yape"

    private String numeroTarjeta;   // simulado
    private String titularTarjeta;
    private String fechaVencimiento;
    private String cvv;

    private String rucCliente;      // para factura con RUC
}
```

### CrearPedidoRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CrearPedidoRequest {

    private UUID mesaId;            // null si es delivery

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<DetallePedidoRequest> detalles;

    private String notasAdicionales;
    private boolean esDelivery;
    private String direccionDelivery;
}
```

---

## 10. DTOs DE SALIDA (RESPONSE)

### AuthResponse.java

```java
package pe.edu.upeu.api_restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tipo;        // "Bearer"
    private String email;
    private String nombre;
    private String rol;
    private long expiraEn;     // milisegundos
}
```

### MesaResponse.java

```java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MesaResponse {
    private UUID id;
    private Integer numero;
    private Integer capacidad;
    private String ubicacion;
    private EstadoMesa estado;
    private String urlQR;
    private String imagenQRBase64;
}
```

### ReservaResponse.java

```java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservaResponse {
    private UUID id;
    private UUID mesaId;
    private Integer numeroMesa;
    private String nombreCliente;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Integer numerPersonas;
    private BigDecimal montoAdelanto;
    private EstadoReserva estado;
    private String estadoPago;
    private LocalDateTime creadaEn;
}
```

---

## 11. REPOSITORIOS

```java
// UsuarioRepository.java
package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

```java
// MesaRepository.java
package pe.edu.upeu.api_restaurant.repository;

import pe.edu.upeu.api_restaurant.entity.Mesa;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByEstado(EstadoMesa estado);
    Optional<Mesa> findByNumero(Integer numero);
    boolean existsByNumero(Integer numero);
}
```

```java
// ReservaRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.Reserva;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(UUID clienteId);

    List<Reserva> findByMesaIdAndEstado(UUID mesaId, EstadoReserva estado);

    // Validación de negocio: verificar conflictos de horario en la misma mesa
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.mesa.id = :mesaId
        AND r.estado IN ('CONFIRMADA', 'ACTIVA', 'PENDIENTE_PAGO')
        AND r.fechaHoraInicio < :fin
        AND r.fechaHoraFin > :inicio
    """)
    List<Reserva> buscarConflictos(
        @Param("mesaId") UUID mesaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
}
```

```java
// PedidoRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.Pedido;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByMesaIdAndEstadoNot(UUID mesaId, EstadoPedido estado);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByClienteId(UUID clienteId);
}
```

---

## 12. SERVICIOS CON LÓGICA DE NEGOCIO

### AutenticacionService.java

```java
package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.LoginRequest;
import pe.edu.upeu.api_restaurant.dto.request.RegistroUsuarioRequest;
import pe.edu.upeu.api_restaurant.dto.response.AuthResponse;
import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.entity.enums.RolUsuario;
import pe.edu.upeu.api_restaurant.exception.ReglaNegocioException;
import pe.edu.upeu.api_restaurant.repository.UsuarioRepository;
import pe.edu.upeu.api_restaurant.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de autenticación y gestión de usuarios.
 * Lógica de negocio: registro de nuevos usuarios, inicio de sesión con JWT.
 */
@Service
@RequiredArgsConstructor
public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Lógica de negocio: registro de usuario.
     * Valida que el email no exista antes de crear el usuario.
     */
    @Transactional
    public AuthResponse registrar(RegistroUsuarioRequest request) {

        // Validación de negocio: email debe ser único en el sistema
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ReglaNegocioException("El email ya está registrado en el sistema");
        }

        // Construcción del usuario con contraseña encriptada
        Usuario nuevoUsuario = Usuario.builder()
            .nombre(request.getNombre())
            .email(request.getEmail())
            .contrasena(passwordEncoder.encode(request.getContrasena()))
            .telefono(request.getTelefono())
            .rol(request.getRol())
            .activo(true)
            .build();

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // Generación del token JWT después del registro exitoso
        String token = jwtUtil.generarToken(usuarioGuardado.getEmail(), usuarioGuardado.getRol().name());

        return AuthResponse.builder()
            .token(token)
            .tipo("Bearer")
            .email(usuarioGuardado.getEmail())
            .nombre(usuarioGuardado.getNombre())
            .rol(usuarioGuardado.getRol().name())
            .expiraEn(jwtUtil.obtenerExpiracion())
            .build();
    }

    /**
     * Lógica de negocio: autenticación de usuario.
     * Verifica credenciales y devuelve JWT si son válidas.
     */
    public AuthResponse iniciarSesion(LoginRequest request) {

        // Validación de negocio: el usuario debe existir y estar activo
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ReglaNegocioException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new ReglaNegocioException("La cuenta está desactivada. Contacte al administrador");
        }

        // Validación de negocio: contraseña debe coincidir con la almacenada
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new ReglaNegocioException("Credenciales inválidas");
        }

        // Generación del token JWT para sesión activa
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name());

        return AuthResponse.builder()
            .token(token)
            .tipo("Bearer")
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .rol(usuario.getRol().name())
            .expiraEn(jwtUtil.obtenerExpiracion())
            .build();
    }
}
```

### MesaService.java

```java
package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.CrearMesaRequest;
import pe.edu.upeu.api_restaurant.dto.response.MesaResponse;
import pe.edu.upeu.api_restaurant.entity.CodigoQR;
import pe.edu.upeu.api_restaurant.entity.Mesa;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import pe.edu.upeu.api_restaurant.exception.RecursoNoEncontradoException;
import pe.edu.upeu.api_restaurant.exception.ReglaNegocioException;
import pe.edu.upeu.api_restaurant.repository.MesaRepository;
import pe.edu.upeu.api_restaurant.util.GeneradorQR;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de mesas.
 * Lógica de negocio: creación, estado y control de disponibilidad de mesas.
 */
@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final GeneradorQR generadorQR;

    private static final String BASE_URL_QR = "http://localhost:8080/api/mesas/qr/";

    /**
     * Lógica de negocio: creación de mesa con QR único.
     * Cada mesa tiene un código QR que los clientes escanean para hacer pedidos.
     */
    @Transactional
    public MesaResponse crearMesa(CrearMesaRequest request) {

        // Validación de negocio: no puede haber dos mesas con el mismo número
        if (mesaRepository.existsByNumero(request.getNumero())) {
            throw new ReglaNegocioException("Ya existe una mesa con el número " + request.getNumero());
        }

        // Generación del token único para el QR de esta mesa
        String tokenQR = UUID.randomUUID().toString();
        String urlQR = BASE_URL_QR + tokenQR;

        // Creación del QR como imagen en base64
        String imagenQRBase64 = generadorQR.generarQRBase64(urlQR);

        // Construcción de la entidad Mesa con su QR asociado
        Mesa mesa = Mesa.builder()
            .numero(request.getNumero())
            .capacidad(request.getCapacidad())
            .ubicacion(request.getUbicacion())
            .estado(EstadoMesa.DISPONIBLE)
            .build();

        CodigoQR codigoQR = CodigoQR.builder()
            .mesa(mesa)
            .token(tokenQR)
            .urlAcceso(urlQR)
            .imagenBase64(imagenQRBase64)
            .activo(true)
            .build();

        mesa.setCodigoQR(codigoQR);
        Mesa mesaGuardada = mesaRepository.save(mesa);

        return mapearAResponse(mesaGuardada);
    }

    /**
     * Lógica de negocio: consulta de mesa por token QR.
     * El cliente escanea el QR y obtiene la información de la mesa.
     */
    public MesaResponse obtenerMesaPorTokenQR(String token) {
        Mesa mesa = mesaRepository.findAll().stream()
            .filter(m -> m.getCodigoQR() != null
                && m.getCodigoQR().getToken().equals(token)
                && m.getCodigoQR().isActivo())
            .findFirst()
            .orElseThrow(() -> new RecursoNoEncontradoException("QR inválido o expirado"));

        return mapearAResponse(mesa);
    }

    /**
     * Lógica de negocio: cambio de estado de mesa.
     * Controla el ciclo de vida: DISPONIBLE → RESERVADA → OCUPADA → PENDIENTE_PAGO → DISPONIBLE
     */
    @Transactional
    public void cambiarEstadoMesa(UUID mesaId, EstadoMesa nuevoEstado) {
        Mesa mesa = mesaRepository.findById(mesaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada con ID: " + mesaId));

        // Validación de negocio: restricciones de transición de estado
        validarTransicionEstado(mesa.getEstado(), nuevoEstado);

        mesa.setEstado(nuevoEstado);
        mesaRepository.save(mesa);
    }

    /**
     * Lógica de negocio: validación de transiciones de estado permitidas.
     * Evita que una mesa pase a un estado inválido (ej: OCUPADA → DISPONIBLE sin pagar).
     */
    private void validarTransicionEstado(EstadoMesa estadoActual, EstadoMesa nuevoEstado) {
        boolean transicionValida = switch (estadoActual) {
            case DISPONIBLE -> nuevoEstado == EstadoMesa.RESERVADA || nuevoEstado == EstadoMesa.OCUPADA;
            case RESERVADA  -> nuevoEstado == EstadoMesa.ACTIVA || nuevoEstado == EstadoMesa.DISPONIBLE;
            case OCUPADA    -> nuevoEstado == EstadoMesa.PENDIENTE_PAGO;
            case PENDIENTE_PAGO -> nuevoEstado == EstadoMesa.EN_LIMPIEZA;
            case EN_LIMPIEZA    -> nuevoEstado == EstadoMesa.DISPONIBLE;
            case BLOQUEADA      -> nuevoEstado == EstadoMesa.DISPONIBLE;
        };

        if (!transicionValida) {
            throw new ReglaNegocioException(
                String.format("No se puede cambiar el estado de mesa de %s a %s", estadoActual, nuevoEstado)
            );
        }
    }

    public List<MesaResponse> listarMesas() {
        return mesaRepository.findAll().stream()
            .map(this::mapearAResponse)
            .collect(Collectors.toList());
    }

    private MesaResponse mapearAResponse(Mesa mesa) {
        return MesaResponse.builder()
            .id(mesa.getId())
            .numero(mesa.getNumero())
            .capacidad(mesa.getCapacidad())
            .ubicacion(mesa.getUbicacion())
            .estado(mesa.getEstado())
            .urlQR(mesa.getCodigoQR() != null ? mesa.getCodigoQR().getUrlAcceso() : null)
            .imagenQRBase64(mesa.getCodigoQR() != null ? mesa.getCodigoQR().getImagenBase64() : null)
            .build();
    }
}
```

### ReservaService.java

```java
package pe.edu.upeu.api_restaurant.service;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.CrearReservaRequest;
import pe.edu.upeu.api_restaurant.dto.response.ReservaResponse;
import pe.edu.upeu.api_restaurant.entity.*;
import pe.edu.upeu.api_restaurant.entity.enums.*;
import pe.edu.upeu.api_restaurant.exception.*;
import pe.edu.upeu.api_restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de reservas.
 * Lógica de negocio crítica: control de disponibilidad de mesas,
 * adelanto obligatorio y bloqueo de mesas para evitar doble reserva.
 */
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Flujo crítico: creación de reserva.
     * 1. Verifica que la mesa existe y está disponible
     * 2. Verifica conflictos de horario
     * 3. Crea la reserva en estado PENDIENTE_PAGO
     * 4. El pago adelantado se procesa en PagoService y activa la reserva
     */
    @Transactional
    public ReservaResponse crearReserva(UUID clienteId, CrearReservaRequest request) {

        // Validación de negocio: la mesa debe existir
        Mesa mesa = mesaRepository.findById(request.getMesaId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada"));

        // Validación de negocio: la mesa debe estar disponible para reservar
        if (mesa.getEstado() != EstadoMesa.DISPONIBLE) {
            throw new ReglaNegocioException(
                "La mesa " + mesa.getNumero() + " no está disponible. Estado actual: " + mesa.getEstado()
            );
        }

        // Validación de negocio: no debe haber conflictos de horario en la misma mesa
        List<Reserva> conflictos = reservaRepository.buscarConflictos(
            request.getMesaId(),
            request.getFechaHoraInicio(),
            request.getFechaHoraFin()
        );

        if (!conflictos.isEmpty()) {
            throw new ReglaNegocioException(
                "La mesa ya tiene una reserva para ese horario. Elija otro horario o mesa."
            );
        }

        // Validación de negocio: la fecha de fin debe ser posterior a la de inicio
        if (!request.getFechaHoraFin().isAfter(request.getFechaHoraInicio())) {
            throw new ReglaNegocioException("La fecha de fin debe ser posterior a la de inicio");
        }

        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        // Creación de la reserva en estado pendiente (requiere pago para activarse)
        Reserva reserva = Reserva.builder()
            .mesa(mesa)
            .cliente(cliente)
            .fechaHoraInicio(request.getFechaHoraInicio())
            .fechaHoraFin(request.getFechaHoraFin())
            .numerPersonas(request.getNumerPersonas())
            .montoAdelanto(request.getMontoAdelanto())
            .estado(EstadoReserva.PENDIENTE_PAGO)
            .notasEspeciales(request.getNotasEspeciales())
            .build();

        // Bloqueo preventivo de la mesa para evitar doble reserva durante el proceso de pago
        mesa.setEstado(EstadoMesa.RESERVADA);
        mesaRepository.save(mesa);

        Reserva reservaGuardada = reservaRepository.save(reserva);
        return mapearAResponse(reservaGuardada);
    }

    /**
     * Lógica de negocio: confirmación de reserva tras pago exitoso.
     * Este método es invocado por PagoService cuando el adelanto es confirmado.
     */
    @Transactional
    public void confirmarReservaTraspadoPago(UUID reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        // Validación de negocio: solo se confirma si estaba pendiente de pago
        if (reserva.getEstado() != EstadoReserva.PENDIENTE_PAGO) {
            throw new ReglaNegocioException("La reserva no está en estado de espera de pago");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);
    }

    /**
     * Lógica de negocio: liberación de mesa si el pago no se confirma.
     */
    @Transactional
    public void cancelarReservaPorFalloPago(UUID reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        reserva.setEstado(EstadoReserva.CANCELADA);

        // Liberar la mesa para que otros puedan reservarla
        Mesa mesa = reserva.getMesa();
        mesa.setEstado(EstadoMesa.DISPONIBLE);

        mesaRepository.save(mesa);
        reservaRepository.save(reserva);
    }

    public List<ReservaResponse> listarReservasPorCliente(UUID clienteId) {
        return reservaRepository.findByClienteId(clienteId).stream()
            .map(this::mapearAResponse)
            .collect(Collectors.toList());
    }

    private ReservaResponse mapearAResponse(Reserva r) {
        return ReservaResponse.builder()
            .id(r.getId())
            .mesaId(r.getMesa().getId())
            .numeroMesa(r.getMesa().getNumero())
            .nombreCliente(r.getCliente().getNombre())
            .fechaHoraInicio(r.getFechaHoraInicio())
            .fechaHoraFin(r.getFechaHoraFin())
            .numerPersonas(r.getNumerPersonas())
            .montoAdelanto(r.getMontoAdelanto())
            .estado(r.getEstado())
            .creadaEn(r.getCreadaEn())
            .build();
    }
}
```

### PagoService.java

```java
package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.request.PagoRequest;
import pe.edu.upeu.api_restaurant.dto.response.PagoResponse;
import pe.edu.upeu.api_restaurant.entity.*;
import pe.edu.upeu.api_restaurant.entity.enums.*;
import pe.edu.upeu.api_restaurant.exception.*;
import pe.edu.upeu.api_restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de pagos.
 * Lógica de negocio crítica: distingue entre adelanto de reserva y consumo final.
 * Simula una pasarela de pago con estructura preparada para integración real.
 * La pasarela real reemplazaría el método simularProcesoPasarela().
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaService reservaService;

    /**
     * Flujo crítico: procesamiento de pago.
     * 1. Valida el tipo de pago (adelanto vs consumo final)
     * 2. Simula la pasarela (en producción: llamada a API externa)
     * 3. Si es adelanto: activa la reserva
     * 4. Si es consumo final: cierra el pedido y genera factura
     */
    @Transactional
    public PagoResponse procesarPago(UUID clienteId, PagoRequest request) {

        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        // Construcción del pago según el tipo
        Pago pago = construirPago(cliente, request);

        // Simulación de pasarela — en producción: llamar a Stripe, Culqi, etc.
        boolean pagoExitoso = simularProcesoPasarela(request);

        if (pagoExitoso) {
            // Pago aprobado: actualizar estado y ejecutar lógica de negocio post-pago
            pago.setEstado(EstadoPago.CONFIRMADO);
            pago.setFechaPago(LocalDateTime.now());
            pago.setReferenciaPasarela(UUID.randomUUID().toString());

            pagoRepository.save(pago);

            // Lógica post-pago según el tipo
            ejecutarAccionPostPago(pago, request);

            log.info("Pago confirmado. Referencia: {}", pago.getReferenciaPasarela());
        } else {
            // Pago rechazado: cancelar reserva si es adelanto y liberar mesa
            pago.setEstado(EstadoPago.RECHAZADO);
            pagoRepository.save(pago);

            if (request.getTipo() == TipoPago.ADELANTO_RESERVA && request.getReservaId() != null) {
                reservaService.cancelarReservaPorFalloPago(request.getReservaId());
            }

            throw new PagoInvalidoException("El pago fue rechazado. Verifique los datos e intente nuevamente.");
        }

        return PagoResponse.builder()
            .id(pago.getId())
            .estado(pago.getEstado())
            .referencia(pago.getReferenciaPasarela())
            .monto(pago.getMonto())
            .tipo(pago.getTipo())
            .fechaPago(pago.getFechaPago())
            .build();
    }

    /**
     * Simulación de pasarela de pago.
     * ARQUITECTURA PREPARADA PARA PRODUCCIÓN:
     * En producción, este método haría una llamada HTTP a la API de la pasarela real
     * y devolvería true/false según la respuesta.
     * Ejemplo con Stripe: stripeClient.charges().create(params)
     */
    private boolean simularProcesoPasarela(PagoRequest request) {
        // Simulación: tarjetas que terminen en 0000 son rechazadas (para pruebas)
        if (request.getNumeroTarjeta() != null && request.getNumeroTarjeta().endsWith("0000")) {
            return false;
        }
        // En todos los demás casos, aprobamos el pago simulado
        return true;
    }

    /**
     * Lógica de negocio: acciones a ejecutar después de un pago exitoso.
     * Distingue entre adelanto de reserva y consumo final.
     */
    private void ejecutarAccionPostPago(Pago pago, PagoRequest request) {
        switch (request.getTipo()) {
            case ADELANTO_RESERVA -> {
                // Activar la reserva ya que el adelanto fue confirmado
                if (request.getReservaId() != null) {
                    reservaService.confirmarReservaTraspadoPago(request.getReservaId());
                }
            }
            case CONSUMO_FINAL -> {
                // Marcar el pedido como pagado (la factura se genera en FacturaService)
                if (request.getPedidoId() != null) {
                    Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
                    pago.setPedido(pedido);
                    pagoRepository.save(pago);
                }
            }
            case DELIVERY -> log.info("Pago de delivery procesado para pedido: {}", request.getPedidoId());
        }
    }

    private Pago construirPago(Usuario cliente, PagoRequest request) {
        return Pago.builder()
            .cliente(cliente)
            .monto(request.getMonto())
            .tipo(request.getTipo())
            .estado(EstadoPago.PROCESANDO)
            .metodoPago(request.getMetodoPago())
            .ultimos4Digitos(
                request.getNumeroTarjeta() != null && request.getNumeroTarjeta().length() >= 4
                    ? request.getNumeroTarjeta().substring(request.getNumeroTarjeta().length() - 4)
                    : null
            )
            .build();
    }
}
```

### PedidoService.java

```java
package pe.edu.upeu.api_restaurant.service;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.CrearPedidoRequest;
import pe.edu.upeu.api_restaurant.dto.response.PedidoResponse;
import pe.edu.upeu.api_restaurant.entity.*;
import pe.edu.upeu.api_restaurant.entity.enums.*;
import pe.edu.upeu.api_restaurant.exception.*;
import pe.edu.upeu.api_restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de pedidos.
 * Lógica de negocio: pedidos por QR, por mesero y delivery.
 * Un pedido solo puede crearse si la mesa está activa (con reserva o pago).
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoMenuRepository productoMenuRepository;

    /**
     * Lógica de negocio: creación de pedido.
     * Validación crítica: la mesa debe estar OCUPADA o tener reserva activa.
     * El total se calcula automáticamente desde los productos.
     */
    @Transactional
    public PedidoResponse crearPedido(UUID clienteId, CrearPedidoRequest request) {

        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        Mesa mesa = null;
        if (request.getMesaId() != null) {
            mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada"));

            // Validación de negocio: la mesa debe estar ocupada o con reserva activa
            if (mesa.getEstado() != EstadoMesa.OCUPADA && mesa.getEstado() != EstadoMesa.RESERVADA) {
                throw new ReglaNegocioException(
                    "No se pueden realizar pedidos en una mesa que no está activa. Estado: " + mesa.getEstado()
                );
            }
        }

        // Construcción de los detalles y cálculo del total
        List<DetallePedido> detalles = construirDetalles(request);
        BigDecimal total = detalles.stream()
            .map(DetallePedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = Pedido.builder()
            .mesa(mesa)
            .cliente(cliente)
            .estado(EstadoPedido.PENDIENTE)
            .total(total)
            .notasAdicionales(request.getNotasAdicionales())
            .esDelivery(request.isEsDelivery())
            .detalles(detalles)
            .build();

        // Asociar cada detalle al pedido
        detalles.forEach(d -> d.setPedido(pedido));

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return mapearAResponse(pedidoGuardado);
    }

    /**
     * Lógica de negocio: envío del pedido a cocina.
     * Cambia el estado a ENVIADO_COCINA para que el módulo de cocina lo vea.
     */
    @Transactional
    public void enviarPedidoACocina(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        // Validación de negocio: solo se puede enviar a cocina si está pendiente
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ReglaNegocioException("El pedido ya fue enviado a cocina o está en otro estado");
        }

        pedido.setEstado(EstadoPedido.ENVIADO_COCINA);
        pedidoRepository.save(pedido);
    }

    /**
     * Lógica de negocio: actualización del estado del pedido por cocina.
     * Los estados permitidos desde cocina son: EN_PREPARACION y LISTO.
     */
    @Transactional
    public void actualizarEstadoPedido(UUID pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }

    private List<DetallePedido> construirDetalles(CrearPedidoRequest request) {
        return request.getDetalles().stream().map(detalleReq -> {
            ProductoMenu producto = productoMenuRepository.findById(detalleReq.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + detalleReq.getProductoId()));

            if (!producto.isDisponible()) {
                throw new ReglaNegocioException("El producto no está disponible: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalleReq.getCantidad()));

            return DetallePedido.builder()
                .producto(producto)
                .cantidad(detalleReq.getCantidad())
                .precioUnitario(producto.getPrecio())
                .subtotal(subtotal)
                .observaciones(detalleReq.getObservaciones())
                .build();
        }).collect(Collectors.toList());
    }

    private PedidoResponse mapearAResponse(Pedido p) {
        return PedidoResponse.builder()
            .id(p.getId())
            .estado(p.getEstado())
            .total(p.getTotal())
            .esDelivery(p.isEsDelivery())
            .creadoEn(p.getCreadoEn())
            .build();
    }
}
```

---

## 13. CONTROLADORES REST

### AuthController.java

```java
package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.LoginRequest;
import pe.edu.upeu.api_restaurant.dto.request.RegistroUsuarioRequest;
import pe.edu.upeu.api_restaurant.dto.response.AuthResponse;
import pe.edu.upeu.api_restaurant.service.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {

    private final AutenticacionService autenticacionService;

    @PostMapping("/registrar")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autenticacionService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener JWT")
    public ResponseEntity<AuthResponse> iniciarSesion(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticacionService.iniciarSesion(request));
    }
}
```

### MesaController.java

```java
package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.request.CrearMesaRequest;
import pe.edu.upeu.api_restaurant.dto.response.MesaResponse;
import pe.edu.upeu.api_restaurant.service.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Gestión de mesas y códigos QR")
@SecurityRequirement(name = "bearerAuth")
public class MesaController {

    private final MesaService mesaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva mesa con QR")
    public ResponseEntity<MesaResponse> crearMesa(@Valid @RequestBody CrearMesaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.crearMesa(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    @Operation(summary = "Listar todas las mesas")
    public ResponseEntity<List<MesaResponse>> listarMesas() {
        return ResponseEntity.ok(mesaService.listarMesas());
    }

    // Endpoint público: el cliente escanea el QR y llega aquí sin autenticación
    @GetMapping("/qr/{token}")
    @Operation(summary = "Obtener información de mesa por QR (público)")
    public ResponseEntity<MesaResponse> obtenerMesaPorQR(@PathVariable String token) {
        return ResponseEntity.ok(mesaService.obtenerMesaPorTokenQR(token));
    }
}
```

### ReservaController.java

```java
package pe.edu.upeu.api_restaurant.controller;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.CrearReservaRequest;
import pe.edu.upeu.api_restaurant.dto.response.ReservaResponse;
import pe.edu.upeu.api_restaurant.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas de mesas")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Crear una reserva (requiere adelanto)")
    public ResponseEntity<ReservaResponse> crearReserva(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CrearReservaRequest request) {
        // El clienteId se obtiene del token JWT, no del body
        UUID clienteId = obtenerClienteIdDesdeToken(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reservaService.crearReserva(clienteId, request));
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Listar reservas del cliente autenticado")
    public ResponseEntity<List<ReservaResponse>> misReservas(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID clienteId = obtenerClienteIdDesdeToken(userDetails);
        return ResponseEntity.ok(reservaService.listarReservasPorCliente(clienteId));
    }

    // Método auxiliar: extrae el ID del usuario desde el token JWT
    private Long obtenerClienteIdDesdeToken(UserDetails userDetails) {
        // En la implementación real, el UserDetails contiene el ID del usuario
        // Se puede obtener casteando a la clase custom UserDetailsImpl
        return 1L; // placeholder — implementar con UserDetailsImpl
    }
}
```

### PagoController.java

```java
package pe.edu.upeu.api_restaurant.controller;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.PagoRequest;
import pe.edu.upeu.api_restaurant.dto.response.PagoResponse;
import pe.edu.upeu.api_restaurant.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Procesamiento de pagos (simulado - preparado para pasarela real)")
public class PagoController {

    private final PagoService pagoService;

    /**
     * Formulario de pago simulado.
     * En producción, este endpoint recibiría el token de la pasarela real (ej: Stripe token).
     */
    @PostMapping("/procesar")
    @Operation(summary = "Procesar pago (adelanto o consumo final)")
    public ResponseEntity<PagoResponse> procesarPago(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PagoRequest request) {
        UUID clienteId = UUID.randomUUID(); // placeholder — reemplazar con UUID del JWT
        return ResponseEntity.ok(pagoService.procesarPago(clienteId, request));
    }
}
```

---

## 14. SEGURIDAD — JWT + SPRING SECURITY

### JwtUtil.java

```java
package pe.edu.upeu.api_restaurant.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secreto;

    @Value("${jwt.expiracion-ms}")
    private long expiracionMs;

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secreto.getBytes());
    }

    public String generarToken(String email, String rol) {
        return Jwts.builder()
            .subject(email)
            .claim("rol", rol)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiracionMs))
            .signWith(obtenerClave())
            .compact();
    }

    public String extraerEmail(String token) {
        return parsearToken(token).getPayload().getSubject();
    }

    public String extraerRol(String token) {
        return parsearToken(token).getPayload().get("rol", String.class);
    }

    public boolean esTokenValido(String token) {
        try {
            parsearToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long obtenerExpiracion() {
        return expiracionMs;
    }

    private Jws<Claims> parsearToken(String token) {
        return Jwts.parser()
            .verifyWith(obtenerClave())
            .build()
            .parseSignedClaims(token);
    }
}
```

### JwtFilter.java

```java
package pe.edu.upeu.api_restaurant.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Extracción del header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extracción del token (sin el prefijo "Bearer ")
        String token = authHeader.substring(7);

        if (!jwtUtil.esTokenValido(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUtil.extraerEmail(token);

        // Solo autenticar si no hay una sesión activa en el contexto
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
```

### SecurityConfig.java

```java
package pe.edu.upeu.api_restaurant.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (no requieren JWT)
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/mesas/qr/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder codificadorContrasena() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager gestorAutenticacion(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### UserDetailsServiceImpl.java

```java
package pe.edu.upeu.api_restaurant.security;

import pe.edu.upeu.api_restaurant.entity.Usuario;
import pe.edu.upeu.api_restaurant.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Mapeo del rol del sistema al rol de Spring Security
        return new org.springframework.security.core.userdetails.User(
            usuario.getEmail(),
            usuario.getContrasena(),
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
}
```

---

## 15. MANEJO GLOBAL DE EXCEPCIONES

### GlobalExceptionHandler.java

```java
package pe.edu.upeu.api_restaurant.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(construirError(ex.getMessage(), 404));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(ReglaNegocioException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(construirError(ex.getMessage(), 422));
    }

    @ExceptionHandler(PagoInvalidoException.class)
    public ResponseEntity<ErrorResponse> manejarPagoInvalido(PagoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(construirError(ex.getMessage(), 402));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(construirError(errores, 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(construirError("Error interno del servidor", 500));
    }

    private ErrorResponse construirError(String mensaje, int codigo) {
        return new ErrorResponse(codigo, mensaje, LocalDateTime.now());
    }
}
```

### Clases de excepción

```java
// RecursoNoEncontradoException.java
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}

// ReglaNegocioException.java
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) { super(mensaje); }
}

// PagoInvalidoException.java
public class PagoInvalidoException extends RuntimeException {
    public PagoInvalidoException(String mensaje) { super(mensaje); }
}

// ErrorResponse.java
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int codigo;
    private String mensaje;
    private LocalDateTime timestamp;
}
```

---

## 16. UTILIDADES

### GeneradorQR.java

```java
package pe.edu.upeu.api_restaurant.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

@Component
public class GeneradorQR {

    private static final int ANCHO_QR = 300;
    private static final int ALTO_QR = 300;

    /**
     * Genera un QR en formato base64 a partir de una URL.
     * El base64 resultante puede mostrarse directamente en un <img src="data:image/png;base64,...">
     */
    public String generarQRBase64(String contenido) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix matrix = qrWriter.encode(contenido, BarcodeFormat.QR_CODE, ANCHO_QR, ALTO_QR);

            BufferedImage imagen = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagen, "PNG", baos);

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el código QR: " + e.getMessage());
        }
    }
}
```

### ConstantesNegocio.java

```java
package pe.edu.upeu.api_restaurant.util;

public final class ConstantesNegocio {

    private ConstantesNegocio() {}

    // Porcentaje de penalidad por cancelación de reserva
    public static final double PENALIDAD_CANCELACION = 0.30;  // 30%

    // Horas mínimas para cancelar sin penalidad
    public static final int HORAS_CANCELACION_GRATUITA = 24;

    // Porcentaje de IGV/IVA
    public static final double PORCENTAJE_IGV = 0.18;  // 18%

    // Prefijo para número de factura
    public static final String PREFIJO_FACTURA = "FAC-";

    // Tiempo máximo de reserva en horas
    public static final int DURACION_MAX_RESERVA_HORAS = 4;
}
```

---

## 17. CONFIGURACIÓN SWAGGER

### SwaggerConfig.java

```java
package pe.edu.upeu.api_restaurant.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "System Restaurante API",
        version = "1.0.0",
        description = "API REST para gestión de restaurante con QR, pedidos, reservas y pagos"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class SwaggerConfig {
    // La configuración se hace con anotaciones
}
```

---

## 18. ENDPOINTS REST — RESUMEN COMPLETO

### Autenticación
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/auth/registrar` | Público | Registro de usuario |
| POST | `/api/auth/login` | Público | Login y obtención de JWT |

### Mesas
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/mesas` | ADMIN | Crear mesa con QR |
| GET | `/api/mesas` | ADMIN, MESERO | Listar todas las mesas |
| GET | `/api/mesas/{id}` | ADMIN, MESERO | Detalle de una mesa |
| GET | `/api/mesas/qr/{token}` | Público | Info de mesa por QR |
| PATCH | `/api/mesas/{id}/estado` | ADMIN, MESERO | Cambiar estado de mesa |

### Reservas
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/reservas` | CLIENTE | Crear reserva (requiere adelanto) |
| GET | `/api/reservas/mis-reservas` | CLIENTE | Mis reservas |
| GET | `/api/reservas/{id}` | ADMIN, MESERO | Ver reserva |
| DELETE | `/api/reservas/{id}` | CLIENTE, ADMIN | Cancelar reserva |

### Pagos
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/pagos/procesar` | AUTH | Procesar pago (adelanto o final) |
| GET | `/api/pagos/mis-pagos` | CLIENTE | Historial de pagos |

### Pedidos
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/pedidos` | AUTH | Crear pedido |
| GET | `/api/pedidos/{id}` | AUTH | Ver pedido |
| POST | `/api/pedidos/{id}/enviar-cocina` | MESERO | Enviar a cocina |
| PATCH | `/api/pedidos/{id}/estado` | COCINERO | Actualizar estado |
| POST | `/api/pedidos/{id}/solicitar-mesero` | CLIENTE | Solicitar mesero |

### Cocina
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| GET | `/api/cocina/pendientes` | COCINERO | Pedidos pendientes |
| PATCH | `/api/cocina/pedidos/{id}/estado` | COCINERO | Actualizar estado |

### Delivery
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/delivery` | ADMIN | Crear pedido delivery |
| PATCH | `/api/delivery/{id}/asignar` | ADMIN | Asignar repartidor |
| PATCH | `/api/delivery/{id}/estado` | REPARTIDOR | Actualizar estado |

### Facturación
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/facturas/generar/{pedidoId}` | MESERO | Generar factura |
| GET | `/api/facturas/{id}` | AUTH | Ver factura |

### Cancelaciones
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/cancelaciones/reserva/{id}` | CLIENT, ADMIN | Cancelar reserva |
| POST | `/api/cancelaciones/pedido/{id}` | MESERO, ADMIN | Cancelar pedido |

### Reportes
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| GET | `/api/reportes/ventas` | ADMIN | Reporte de ventas |
| GET | `/api/reportes/mesas` | ADMIN | Ocupación de mesas |
| GET | `/api/reportes/productos` | ADMIN | Productos más vendidos |

---

## 19. FLUJO DETALLADO DE PROCESOS

### Flujo 1: Reserva con adelanto

```
Cliente → POST /reservas
    ↓ ReservaService.crearReserva()
    ↓ Valida mesa disponible
    ↓ Verifica conflictos de horario
    ↓ Crea Reserva (estado: PENDIENTE_PAGO)
    ↓ Mesa pasa a RESERVADA (bloqueo preventivo)
    ↓ Responde con reservaId y monto a pagar
    
Cliente → POST /pagos/procesar (tipo: ADELANTO_RESERVA, reservaId)
    ↓ PagoService.procesarPago()
    ↓ simularProcesoPasarela() → true/false
    
    Si exitoso:
        ↓ Pago.estado = CONFIRMADO
        ↓ ReservaService.confirmarReservaTraspadoPago()
        ↓ Reserva.estado = CONFIRMADA
        ↓ Mesa permanece RESERVADA
        
    Si rechazado:
        ↓ Pago.estado = RECHAZADO
        ↓ ReservaService.cancelarReservaPorFalloPago()
        ↓ Reserva.estado = CANCELADA
        ↓ Mesa vuelve a DISPONIBLE
```

### Flujo 2: Pedido por QR

```
Cliente escanea QR → GET /mesas/qr/{token}
    ↓ Obtiene info de la mesa (id, numero, estado)
    
Cliente → GET /menu (sin auth)
    ↓ Ve productos disponibles con precios

Cliente (autenticado) → POST /pedidos
    ↓ PedidoService.crearPedido()
    ↓ Valida que la mesa esté OCUPADA o RESERVADA
    ↓ Calcula total automáticamente
    ↓ Crea Pedido (estado: PENDIENTE)
    
Mesero → POST /pedidos/{id}/enviar-cocina
    ↓ Pedido.estado = ENVIADO_COCINA

Cocinero → PATCH /cocina/pedidos/{id}/estado (EN_PREPARACION)
    ↓ Pedido.estado = EN_PREPARACION
    
Cocinero → PATCH /cocina/pedidos/{id}/estado (LISTO)
    ↓ Pedido.estado = LISTO
    
Mesero entrega → PATCH /pedidos/{id}/estado (ENTREGADO)
    ↓ Pedido.estado = ENTREGADO
```

### Flujo 3: Pago final y cierre de mesa

```
Mesero → POST /facturas/generar/{pedidoId}
    ↓ FacturaService.generarFactura()
    ↓ Calcula IGV (18%)
    ↓ Genera número de factura (FAC-00001)
    ↓ Crea Factura en BD

Cliente → POST /pagos/procesar (tipo: CONSUMO_FINAL, pedidoId)
    ↓ PagoService.procesarPago()
    ↓ Pago confirmado
    
Mesero → PATCH /mesas/{id}/estado (PENDIENTE_PAGO)
    ↓ MesaService.cambiarEstadoMesa()
    
Limpieza → PATCH /mesas/{id}/estado (EN_LIMPIEZA)
    ↓ Mesa.estado = EN_LIMPIEZA

Admin → PATCH /mesas/{id}/estado (DISPONIBLE)
    ↓ Mesa.estado = DISPONIBLE (ciclo completo)
```

### Flujo 4: Cancelación con penalidad

```
Cliente → DELETE /cancelaciones/reserva/{id}
    ↓ CancelacionService.cancelarReserva()
    ↓ Verifica cuántas horas faltan para la reserva
    
    Si faltan más de 24 horas:
        ↓ Reembolso completo del adelanto
        ↓ Reserva.estado = CANCELADA
        ↓ Mesa.estado = DISPONIBLE
        
    Si faltan menos de 24 horas:
        ↓ Penalidad del 30% (ConstantesNegocio.PENALIDAD_CANCELACION)
        ↓ Reembolso parcial del 70%
        ↓ Reserva.estado = CANCELADA
        ↓ Mesa.estado = DISPONIBLE
        ↓ Se genera comprobante de cancelación
```

---

## 20. CLASE PRINCIPAL

```java
package pe.edu.upeu.api_restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SystemRestauranteApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemRestauranteApiApplication.class, args);
    }
}
```

---

## 21. COMANDOS PARA INICIAR

```bash
# 1. Levantar PostgreSQL con Docker
docker-compose up -d

# 2. Verificar que la DB está activa
docker-compose ps

# 3. Compilar el proyecto
./gradlew build

# 4. Ejecutar la aplicación
./gradlew bootRun

# 5. Con DevTools activo, también puedes:
./gradlew bootRun --continuous

# 6. Ver documentación Swagger
# Abrir en navegador: http://localhost:8080/api/swagger-ui.html

# 7. Ejecutar tests
./gradlew test
```

---

## 22. ORDEN DE IMPLEMENTACIÓN RECOMENDADO

Sigue este orden para evitar dependencias rotas:

```
1.  Entidades + Enums
2.  Repositorios
3.  Excepciones
4.  DTOs (Request y Response)
5.  Utilidades (JwtUtil, GeneradorQR, Constantes)
6.  Security (JwtFilter, UserDetailsServiceImpl, SecurityConfig)
7.  AutenticacionService + AuthController  ← primero prueba el login
8.  MesaService + MesaController           ← luego crea mesas y QRs
9.  ReservaService + ReservaController     ← flujo de reservas
10. PagoService + PagoController           ← procesar adelantos
11. ProductoMenu + MenuController          ← el menú del restaurante
12. PedidoService + PedidoController       ← pedidos por QR y mesero
13. CocinaController                       ← gestión de cocina
14. FacturaService + FacturaController     ← facturación
15. DeliveryService + DeliveryController   ← delivery
16. CancelacionService                     ← cancelaciones y reembolsos
17. ReporteService + ReporteController     ← reportes finales
```

---

## 23. NOTAS PARA PRODUCCIÓN

- **JWT Secret:** cambiar `jwt.secret` por una clave de 256 bits aleatoria
- **ddl-auto:** cambiar de `update` a `validate` y usar Flyway/Liquibase para migraciones
- **Pasarela de pago:** reemplazar `simularProcesoPasarela()` en `PagoService` con la SDK real (Stripe, Culqi, MercadoPago)
- **QR:** considerar guardar la imagen en un bucket S3 en lugar de base64 en BD
- **CORS:** ajustar `CorsConfig.java` para permitir solo el dominio del frontend
- **Logs:** integrar con ELK Stack o similar para producción
- **Rate limiting:** agregar `spring-boot-starter-actuator` + Bucket4j para limitar requests

---

## 24. ENTIDADES FALTANTES

### OrdenCocina.java

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes_cocina")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCocina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Prioridad de preparación (1 = urgente, 5 = normal)
    @Column(nullable = false)
    @Builder.Default
    private Integer prioridad = 3;

    private String notasCocinero;

    @Column(nullable = false)
    private LocalDateTime recibidoEn;

    private LocalDateTime inicioPreparacionEn;

    private LocalDateTime listoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cocinero_id")
    private Usuario cocineroAsignado;

    @PrePersist
    protected void alCrear() {
        recibidoEn = LocalDateTime.now();
    }
}
```

### Delivery.java

```java
package pe.edu.upeu.api_restaurant.entity;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoDelivery;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Usuario repartidor;

    @Column(nullable = false)
    private String direccionEntrega;

    private String referenciaDireccion;

    private Double latitud;
    private Double longitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoDelivery estado = EstadoDelivery.PENDIENTE;

    @Column(nullable = false)
    private BigDecimal costoDelivery;

    private String telefonoContacto;

    private LocalDateTime asignadoEn;
    private LocalDateTime salidaEn;
    private LocalDateTime entregadoEn;

    private String notasEntrega;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void alCrear() {
        creadoEn = LocalDateTime.now();
    }
}
```

### Cancelacion.java

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancelaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cancelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Una cancelación puede ser de reserva o de pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitado_por_id", nullable = false)
    private Usuario solicitadoPor;

    @Column(nullable = false)
    private String motivo;

    // Monto del adelanto o pago original
    @Column(nullable = false)
    private BigDecimal montoOriginal;

    // Monto que se reembolsa (puede ser total o parcial)
    @Column(nullable = false)
    private BigDecimal montoReembolso;

    // Monto de penalidad aplicada
    @Column(nullable = false)
    private BigDecimal montoPenalidad;

    private String observacionesAdmin;

    @Column(nullable = false)
    private boolean reembolsoProcesado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @PrePersist
    protected void alCrear() {
        creadaEn = LocalDateTime.now();
    }
}
```

---

## 25. REPOSITORIOS FALTANTES

```java
// OrdenCocinaRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.OrdenCocina;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdenCocinaRepository extends JpaRepository<OrdenCocina, Long> {

    // Pedidos activos en cocina, ordenados por prioridad y hora de llegada
    @Query("""
        SELECT o FROM OrdenCocina o
        WHERE o.pedido.estado IN ('ENVIADO_COCINA', 'EN_PREPARACION')
        ORDER BY o.prioridad ASC, o.recibidoEn ASC
    """)
    List<OrdenCocina> findPendientesEnCocina();

    List<OrdenCocina> findByCocineroAsignadoId(UUID cocineroId);
}
```

```java
// DeliveryRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.Delivery;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByEstado(EstadoDelivery estado);
    List<Delivery> findByRepartidorId(UUID repartidorId);
    List<Delivery> findByRepartidorIdAndEstado(UUID repartidorId, EstadoDelivery estado);
}
```

```java
// FacturaRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    List<Factura> findByClienteId(UUID clienteId);

    // Para reportes de facturación por rango de fechas
    @Query("SELECT f FROM Factura f WHERE f.emitidaEn BETWEEN :inicio AND :fin")
    List<Factura> findByRangoFechas(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    // Último número de factura para generar el siguiente (FAC-00001, FAC-00002...)
    @Query("SELECT MAX(f.numeroFactura) FROM Factura f")
    Optional<String> findUltimoNumeroFactura();
}
```

```java
// CancelacionRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.Cancelacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CancelacionRepository extends JpaRepository<Cancelacion, Long> {
    List<Cancelacion> findBySolicitadoPorId(UUID usuarioId);

    // Total de penalidades cobradas en un periodo (para reportes)
    @Query("""
        SELECT COALESCE(SUM(c.montoPenalidad), 0)
        FROM Cancelacion c
        WHERE c.creadaEn BETWEEN :inicio AND :fin
    """)
    BigDecimal sumaPenalidadesPorPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
}
```

```java
// PagoRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.Pago;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPago;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByClienteId(UUID clienteId);
    List<Pago> findByEstado(EstadoPago estado);

    // Total facturado en un periodo (para reportes de ventas)
    @Query("""
        SELECT COALESCE(SUM(p.monto), 0)
        FROM Pago p
        WHERE p.estado = 'CONFIRMADO'
        AND p.tipo = :tipo
        AND p.fechaPago BETWEEN :inicio AND :fin
    """)
    BigDecimal totalPorTipoYPeriodo(
        @Param("tipo") TipoPago tipo,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
}
```

```java
// ProductoMenuRepository.java
package pe.edu.upeu.api_restaurant.repository;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.ProductoMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoMenuRepository extends JpaRepository<ProductoMenu, Long> {
    List<ProductoMenu> findByDisponibleTrue();
    List<ProductoMenu> findByCategoriaId(UUID categoriaId);

    // Productos más pedidos (para reportes y recomendaciones)
    @Query("""
        SELECT dp.producto, SUM(dp.cantidad) as totalPedido
        FROM DetallePedido dp
        GROUP BY dp.producto
        ORDER BY totalPedido DESC
    """)
    List<Object[]> findProductosMasPedidos();

    @Query("SELECT p FROM ProductoMenu p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<ProductoMenu> buscarPorNombre(@Param("nombre") String nombre);
}
```

---

## 26. SERVICIOS — MÓDULOS FALTANTES

### CocinaService.java

```java
package pe.edu.upeu.api_restaurant.service;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.response.OrdenCocinaResponse;
import pe.edu.upeu.api_restaurant.entity.*;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import pe.edu.upeu.api_restaurant.exception.*;
import pe.edu.upeu.api_restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio del módulo de cocina.
 * Lógica de negocio: recepción de pedidos, asignación a cocineros,
 * actualización de estado y control de tiempos de preparación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CocinaService {

    private final OrdenCocinaRepository ordenCocinaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Lógica de negocio: obtener cola de pedidos pendientes en cocina.
     * Ordenados por prioridad (ascendente) y hora de llegada (FIFO).
     */
    public List<OrdenCocinaResponse> obtenerPendientesEnCocina() {
        return ordenCocinaRepository.findPendientesEnCocina().stream()
            .map(this::mapearAResponse)
            .collect(Collectors.toList());
    }

    /**
     * Flujo crítico: crear orden de cocina cuando un pedido es enviado.
     * Este método es invocado por PedidoService al enviar a cocina.
     */
    @Transactional
    public OrdenCocinaResponse crearOrdenCocina(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + pedidoId));

        // Validación de negocio: solo se crea orden si el pedido fue enviado a cocina
        if (pedido.getEstado() != EstadoPedido.ENVIADO_COCINA) {
            throw new ReglaNegocioException("El pedido debe estar en estado ENVIADO_COCINA para crear la orden");
        }

        OrdenCocina orden = OrdenCocina.builder()
            .pedido(pedido)
            .prioridad(calcularPrioridad(pedido))
            .build();

        OrdenCocina ordenGuardada = ordenCocinaRepository.save(orden);
        log.info("Orden de cocina creada para pedido ID: {}", pedidoId);
        return mapearAResponse(ordenGuardada);
    }

    /**
     * Lógica de negocio: asignar cocinero a una orden.
     * Solo usuarios con rol COCINERO pueden ser asignados.
     */
    @Transactional
    public OrdenCocinaResponse asignarCocinero(UUID ordenId, UUID cocineroId) {
        OrdenCocina orden = ordenCocinaRepository.findById(ordenId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden de cocina no encontrada"));

        Usuario cocinero = usuarioRepository.findById(cocineroId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cocinero no encontrado"));

        // Validación de negocio: solo roles de cocina pueden ser asignados
        if (cocinero.getRol().name().equals("COCINERO") == false) {
            throw new ReglaNegocioException("El usuario asignado debe tener el rol COCINERO");
        }

        orden.setCocineroAsignado(cocinero);
        return mapearAResponse(ordenCocinaRepository.save(orden));
    }

    /**
     * Lógica de negocio: marcar inicio de preparación.
     * Registra el timestamp de inicio para medir tiempos.
     */
    @Transactional
    public OrdenCocinaResponse iniciarPreparacion(UUID ordenId) {
        OrdenCocina orden = ordenCocinaRepository.findById(ordenId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden de cocina no encontrada"));

        orden.setInicioPreparacionEn(LocalDateTime.now());
        orden.getPedido().setEstado(EstadoPedido.EN_PREPARACION);
        pedidoRepository.save(orden.getPedido());

        return mapearAResponse(ordenCocinaRepository.save(orden));
    }

    /**
     * Lógica de negocio: marcar pedido como listo.
     * Actualiza el pedido y notifica que está listo para entregar.
     */
    @Transactional
    public OrdenCocinaResponse marcarComoListo(UUID ordenId) {
        OrdenCocina orden = ordenCocinaRepository.findById(ordenId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Orden de cocina no encontrada"));

        // Validación de negocio: debe haber iniciado la preparación antes de marcar como listo
        if (orden.getInicioPreparacionEn() == null) {
            throw new ReglaNegocioException("No se puede marcar como listo sin haber iniciado la preparación");
        }

        orden.setListoEn(LocalDateTime.now());
        orden.getPedido().setEstado(EstadoPedido.LISTO);
        pedidoRepository.save(orden.getPedido());

        log.info("Pedido ID: {} marcado como LISTO. Tiempo: {} minutos",
            orden.getPedido().getId(),
            calcularMinutosPreparacion(orden));

        return mapearAResponse(ordenCocinaRepository.save(orden));
    }

    /**
     * Calcula prioridad automática según tipo de pedido.
     * Delivery tiene mayor prioridad por el tiempo de espera del repartidor.
     */
    private Integer calcularPrioridad(Pedido pedido) {
        return pedido.isEsDelivery() ? 1 : 3;
    }

    private long calcularMinutosPreparacion(OrdenCocina orden) {
        if (orden.getInicioPreparacionEn() == null || orden.getListoEn() == null) return 0;
        return java.time.Duration.between(orden.getInicioPreparacionEn(), orden.getListoEn()).toMinutes();
    }

    private OrdenCocinaResponse mapearAResponse(OrdenCocina o) {
        return OrdenCocinaResponse.builder()
            .id(o.getId())
            .pedidoId(o.getPedido().getId())
            .estadoPedido(o.getPedido().getEstado())
            .prioridad(o.getPrioridad())
            .cocineroAsignado(o.getCocineroAsignado() != null ? o.getCocineroAsignado().getNombre() : null)
            .recibidoEn(o.getRecibidoEn())
            .inicioPreparacionEn(o.getInicioPreparacionEn())
            .listoEn(o.getListoEn())
            .build();
    }
}
```

### DeliveryService.java

```java
package pe.edu.upeu.api_restaurant.service;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.DeliveryRequest;
import pe.edu.upeu.api_restaurant.dto.response.DeliveryResponse;
import pe.edu.upeu.api_restaurant.entity.*;
import pe.edu.upeu.api_restaurant.entity.enums.*;
import pe.edu.upeu.api_restaurant.exception.*;
import pe.edu.upeu.api_restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio del módulo de delivery.
 * Lógica de negocio: creación de pedido delivery, asignación de repartidor
 * y seguimiento de estado hasta la entrega.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Lógica de negocio: crear registro de delivery para un pedido.
     * Solo pedidos marcados como esDelivery=true pueden tener delivery.
     */
    @Transactional
    public DeliveryResponse crearDelivery(DeliveryRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        // Validación de negocio: el pedido debe ser de tipo delivery
        if (!pedido.isEsDelivery()) {
            throw new ReglaNegocioException("El pedido no está marcado como delivery");
        }

        // Validación de negocio: no crear doble delivery para el mismo pedido
        boolean yaExiste = deliveryRepository.findAll().stream()
            .anyMatch(d -> d.getPedido().getId().equals(request.getPedidoId()));
        if (yaExiste) {
            throw new ReglaNegocioException("Ya existe un delivery para este pedido");
        }

        Delivery delivery = Delivery.builder()
            .pedido(pedido)
            .direccionEntrega(request.getDireccionEntrega())
            .referenciaDireccion(request.getReferenciaDireccion())
            .latitud(request.getLatitud())
            .longitud(request.getLongitud())
            .costoDelivery(request.getCostoDelivery())
            .telefonoContacto(request.getTelefonoContacto())
            .estado(EstadoDelivery.PENDIENTE)
            .build();

        return mapearAResponse(deliveryRepository.save(delivery));
    }

    /**
     * Lógica de negocio: asignar repartidor a un delivery pendiente.
     * Valida que el repartidor esté disponible (sin deliveries activos).
     */
    @Transactional
    public DeliveryResponse asignarRepartidor(UUID deliveryId, UUID repartidorId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Delivery no encontrado"));

        // Validación de negocio: solo se asigna si está pendiente
        if (delivery.getEstado() != EstadoDelivery.PENDIENTE) {
            throw new ReglaNegocioException("El delivery ya tiene repartidor asignado o fue completado");
        }

        Usuario repartidor = usuarioRepository.findById(repartidorId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Repartidor no encontrado"));

        // Validación de negocio: el repartidor debe tener el rol correcto
        if (!repartidor.getRol().name().equals("REPARTIDOR")) {
            throw new ReglaNegocioException("El usuario debe tener el rol REPARTIDOR");
        }

        // Validación de negocio: el repartidor no debe tener otro delivery activo
        List<Delivery> activosDelRepartidor = deliveryRepository
            .findByRepartidorIdAndEstado(repartidorId, EstadoDelivery.EN_CAMINO);
        if (!activosDelRepartidor.isEmpty()) {
            throw new ReglaNegocioException("El repartidor ya tiene un delivery en curso");
        }

        delivery.setRepartidor(repartidor);
        delivery.setEstado(EstadoDelivery.ASIGNADO);
        delivery.setAsignadoEn(LocalDateTime.now());

        log.info("Repartidor {} asignado al delivery {}", repartidor.getNombre(), deliveryId);
        return mapearAResponse(deliveryRepository.save(delivery));
    }

    /**
     * Lógica de negocio: actualizar estado del delivery por el repartidor.
     * Flujo permitido: ASIGNADO → EN_CAMINO → ENTREGADO (o FALLIDO).
     */
    @Transactional
    public DeliveryResponse actualizarEstado(UUID deliveryId, EstadoDelivery nuevoEstado) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Delivery no encontrado"));

        // Validación de negocio: transiciones de estado permitidas
        validarTransicionDelivery(delivery.getEstado(), nuevoEstado);

        delivery.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoDelivery.EN_CAMINO) {
            delivery.setSalidaEn(LocalDateTime.now());
        }

        if (nuevoEstado == EstadoDelivery.ENTREGADO) {
            delivery.setEntregadoEn(LocalDateTime.now());
            // Actualizar el pedido como entregado
            delivery.getPedido().setEstado(EstadoPedido.ENTREGADO);
            pedidoRepository.save(delivery.getPedido());
        }

        return mapearAResponse(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponse> listarPendientes() {
        return deliveryRepository.findByEstado(EstadoDelivery.PENDIENTE).stream()
            .map(this::mapearAResponse)
            .collect(Collectors.toList());
    }

    public List<DeliveryResponse> listarPorRepartidor(UUID repartidorId) {
        return deliveryRepository.findByRepartidorId(repartidorId).stream()
            .map(this::mapearAResponse)
            .collect(Collectors.toList());
    }

    private void validarTransicionDelivery(EstadoDelivery actual, EstadoDelivery nuevo) {
        boolean valida = switch (actual) {
            case PENDIENTE  -> nuevo == EstadoDelivery.ASIGNADO;
            case ASIGNADO   -> nuevo == EstadoDelivery.EN_CAMINO;
            case EN_CAMINO  -> nuevo == EstadoDelivery.ENTREGADO || nuevo == EstadoDelivery.FALLIDO;
            default -> false;
        };
        if (!valida) {
            throw new ReglaNegocioException(
                String.format("Transición de estado inválida: %s → %s", actual, nuevo)
            );
        }
    }

    private DeliveryResponse mapearAResponse(Delivery d) {
        return DeliveryResponse.builder()
            .id(d.getId())
            .pedidoId(d.getPedido().getId())
            .repartidor(d.getRepartidor() != null ? d.getRepartidor().getNombre() : null)
            .direccionEntrega(d.getDireccionEntrega())
            .estado(d.getEstado())
            .costoDelivery(d.getCostoDelivery())
            .creadoEn(d.getCreadoEn())
            .salidaEn(d.getSalidaEn())
            .entregadoEn(d.getEntregadoEn())
            .build();
    }
}
```

### FacturaService.java

```java
package pe.edu.upeu.api_restaurant.service;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.response.FacturaResponse;
import pe.edu.upeu.api_restaurant.entity.*;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import pe.edu.upeu.api_restaurant.exception.*;
import pe.edu.upeu.api_restaurant.repository.*;
import pe.edu.upeu.api_restaurant.util.ConstantesNegocio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Servicio de facturación.
 * Lógica de negocio: generación de facturas con IGV,
 * numeración correlativa y distinción entre boleta y factura con RUC.
 */
@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PedidoRepository pedidoRepository;

    /**
     * Flujo crítico: generación de factura o comprobante.
     * Calcula automáticamente el IGV y el total.
     * Genera número correlativo único (FAC-00001, FAC-00002...).
     */
    @Transactional
    public FacturaResponse generarFactura(UUID pedidoId, String rucCliente, String razonSocial) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + pedidoId));

        // Validación de negocio: el pedido debe estar entregado o listo para facturar
        if (pedido.getEstado() != EstadoPedido.ENTREGADO && pedido.getEstado() != EstadoPedido.LISTO) {
            throw new ReglaNegocioException(
                "Solo se puede facturar un pedido entregado o listo. Estado actual: " + pedido.getEstado()
            );
        }

        // Validación de negocio: no generar doble factura para el mismo pedido
        boolean yaFacturado = facturaRepository.findAll().stream()
            .anyMatch(f -> f.getPedido().getId().equals(pedidoId));
        if (yaFacturado) {
            throw new ReglaNegocioException("Este pedido ya fue facturado");
        }

        // Cálculo del IGV (18%) sobre el subtotal
        BigDecimal subtotal = pedido.getTotal();
        BigDecimal igv = subtotal.multiply(BigDecimal.valueOf(ConstantesNegocio.PORCENTAJE_IGV))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        Factura factura = Factura.builder()
            .numeroFactura(generarNumeroFactura())
            .pedido(pedido)
            .cliente(pedido.getCliente())
            .subtotal(subtotal)
            .igv(igv)
            .total(total)
            .rucCliente(rucCliente)
            .razonSocialCliente(razonSocial)
            .build();

        Factura facturaGuardada = facturaRepository.save(factura);
        return mapearAResponse(facturaGuardada);
    }

    public FacturaResponse obtenerFactura(UUID facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Factura no encontrada"));
        return mapearAResponse(factura);
    }

    /**
     * Generación del número correlativo de factura.
     * Formato: FAC-00001, FAC-00002, ... FAC-99999
     */
    private String generarNumeroFactura() {
        Optional<String> ultimo = facturaRepository.findUltimoNumeroFactura();
        int siguiente = 1;

        if (ultimo.isPresent()) {
            // Extraer el número del último: "FAC-00042" → 42
            String numeroStr = ultimo.get().replace(ConstantesNegocio.PREFIJO_FACTURA, "");
            siguiente = Integer.parseInt(numeroStr) + 1;
        }

        return String.format("%s%05d", ConstantesNegocio.PREFIJO_FACTURA, siguiente);
    }

    private FacturaResponse mapearAResponse(Factura f) {
        return FacturaResponse.builder()
            .id(f.getId())
            .numeroFactura(f.getNumeroFactura())
            .pedidoId(f.getPedido().getId())
            .nombreCliente(f.getCliente() != null ? f.getCliente().getNombre() : "")
            .rucCliente(f.getRucCliente())
            .razonSocial(f.getRazonSocialCliente())
            .subtotal(f.getSubtotal())
            .igv(f.getIgv())
            .total(f.getTotal())
            .emitidaEn(f.getEmitidaEn())
            .build();
    }
}
```

### CancelacionService.java

```java
package pe.edu.upeu.api_restaurant.service;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.CancelacionRequest;
import pe.edu.upeu.api_restaurant.dto.response.CancelacionResponse;
import pe.edu.upeu.api_restaurant.entity.*;
import pe.edu.upeu.api_restaurant.entity.enums.*;
import pe.edu.upeu.api_restaurant.exception.*;
import pe.edu.upeu.api_restaurant.repository.*;
import pe.edu.upeu.api_restaurant.util.ConstantesNegocio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Servicio de cancelaciones y reembolsos.
 * Lógica de negocio crítica:
 * - Cancelación de reserva con política de penalidad por tiempo
 * - Cancelación de pedido antes de ser enviado a cocina (sin penalidad)
 * - Cancelación de pedido en cocina (con penalidad parcial)
 * - Liberación de recursos (mesa) tras cancelación
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CancelacionService {

    private final CancelacionRepository cancelacionRepository;
    private final ReservaRepository reservaRepository;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Flujo crítico: cancelación de reserva con política de penalidad.
     *
     * Política de cancelación:
     * - Más de 24 horas antes: reembolso completo (0% penalidad)
     * - Menos de 24 horas antes: penalidad del 30% sobre el adelanto
     * - Día de la reserva (sin asistencia): penalidad del 100%
     */
    @Transactional
    public CancelacionResponse cancelarReserva(UUID reservaId, UUID usuarioId, String motivo) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada: " + reservaId));

        // Validación de negocio: solo se pueden cancelar reservas activas
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("La reserva ya está cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new ReglaNegocioException("No se puede cancelar una reserva completada");
        }

        Usuario solicitante = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Cálculo de penalidad según política de tiempo
        BigDecimal montoAdelanto = reserva.getMontoAdelanto();
        long horasHastaReserva = ChronoUnit.HOURS.between(LocalDateTime.now(), reserva.getFechaHoraInicio());

        BigDecimal montoPenalidad;
        BigDecimal montoReembolso;

        if (horasHastaReserva >= ConstantesNegocio.HORAS_CANCELACION_GRATUITA) {
            // Cancelación gratuita: reembolso total
            montoPenalidad = BigDecimal.ZERO;
            montoReembolso = montoAdelanto;
            log.info("Cancelación gratuita. Horas restantes: {}", horasHastaReserva);
        } else if (horasHastaReserva > 0) {
            // Cancelación tardía: penalidad del 30%
            montoPenalidad = montoAdelanto
                .multiply(BigDecimal.valueOf(ConstantesNegocio.PENALIDAD_CANCELACION))
                .setScale(2, RoundingMode.HALF_UP);
            montoReembolso = montoAdelanto.subtract(montoPenalidad);
            log.info("Cancelación tardía. Penalidad: {}, Reembolso: {}", montoPenalidad, montoReembolso);
        } else {
            // No asistió o cancela el mismo día: penalidad del 100%
            montoPenalidad = montoAdelanto;
            montoReembolso = BigDecimal.ZERO;
            log.info("No asistencia. Penalidad del 100%: {}", montoPenalidad);
        }

        // Actualizar el estado de la reserva
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // Liberar la mesa para que pueda ser reservada nuevamente
        Mesa mesa = reserva.getMesa();
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesaRepository.save(mesa);

        // Registrar la cancelación con todos sus detalles
        Cancelacion cancelacion = Cancelacion.builder()
            .reserva(reserva)
            .solicitadoPor(solicitante)
            .motivo(motivo)
            .montoOriginal(montoAdelanto)
            .montoPenalidad(montoPenalidad)
            .montoReembolso(montoReembolso)
            .reembolsoProcesado(montoReembolso.compareTo(BigDecimal.ZERO) == 0) // si no hay reembolso, se marca como procesado
            .build();

        Cancelacion guardada = cancelacionRepository.save(cancelacion);
        return mapearAResponse(guardada);
    }

    /**
     * Lógica de negocio: cancelación de pedido.
     * - PENDIENTE: cancelación sin penalidad, reembolso total si ya pagó
     * - ENVIADO_COCINA o EN_PREPARACION: penalidad del 50%
     * - LISTO o ENTREGADO: no se puede cancelar
     */
    @Transactional
    public CancelacionResponse cancelarPedido(UUID pedidoId, UUID usuarioId, String motivo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + pedidoId));

        // Validación de negocio: restricciones según estado del pedido
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new ReglaNegocioException("No se puede cancelar un pedido ya entregado");
        }
        if (pedido.getEstado() == EstadoPedido.LISTO) {
            throw new ReglaNegocioException("El pedido ya está listo para entregar, no se puede cancelar");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new ReglaNegocioException("El pedido ya fue cancelado previamente");
        }

        Usuario solicitante = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        BigDecimal montoTotal = pedido.getTotal();
        BigDecimal montoPenalidad;
        BigDecimal montoReembolso;

        // Aplicar penalidad si el pedido ya entró a cocina
        if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
            montoPenalidad = BigDecimal.ZERO;
            montoReembolso = montoTotal;
        } else {
            // En preparación: penalidad del 50%
            montoPenalidad = montoTotal.multiply(BigDecimal.valueOf(0.50)).setScale(2, RoundingMode.HALF_UP);
            montoReembolso = montoTotal.subtract(montoPenalidad);
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        Cancelacion cancelacion = Cancelacion.builder()
            .pedido(pedido)
            .solicitadoPor(solicitante)
            .motivo(motivo)
            .montoOriginal(montoTotal)
            .montoPenalidad(montoPenalidad)
            .montoReembolso(montoReembolso)
            .reembolsoProcesado(false)
            .build();

        return mapearAResponse(cancelacionRepository.save(cancelacion));
    }

    private CancelacionResponse mapearAResponse(Cancelacion c) {
        return CancelacionResponse.builder()
            .id(c.getId())
            .motivo(c.getMotivo())
            .montoOriginal(c.getMontoOriginal())
            .montoPenalidad(c.getMontoPenalidad())
            .montoReembolso(c.getMontoReembolso())
            .reembolsoProcesado(c.isReembolsoProcesado())
            .creadaEn(c.getCreadaEn())
            .build();
    }
}
```

### ReporteService.java

```java
package pe.edu.upeu.api_restaurant.service;

import pe.edu.upeu.api_restaurant.dto.response.ReporteResponse;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoMesa;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import pe.edu.upeu.api_restaurant.repository.*;
import pe.edu.upeu.api_restaurant.util.ConstantesNegocio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de reportes y estadísticas.
 * Lógica de negocio: consolida información del sistema para toma de decisiones.
 * Módulos: ventas, ocupación de mesas, productos más vendidos, cancelaciones.
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PagoRepository pagoRepository;
    private final MesaRepository mesaRepository;
    private final ProductoMenuRepository productoMenuRepository;
    private final CancelacionRepository cancelacionRepository;
    private final PedidoRepository pedidoRepository;

    /**
     * Reporte de ventas por periodo.
     * Incluye total de adelantos, consumo en mesa y delivery.
     */
    public ReporteResponse.Ventas reporteVentas(LocalDateTime inicio, LocalDateTime fin) {
        BigDecimal totalAdelantos = pagoRepository.totalPorTipoYPeriodo(TipoPago.ADELANTO_RESERVA, inicio, fin);
        BigDecimal totalConsumo   = pagoRepository.totalPorTipoYPeriodo(TipoPago.CONSUMO_FINAL, inicio, fin);
        BigDecimal totalDelivery  = pagoRepository.totalPorTipoYPeriodo(TipoPago.DELIVERY, inicio, fin);
        BigDecimal totalGeneral   = totalAdelantos.add(totalConsumo).add(totalDelivery);

        BigDecimal totalPenalidades = cancelacionRepository.sumaPenalidadesPorPeriodo(inicio, fin);

        return ReporteResponse.Ventas.builder()
            .periodo(inicio + " al " + fin)
            .totalAdelantos(totalAdelantos)
            .totalConsumoEnMesa(totalConsumo)
            .totalDelivery(totalDelivery)
            .totalGeneral(totalGeneral)
            .totalPenalidades(totalPenalidades)
            .build();
    }

    /**
     * Reporte de ocupación de mesas.
     * Muestra distribución de estados actual.
     */
    public ReporteResponse.OcupacionMesas reporteOcupacionMesas() {
        long totalMesas      = mesaRepository.count();
        long disponibles     = mesaRepository.findByEstado(EstadoMesa.DISPONIBLE).size();
        long ocupadas        = mesaRepository.findByEstado(EstadoMesa.OCUPADA).size();
        long reservadas      = mesaRepository.findByEstado(EstadoMesa.RESERVADA).size();
        long pendientePago   = mesaRepository.findByEstado(EstadoMesa.PENDIENTE_PAGO).size();

        double porcentajeOcupacion = totalMesas > 0
            ? ((double)(ocupadas + reservadas) / totalMesas) * 100
            : 0;

        return ReporteResponse.OcupacionMesas.builder()
            .totalMesas(totalMesas)
            .disponibles(disponibles)
            .ocupadas(ocupadas)
            .reservadas(reservadas)
            .pendientesPago(pendientePago)
            .porcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0)
            .build();
    }

    /**
     * Reporte de productos más vendidos.
     * Útil para ajustar el menú y optimizar el stock.
     */
    public List<ReporteResponse.ProductoVendido> reporteProductosMasVendidos() {
        return productoMenuRepository.findProductosMasPedidos().stream()
            .limit(10)
            .map(row -> ReporteResponse.ProductoVendido.builder()
                .nombreProducto(((pe.edu.upeu.api_restaurant.entity.ProductoMenu) row[0]).getNombre())
                .totalUnidades(((Number) row[1]).longValue())
                .build())
            .collect(Collectors.toList());
    }
}
```

---

## 27. CONTROLADORES — MÓDULOS FALTANTES

### CocinaController.java

```java
package pe.edu.upeu.api_restaurant.controller;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.response.OrdenCocinaResponse;
import pe.edu.upeu.api_restaurant.service.CocinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cocina")
@RequiredArgsConstructor
@Tag(name = "Cocina", description = "Gestión de órdenes en cocina")
public class CocinaController {

    private final CocinaService cocinaService;

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('COCINERO', 'ADMIN')")
    @Operation(summary = "Listar pedidos pendientes en cocina (ordenados por prioridad)")
    public ResponseEntity<List<OrdenCocinaResponse>> listarPendientes() {
        return ResponseEntity.ok(cocinaService.obtenerPendientesEnCocina());
    }

    @PostMapping("/ordenes/{ordenId}/asignar/{cocineroId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COCINERO')")
    @Operation(summary = "Asignar cocinero a una orden")
    public ResponseEntity<OrdenCocinaResponse> asignarCocinero(
            @PathVariable UUID ordenId,
            @PathVariable UUID cocineroId) {
        return ResponseEntity.ok(cocinaService.asignarCocinero(ordenId, cocineroId));
    }

    @PatchMapping("/ordenes/{ordenId}/iniciar")
    @PreAuthorize("hasRole('COCINERO')")
    @Operation(summary = "Iniciar preparación de una orden")
    public ResponseEntity<OrdenCocinaResponse> iniciarPreparacion(@PathVariable UUID ordenId) {
        return ResponseEntity.ok(cocinaService.iniciarPreparacion(ordenId));
    }

    @PatchMapping("/ordenes/{ordenId}/listo")
    @PreAuthorize("hasRole('COCINERO')")
    @Operation(summary = "Marcar orden como lista para entregar")
    public ResponseEntity<OrdenCocinaResponse> marcarComoListo(@PathVariable UUID ordenId) {
        return ResponseEntity.ok(cocinaService.marcarComoListo(ordenId));
    }
}
```

### DeliveryController.java

```java
package pe.edu.upeu.api_restaurant.controller;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.DeliveryRequest;
import pe.edu.upeu.api_restaurant.dto.response.DeliveryResponse;
import pe.edu.upeu.api_restaurant.entity.enums.EstadoDelivery;
import pe.edu.upeu.api_restaurant.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery", description = "Gestión de pedidos a domicilio")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    @Operation(summary = "Crear registro de delivery para un pedido")
    public ResponseEntity<DeliveryResponse> crearDelivery(@Valid @RequestBody DeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.crearDelivery(request));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPARTIDOR')")
    @Operation(summary = "Listar deliveries pendientes de asignación")
    public ResponseEntity<List<DeliveryResponse>> listarPendientes() {
        return ResponseEntity.ok(deliveryService.listarPendientes());
    }

    @PatchMapping("/{deliveryId}/asignar/{repartidorId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar repartidor a un delivery")
    public ResponseEntity<DeliveryResponse> asignarRepartidor(
            @PathVariable UUID deliveryId,
            @PathVariable UUID repartidorId) {
        return ResponseEntity.ok(deliveryService.asignarRepartidor(deliveryId, repartidorId));
    }

    @PatchMapping("/{deliveryId}/estado")
    @PreAuthorize("hasAnyRole('REPARTIDOR', 'ADMIN')")
    @Operation(summary = "Actualizar estado del delivery (EN_CAMINO, ENTREGADO, FALLIDO)")
    public ResponseEntity<DeliveryResponse> actualizarEstado(
            @PathVariable UUID deliveryId,
            @RequestParam EstadoDelivery nuevoEstado) {
        return ResponseEntity.ok(deliveryService.actualizarEstado(deliveryId, nuevoEstado));
    }

    @GetMapping("/mis-deliveries")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @Operation(summary = "Deliveries asignados al repartidor autenticado")
    public ResponseEntity<List<DeliveryResponse>> misDeliveries(
            @RequestParam UUID repartidorId) {  // en producción extraer del JWT
        return ResponseEntity.ok(deliveryService.listarPorRepartidor(repartidorId));
    }
}
```

### FacturaController.java

```java
package pe.edu.upeu.api_restaurant.controller;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.response.FacturaResponse;
import pe.edu.upeu.api_restaurant.service.FacturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
@Tag(name = "Facturación", description = "Generación y consulta de facturas y boletas")
public class FacturaController {

    private final FacturaService facturaService;

    @PostMapping("/generar/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    @Operation(summary = "Generar factura para un pedido completado")
    public ResponseEntity<FacturaResponse> generarFactura(
            @PathVariable UUID pedidoId,
            @RequestParam(required = false) String rucCliente,
            @RequestParam(required = false) String razonSocial) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(facturaService.generarFactura(pedidoId, rucCliente, razonSocial));
    }

    @GetMapping("/{facturaId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener detalle de una factura")
    public ResponseEntity<FacturaResponse> obtenerFactura(@PathVariable UUID facturaId) {
        return ResponseEntity.ok(facturaService.obtenerFactura(facturaId));
    }
}
```

### CancelacionController.java

```java
package pe.edu.upeu.api_restaurant.controller;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.request.CancelacionRequest;
import pe.edu.upeu.api_restaurant.dto.response.CancelacionResponse;
import pe.edu.upeu.api_restaurant.service.CancelacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cancelaciones")
@RequiredArgsConstructor
@Tag(name = "Cancelaciones", description = "Cancelación de reservas y pedidos con política de reembolso")
public class CancelacionController {

    private final CancelacionService cancelacionService;

    @PostMapping("/reserva/{reservaId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(summary = "Cancelar una reserva (aplica política de penalidad por tiempo)")
    public ResponseEntity<CancelacionResponse> cancelarReserva(
            @PathVariable UUID reservaId,
            @Valid @RequestBody CancelacionRequest request) {
        // En producción, usuarioId se extrae del JWT
        UUID usuarioId = request.getUsuarioId();
        return ResponseEntity.ok(
            cancelacionService.cancelarReserva(reservaId, usuarioId, request.getMotivo())
        );
    }

    @PostMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('MESERO', 'ADMIN', 'CLIENTE')")
    @Operation(summary = "Cancelar un pedido (sin penalidad si es PENDIENTE, con penalidad si está en cocina)")
    public ResponseEntity<CancelacionResponse> cancelarPedido(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody CancelacionRequest request) {
        return ResponseEntity.ok(
            cancelacionService.cancelarPedido(pedidoId, request.getUsuarioId(), request.getMotivo())
        );
    }
}
```

### ReporteController.java

```java
package pe.edu.upeu.api_restaurant.controller;

import pe.edu.upeu.api_restaurant.dto.response.ReporteResponse;
import pe.edu.upeu.api_restaurant.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reportes", description = "Reportes y estadísticas del restaurante")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas")
    @Operation(summary = "Reporte de ventas por periodo (adelantos + consumo + delivery)")
    public ResponseEntity<ReporteResponse.Ventas> reporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(reporteService.reporteVentas(inicio, fin));
    }

    @GetMapping("/mesas")
    @Operation(summary = "Reporte de ocupación actual de mesas")
    public ResponseEntity<ReporteResponse.OcupacionMesas> reporteMesas() {
        return ResponseEntity.ok(reporteService.reporteOcupacionMesas());
    }

    @GetMapping("/productos-mas-vendidos")
    @Operation(summary = "Top 10 productos más pedidos")
    public ResponseEntity<List<ReporteResponse.ProductoVendido>> reporteProductos() {
        return ResponseEntity.ok(reporteService.reporteProductosMasVendidos());
    }
}
```

---

## 28. DTOs FALTANTES

### DeliveryRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryRequest {

    @NotNull(message = "El ID de pedido es obligatorio")
    private UUID pedidoId;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String direccionEntrega;

    private String referenciaDireccion;
    private Double latitud;
    private Double longitud;

    @NotNull(message = "El costo de delivery es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    private BigDecimal costoDelivery;

    private String telefonoContacto;
}
```

### CancelacionRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CancelacionRequest {

    @NotNull(message = "El ID de usuario es obligatorio")
    private UUID usuarioId;

    @NotBlank(message = "El motivo de cancelación es obligatorio")
    @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
    private String motivo;
}
```

### CrearMesaRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CrearMesaRequest {

    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número de mesa debe ser mayor a 0")
    private Integer numero;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1 persona")
    @Max(value = 20, message = "La capacidad máxima es 20 personas")
    private Integer capacidad;

    private String ubicacion;
}
```

### LoginRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}
```

### DetallePedidoRequest.java

```java
package pe.edu.upeu.api_restaurant.dto.request;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DetallePedidoRequest {

    @NotNull(message = "El ID de producto es obligatorio")
    private UUID productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 50, message = "Cantidad máxima por producto: 50")
    private Integer cantidad;

    private String observaciones;
}
```

### Responses faltantes

```java
// OrdenCocinaResponse.java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrdenCocinaResponse {
    private UUID id;
    private UUID pedidoId;
    private EstadoPedido estadoPedido;
    private Integer prioridad;
    private String cocineroAsignado;
    private LocalDateTime recibidoEn;
    private LocalDateTime inicioPreparacionEn;
    private LocalDateTime listoEn;
}
```

```java
// DeliveryResponse.java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoDelivery;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryResponse {
    private UUID id;
    private UUID pedidoId;
    private String repartidor;
    private String direccionEntrega;
    private EstadoDelivery estado;
    private BigDecimal costoDelivery;
    private LocalDateTime creadoEn;
    private LocalDateTime salidaEn;
    private LocalDateTime entregadoEn;
}
```

```java
// FacturaResponse.java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FacturaResponse {
    private UUID id;
    private String numeroFactura;
    private UUID pedidoId;
    private String nombreCliente;
    private String rucCliente;
    private String razonSocial;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private LocalDateTime emitidaEn;
}
```

```java
// CancelacionResponse.java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CancelacionResponse {
    private UUID id;
    private String motivo;
    private BigDecimal montoOriginal;
    private BigDecimal montoPenalidad;
    private BigDecimal montoReembolso;
    private boolean reembolsoProcesado;
    private LocalDateTime creadaEn;
}
```

```java
// ReporteResponse.java — clase contenedora con inner classes
package pe.edu.upeu.api_restaurant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class ReporteResponse {

    @Data
    @Builder
    public static class Ventas {
        private String periodo;
        private BigDecimal totalAdelantos;
        private BigDecimal totalConsumoEnMesa;
        private BigDecimal totalDelivery;
        private BigDecimal totalGeneral;
        private BigDecimal totalPenalidades;
    }

    @Data
    @Builder
    public static class OcupacionMesas {
        private long totalMesas;
        private long disponibles;
        private long ocupadas;
        private long reservadas;
        private long pendientesPago;
        private double porcentajeOcupacion;
    }

    @Data
    @Builder
    public static class ProductoVendido {
        private String nombreProducto;
        private long totalUnidades;
    }
}
```

```java
// PagoResponse.java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPago;
import pe.edu.upeu.api_restaurant.entity.enums.TipoPago;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PagoResponse {
    private UUID id;
    private EstadoPago estado;
    private String referencia;
    private BigDecimal monto;
    private TipoPago tipo;
    private LocalDateTime fechaPago;
}
```

```java
// PedidoResponse.java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.entity.enums.EstadoPedido;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PedidoResponse {
    private UUID id;
    private EstadoPedido estado;
    private BigDecimal total;
    private boolean esDelivery;
    private LocalDateTime creadoEn;
}
```

---

## 29. CONTROLADORES EXTRA NECESARIOS

### MenuController.java (público para clientes por QR)

```java
package pe.edu.upeu.api_restaurant.controller;
import java.util.UUID;

import pe.edu.upeu.api_restaurant.dto.response.MenuResponse;
import pe.edu.upeu.api_restaurant.entity.ProductoMenu;
import pe.edu.upeu.api_restaurant.repository.ProductoMenuRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "Menú", description = "Consulta pública del menú del restaurante")
public class MenuController {

    private final ProductoMenuRepository productoMenuRepository;

    // Endpoint público: el cliente ve el menú desde el QR sin autenticarse
    @GetMapping
    @Operation(summary = "Listar todos los productos disponibles (público)")
    public ResponseEntity<List<MenuResponse>> listarMenu() {
        List<MenuResponse> menu = productoMenuRepository.findByDisponibleTrue()
            .stream()
            .map(p -> MenuResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .imagenUrl(p.getImagenUrl())
                .tiempoPreparacionMinutos(p.getTiempoPreparacionMinutos())
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Filtrar menú por categoría (público)")
    public ResponseEntity<List<MenuResponse>> listarPorCategoria(@PathVariable UUID categoriaId) {
        List<MenuResponse> menu = productoMenuRepository.findByCategoriaId(categoriaId)
            .stream()
            .filter(ProductoMenu::isDisponible)
            .map(p -> MenuResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .precio(p.getPrecio())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(menu);
    }
}
```

```java
// MenuResponse.java
package pe.edu.upeu.api_restaurant.dto.response;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MenuResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer tiempoPreparacionMinutos;
    private String categoriaNombre;
}
```

---

## 30. ENTIDAD CATEGORIA

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nombre;          // "Entradas", "Platos de fondo", "Bebidas", etc.

    private String descripcion;

    private String iconoUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean activa = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer orden = 0;      // Orden de aparición en el menú
}
```

---

## 31. CONFIGURACIÓN CORS

```java
package pe.edu.upeu.api_restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // En producción: reemplazar con el dominio real del frontend
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
```

---

## 32. CONFIGURACIÓN DE AUDITORÍA

```java
package pe.edu.upeu.api_restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditoriaConfig {

    // Captura el email del usuario autenticado para campos @CreatedBy y @LastModifiedBy
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return Optional.of("sistema");
            }
            return Optional.of(auth.getName());
        };
    }
}
```

---

## 33. DATOS INICIALES — data.sql (opcional)

Crea este archivo en `src/main/resources/data.sql` para tener datos de prueba:

```sql
-- Categorías del menú
INSERT INTO categorias (nombre, descripcion, orden, activa)
VALUES
    ('Entradas', 'Platos para comenzar', 1, true),
    ('Platos de fondo', 'Platos principales', 2, true),
    ('Postres', 'Dulces y postres', 3, true),
    ('Bebidas', 'Bebidas frías y calientes', 4, true),
    ('Delivery especial', 'Solo para pedidos delivery', 5, true)
ON CONFLICT DO NOTHING;

-- Productos del menú
INSERT INTO productos_menu (nombre, descripcion, precio, disponible, tiempo_preparacion_minutos, categoria_id)
VALUES
    ('Ceviche clásico', 'Ceviche de pescado con leche de tigre', 35.00, true, 10, 1),
    ('Tequeños', '6 tequeños con queso crema', 18.00, true, 8, 1),
    ('Lomo saltado', 'Lomo saltado con papas y arroz', 48.00, true, 15, 2),
    ('Aji de gallina', 'Ají de gallina con arroz y papas', 42.00, true, 12, 2),
    ('Arroz con leche', 'Postre tradicional peruano', 12.00, true, 0, 3),
    ('Agua mineral', 'Agua San Luis 600ml', 5.00, true, 0, 4),
    ('Inca Kola', 'Gaseosa 500ml', 6.00, true, 0, 4)
ON CONFLICT DO NOTHING;

-- Usuario administrador por defecto
-- Contraseña: admin123 (encriptada con BCrypt)
INSERT INTO usuarios (nombre, email, contrasena, rol, activo)
VALUES (
    'Administrador',
    'admin@restaurante.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y',
    'ADMIN',
    true
) ON CONFLICT DO NOTHING;
```

Para habilitar el data.sql, agrega en `application.yml`:
```yaml
spring:
  sql:
    init:
      mode: always
```

---

## 34. VARIABLES DE ENTORNO — .env

```env
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/restaurante_db
DB_USERNAME=restaurante_user
DB_PASSWORD=restaurante_pass

# JWT — cambiar en producción por clave aleatoria de 256 bits
JWT_SECRET=mi-clave-super-secreta-de-produccion-cambiar-antes-de-deploy
JWT_EXPIRACION_MS=86400000

# Servidor
SERVER_PORT=8080
```

Y referencias en `application.yml`:
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiracion-ms: ${JWT_EXPIRACION_MS}
```

---

## 35. CHECKLIST DE VERIFICACIÓN

Antes de probar la API, verifica que tienes todo:

```
Infraestructura
  ✅ Docker Desktop corriendo
  ✅ docker-compose up -d ejecutado sin errores
  ✅ Puerto 5432 libre y accesible

Proyecto
  ✅ Java 21 instalado (java -version)
  ✅ build.gradle con todas las dependencias
  ✅ application.yml configurado con credenciales correctas
  ✅ Estructura de carpetas creada

Prueba básica (en este orden)
  1. POST /api/auth/registrar        → debe devolver JWT
  2. POST /api/auth/login            → debe devolver JWT
  3. POST /api/mesas (con JWT)       → debe crear mesa y QR
  4. GET  /api/mesas/qr/{token}      → debe devolver info de mesa
  5. GET  /api/menu                  → debe listar productos
  6. POST /api/reservas (con JWT)    → debe crear reserva PENDIENTE_PAGO
  7. POST /api/pagos/procesar        → debe confirmar reserva
  8. POST /api/pedidos (con JWT)     → debe crear pedido
  9. GET  /api/swagger-ui.html       → debe mostrar documentación completa
```

---

## 36. CONFIGURACIÓN DE UUID EN POSTGRESQL

### PostgreSQL y UUID — Notas importantes

Spring Boot 3.x con Hibernate 6 soporta `GenerationType.UUID` de forma nativa. PostgreSQL almacena el UUID como tipo `uuid` (16 bytes), mucho más eficiente que `varchar`.

Para asegurar que la columna se cree como tipo `uuid` en PostgreSQL, agrega en `application.yml`:

```yaml
spring:
  jpa:
    properties:
      hibernate:
        # Fuerza tipo de columna uuid nativo de PostgreSQL
        type:
          preferred_uuid_jdbc_type: CHAR
```

> Con `ddl-auto: update` Hibernate crea las columnas como `uuid` automáticamente en PostgreSQL.

### Entidad base con UUID (opcional — para no repetir en cada entidad)

Puedes crear una clase base para no repetir `@Id` en cada entidad:

```java
package pe.edu.upeu.api_restaurant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.UUID;

/**
 * Clase base que provee ID UUID a todas las entidades del sistema.
 * Todas las entidades heredan de esta clase en lugar de declarar @Id individualmente.
 */
@MappedSuperclass
@Getter
public abstract class EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
}
```

Luego cada entidad extiende `EntidadBase` y elimina la declaración del `@Id`:

```java
// Ejemplo: Mesa usando EntidadBase
@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa extends EntidadBase {
    // Ya no necesitas declarar @Id ni private UUID id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidad;
    // ... resto de campos
}
```

### Cómo recibir UUID en endpoints

Los UUID en los path params llegan como `String` en la URL y Spring los convierte automáticamente a `UUID`:

```
GET /api/mesas/123e4567-e89b-12d3-a456-426614174000
                ↑ Spring convierte esto a UUID automáticamente con @PathVariable UUID mesaId
```

### Repositorios con UUID — sin cambios

Spring Data JPA detecta el tipo del ID automáticamente:

```java
// Funciona igual, solo cambia el tipo genérico
public interface MesaRepository extends JpaRepository<Mesa, UUID> {
    Optional<Mesa> findByNumero(Integer numero);
    // Todos los métodos findById, save, delete funcionan igual con UUID
}
```

**Todos los repositorios del proyecto deben declararse como `JpaRepository<Entidad, UUID>`:**

```java
public interface UsuarioRepository    extends JpaRepository<Usuario,    UUID> { ... }
public interface MesaRepository       extends JpaRepository<Mesa,       UUID> { ... }
public interface ReservaRepository    extends JpaRepository<Reserva,    UUID> { ... }
public interface PagoRepository       extends JpaRepository<Pago,       UUID> { ... }
public interface PedidoRepository     extends JpaRepository<Pedido,     UUID> { ... }
public interface OrdenCocinaRepository extends JpaRepository<OrdenCocina, UUID> { ... }
public interface DeliveryRepository   extends JpaRepository<Delivery,   UUID> { ... }
public interface FacturaRepository    extends JpaRepository<Factura,    UUID> { ... }
public interface CancelacionRepository extends JpaRepository<Cancelacion, UUID> { ... }
public interface ProductoMenuRepository extends JpaRepository<ProductoMenu, UUID> { ... }
```

### Extracción de UUID desde JWT

En producción, el `clienteId` se extrae del token JWT (no se pasa como parámetro). Actualiza `JwtUtil`:

```java
// JwtUtil.java — agregar soporte para UUID en el token
public String generarToken(String email, String rol, UUID usuarioId) {
    return Jwts.builder()
        .subject(email)
        .claim("rol", rol)
        .claim("usuarioId", usuarioId.toString())   // UUID como String en el claim
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiracionMs))
        .signWith(obtenerClave())
        .compact();
}

public UUID extraerUsuarioId(String token) {
    String uuidStr = parsearToken(token).getPayload().get("usuarioId", String.class);
    return UUID.fromString(uuidStr);
}
```

Actualiza los controladores para extraer el UUID del JWT en lugar de usar placeholder:

```java
// En AuthController, al registrar/login, incluir el UUID en el token
String token = jwtUtil.generarToken(
    usuarioGuardado.getEmail(),
    usuarioGuardado.getRol().name(),
    usuarioGuardado.getId()   // UUID del usuario
);

// En cualquier controller que necesite el ID del usuario autenticado:
@PostMapping("/pedidos")
public ResponseEntity<PedidoResponse> crearPedido(
        @RequestHeader("Authorization") String authHeader,
        @Valid @RequestBody CrearPedidoRequest request) {

    String token = authHeader.substring(7);
    UUID clienteId = jwtUtil.extraerUsuarioId(token);  // extrae UUID del JWT

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(pedidoService.crearPedido(clienteId, request));
}
```

### data.sql actualizado con UUID

PostgreSQL genera UUID automáticamente al insertar, así que el `data.sql` no necesita cambios:

```sql
-- PostgreSQL genera el UUID automáticamente gracias a GenerationType.UUID
-- No es necesario especificar el ID en los INSERT

INSERT INTO categorias (nombre, descripcion, orden, activa)
VALUES
    ('Entradas', 'Platos para comenzar', 1, true),
    ('Platos de fondo', 'Platos principales', 2, true)
ON CONFLICT (nombre) DO NOTHING;

-- Para el usuario admin, la contraseña es: admin123
INSERT INTO usuarios (nombre, email, contrasena, rol, activo)
VALUES (
    'Administrador',
    'admin@restaurante.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y',
    'ADMIN',
    true
) ON CONFLICT (email) DO NOTHING;
```

### Respuesta JSON con UUID

Los UUID se serializan automáticamente como string en las respuestas JSON:

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "numero": 5,
  "capacidad": 4,
  "estado": "DISPONIBLE",
  "urlQR": "http://localhost:8080/api/mesas/qr/550e8400-e29b-41d4-a716-446655440000"
}
```
