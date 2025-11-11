# Solución: Persistencia de Imágenes en Docker

## Problema
Las imágenes de productos desaparecían cada vez que se reiniciaban los contenedores Docker porque se guardaban **dentro del contenedor** en una ruta temporal.

## Solución Implementada

### 1. **Configuración de Ruta de Uploads** (`application.properties`)
```properties
# File upload configuration
file.upload-dir=${UPLOAD_DIR:/app/uploads}
```
- Variable de entorno `UPLOAD_DIR` con valor por defecto `/app/uploads`
- Configurable desde variables de entorno en Docker

### 2. **ProductoController Modificado**
```java
@Value("${file.upload-dir:/app/uploads}")
private String uploadDir;
```
- Cambió de `private final String uploadDir = "uploads"` a usar `@Value` con property
- Ahora lee la ruta desde la configuración

### 3. **Docker Compose con Volumen Persistente**
```yaml
farmacia-producto:
  environment:
    - UPLOAD_DIR=/app/uploads
  volumes:
    - producto_uploads:/app/uploads

volumes:
  producto_uploads:
    driver: local
    name: farmacia_producto_uploads
```

### 4. **Dockerfile Actualizado**
```dockerfile
# Create uploads directory for file handling with correct path
RUN mkdir -p /app/uploads && chown -R farmacia:farmacia /app/uploads
```

## Ventajas de esta Solución

✅ **Persistencia**: Las imágenes sobreviven a reinicios de contenedores
✅ **Separación de Datos**: Los datos están fuera del ciclo de vida del contenedor
✅ **Backup Fácil**: El volumen se puede respaldar independientemente
✅ **Portabilidad**: Funciona en cualquier entorno Docker
✅ **Escalabilidad**: Si se necesitan múltiples instancias, se puede usar volumen compartido

## Cómo Funciona

1. **Volumen Docker**: `farmacia_producto_uploads` se crea automáticamente
2. **Mapeo**: El volumen se monta en `/app/uploads` dentro del contenedor
3. **Persistencia**: Los archivos quedan en `/var/lib/docker/volumes/farmacia_producto_uploads/_data` en el host
4. **Supervivencia**: Aunque el contenedor se elimine, el volumen permanece

## Comandos Útiles

### Ver el volumen
```bash
docker volume inspect farmacia_producto_uploads
```

### Ver contenido del volumen
```bash
docker exec farmacia-producto ls -la /app/uploads
```

### Backup manual del volumen
```bash
docker run --rm -v farmacia_producto_uploads:/source -v $(pwd):/backup alpine tar -czf /backup/uploads-backup.tar.gz -C /source .
```

### Restaurar backup
```bash
docker run --rm -v farmacia_producto_uploads:/target -v $(pwd):/backup alpine tar -xzf /backup/uploads-backup.tar.gz -C /target
```

### Eliminar volumen (¡CUIDADO! Se pierden las imágenes)
```bash
docker compose down
docker volume rm farmacia_producto_uploads
```

## Testing

1. **Subir una imagen** desde el frontend
2. **Reiniciar el servicio**:
   ```bash
   docker compose restart farmacia-producto
   ```
3. **Verificar que la imagen sigue disponible** al recargar la página

## Ubicación de Archivos en el Host

La ruta física en tu Mac es:
```
/var/lib/docker/volumes/farmacia_producto_uploads/_data/
```

**Nota**: En macOS con Docker Desktop, esto está dentro de la VM de Docker, accesible mediante comandos `docker exec`.

## Migración de Imágenes Existentes

Si ya tenías imágenes en la carpeta `uploads` antigua:

```bash
# 1. Copiar desde el contenedor viejo al volumen nuevo
docker cp farmacia-producto-old:/app/uploads/. ./temp-uploads/
docker cp ./temp-uploads/. farmacia-producto:/app/uploads/
rm -rf ./temp-uploads

# 2. O copiar directamente si tienes las imágenes localmente
docker cp ./businessdomain/producto/uploads/. farmacia-producto:/app/uploads/
```

## Troubleshooting

### Problema: Las imágenes no se guardan
**Solución**: Verificar que el directorio tenga permisos correctos
```bash
docker exec farmacia-producto ls -la /app/uploads
# Debe mostrar: drwxr-xr-x farmacia farmacia
```

### Problema: "Permission denied" al subir imagen
**Solución**: Verificar que el usuario `farmacia` tenga permisos
```bash
docker exec -u root farmacia-producto chown -R farmacia:farmacia /app/uploads
```

### Problema: El volumen no se creó
**Solución**: Recrear con docker compose
```bash
docker compose down
docker volume create farmacia_producto_uploads
docker compose up -d farmacia-producto
```

## Archivos Modificados

1. `businessdomain/producto/src/main/resources/application.properties`
2. `businessdomain/producto/src/main/java/.../ProductoController.java`
3. `businessdomain/producto/Dockerfile`
4. `docker-compose.yml`

---
**Fecha**: Noviembre 2025  
**Autor**: Sistema de Persistencia Farmacia Dey
