# App Gateway (Spring Cloud Gateway)

Gateway reactivo que centraliza el tráfico del frontend hacia los microservicios. Expone un único origen (http://localhost:9000) y enruta por path a cada servicio, con CORS habilitado para el front en http://localhost:4200.

## Rutas
- `/auth/**`       → http://localhost:7011 (contexto del servicio: `/auth`)
- `/usuario/**`    → http://localhost:7012 (contexto: `/usuario`)
- `/producto/**`   → http://localhost:7013 (contexto: `/producto`)
- `/metodopago/**` → http://localhost:7014 (contexto: `/metodopago`)
- `/compra/**`     → http://localhost:7015 (contexto: `/compra`)

CORS (en `application.yml`):
- allowedOrigins: `http://localhost:4200`
- allowedMethods: `GET, POST, PUT, DELETE, OPTIONS`
- allowedHeaders: `*`
- allowCredentials: `true`

## Requisitos
- Java 21
- Maven Wrapper (incluido en el repo)
- Microservicios levantados en puertos 7011–7015 con sus context-paths (auth/usuario/producto/metodopago/compra)

## Ejecutar
Desde el módulo `businessdomain/appgw`:

```bash
# (opcional) construir sin tests
../../mvnw -DskipTests -U clean package

# ejecutar en 9000
../../mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9000"
```

Comprobaciones rápidas:
```bash
# Salud (enrutadas)
curl -i http://localhost:9000/usuario/actuator/health   # 200
curl -i http://localhost:9000/producto/actuator/health  # 200
curl -i http://localhost:9000/metodopago/actuator/health# 200
curl -i http://localhost:9000/compra/actuator/health    # 200
curl -i http://localhost:9000/auth/actuator/health      # 403 (esperado por seguridad)

# Login vía gateway (usuario seed test1/test1)
curl -H "Content-Type: application/json" \
     -d '{"username":"test1","password":"test1"}' \
     http://localhost:9000/auth/api/auth/login
```

## Notas de compatibilidad
Este proyecto usa Spring Boot 3.5.x. Spring Cloud Gateway se ejecuta con el verificador de compatibilidad deshabilitado (`spring.cloud.compatibility-verifier.enabled=false`).

Alternativas si prefieres compatibilidad oficial:
- Cambiar a Spring Boot 3.3.x (release train soportado), o
- Adoptar el release train de Spring Cloud compatible con 3.5.x cuando esté disponible.

## Troubleshooting
- 500 al llamar por gateway: usualmente el servicio destino no está arriba o mal el path. Verifica salud directa del servicio en `http://localhost:<puerto>/<contexto>/actuator/health`.
- 403 en `/auth/actuator/health`: esperado por seguridad. Usa endpoints públicos o login para validar.
- Puerto 9000 en uso: libera el puerto y vuelve a levantar.
- CORS desde el front: confirma `http://localhost:4200` como origen permitido o ajusta `application.yml`.
- Front (Angular) en dev: apunta tus llamadas de API a `http://localhost:9000` para un único origen.

## Evidencias E2E
El script `./ejecutar-pruebas-e2e.sh` crea una carpeta `evidencias-e2e-YYYYMMDD-HHMMSS/` con:
- RESUMEN.md (estado general)
- Respuestas de login / screenshots / reports (Selenium, Surefire)
