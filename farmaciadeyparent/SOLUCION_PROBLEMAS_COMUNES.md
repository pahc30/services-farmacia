# Solución de Problemas Comunes - Microservicios Farmacia

## Problema: ClassNotFoundException en servicios

### Descripción
Los servicios pueden mostrar errores como:
- `ClassNotFoundException: pe.com.farmaciadey.auth.models.responses.JwtResponse`
- `ClassNotFoundException: pe.com.farmaciadey.compra.models.responses.DataResponse`

### Causa
Este error ocurre cuando:
1. El servicio está ejecutándose con clases compiladas obsoletas
2. Las clases no se compilaron correctamente después de cambios
3. El classpath no incluye las clases recién compiladas

### Solución

#### Paso 1: Detener el servicio problemático
```bash
# Para encontrar el proceso usando el puerto
lsof -ti:PUERTO | xargs kill -9

# Ejemplos:
lsof -ti:7011 | xargs kill -9  # Auth service
lsof -ti:7015 | xargs kill -9  # Compra service
```

#### Paso 2: Limpiar y recompilar
```bash
cd businessdomain/[nombre-servicio]
mvn clean compile
```

#### Paso 3: Reiniciar el servicio
```bash
mvn spring-boot:run
```

### Verificación
Confirmar que el servicio inició correctamente:
```bash
curl -X GET http://localhost:PUERTO/[contexto]/actuator/health
```

## Problema: Imágenes de productos no se muestran

### Descripción
Las imágenes de productos aparecen rotas en la interfaz web. El navegador intenta cargar imágenes desde URLs como:
`http://localhost:7013/producto/c69f3481-eb12-49b3-90cb-5503d221eea0paracetamol.jpeg`

### Causa
El endpoint de imágenes estaba configurado para descargar archivos como attachments en lugar de mostrarlos como imágenes en el navegador.

### Solución

#### Paso 1: Verificar que las imágenes existen
```bash
ls -la businessdomain/producto/uploads/
```
Debe mostrar archivos como: `c69f3481-eb12-49b3-90cb-5503d221eea0paracetamol.jpeg`

#### Paso 2: Verificar el endpoint de imágenes
El endpoint `@GetMapping("/{fileName}")` en `ProductoController.java` debe estar configurado para:
- Detectar el tipo de contenido MIME correcto (`image/jpeg`, `image/png`, etc.)
- Enviar header `Content-Type` en lugar de `Content-Disposition: attachment`
- Incluir header de cache para mejorar rendimiento

#### Paso 3: Recompilar y reiniciar el servicio
```bash
cd businessdomain/producto
mvn clean compile
# Detener servicio actual
lsof -ti:7013 | xargs kill -9
# Reiniciar servicio
mvn spring-boot:run
```

#### Paso 4: Verificar funcionamiento
```bash
# Probar endpoint de imagen
curl -I http://localhost:7013/producto/NOMBRE_IMAGEN.jpeg
# Debe devolver: Content-Type: image/jpeg
```

### Código corregido
```java
@GetMapping("/{fileName}")
public ResponseEntity<Resource> getFile(@PathVariable("fileName") String fileName) {
    try {
        Path filePath = Paths.get(uploadDir + "/" + fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            // Detectar el tipo de contenido basado en la extensión del archivo
            String contentType = "application/octet-stream";
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            
            switch (fileExtension) {
                case "jpg":
                case "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                case "gif":
                    contentType = "image/gif";
                    break;
                case "webp":
                    contentType = "image/webp";
                    break;
            }
            
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
```

## Problema: DNS Warning en macOS

### Descripción
Warning que aparece en servicios que usan WebFlux:
```
Unable to load io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider, 
fallback to system defaults. This may result in incorrect DNS resolutions on MacOS.
```

### Solución
Agregar la dependencia de Netty DNS nativo para macOS en el `pom.xml`:

```xml
<!-- Netty DNS native resolver para macOS (elimina warnings DNS) -->
<dependency>
  <groupId>io.netty</groupId>
  <artifactId>netty-resolver-dns-native-macos</artifactId>
  <classifier>osx-x86_64</classifier>
  <scope>runtime</scope>
</dependency>
```

### Servicios afectados
- AppGateway (puerto 9000)
- Compra (puerto 7015)
- Cualquier servicio que use WebFlux o WebClient

## Puertos de servicios

| Servicio | Puerto | Contexto |
|----------|--------|----------|
| AppGateway | 9000 | / |
| Auth | 7011 | /auth |
| Usuario | 7012 | /usuario |
| Producto | 7013 | /producto |
| MetodoPago | 7014 | /metodopago |
| Compra | 7015 | /compra |

## Comandos útiles

### Verificar servicios activos
```bash
# Ver todos los puertos en uso
netstat -tulpn | grep LISTEN

# Ver proceso específico
lsof -i :PUERTO
```

### Reiniciar todos los servicios
```bash
# Desde el directorio raíz del proyecto
./stop-all-services.sh
./start-all-services.sh
```

### Compilar todos los módulos
```bash
# Desde el directorio raíz
mvn clean compile -f businessdomain/pom.xml
```

## Notas importantes

1. **Orden de inicio**: Siempre iniciar los servicios base antes que el gateway
2. **Base de datos**: Asegurar que MySQL esté ejecutándose antes de iniciar los servicios
3. **Memoria**: Los servicios pueden requerir tiempo para cargar completamente
4. **Logs**: Revisar los logs para identificar problemas específicos

---
*Documento actualizado: 3 de noviembre de 2025*