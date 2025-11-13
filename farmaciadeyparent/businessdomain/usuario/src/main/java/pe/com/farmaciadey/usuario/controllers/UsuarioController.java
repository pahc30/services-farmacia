package pe.com.farmaciadey.usuario.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import pe.com.farmaciadey.usuario.models.Usuario;
import pe.com.farmaciadey.usuario.models.responses.DataResponse;
import pe.com.farmaciadey.usuario.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService service;
    
    /**
     * Extrae el rol del token JWT de manera simple
     */
    private String extractRoleFromToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        
        try {
            String token = authHeader.substring(7);
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                return null;
            }
            
            // Decodificar el payload (segundo elemento)
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            return jsonNode.has("rol") ? jsonNode.get("rol").asText() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping(value = "/registrar")
    public ResponseEntity<Object> registrar(@RequestBody Usuario request) throws Exception {
        DataResponse response = new DataResponse();
        try {
            Usuario usuarioCreado = service.save(request);
            response.setEstado(1);
            response.setMensaje("Usuario registrado exitosamente");
            response.setDato(usuarioCreado);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setEstado(0);
            response.setMensaje("Error al registrar usuario: " + e.getMessage());
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Object> save(@RequestBody Usuario request) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.save(request));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Integer id, @RequestBody Usuario request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) throws Exception {
        DataResponse response = new DataResponse();
        try {
            if (id != request.getId()) {
                response.setEstado(0);
                response.setMensaje("ID no coincide con el usuario a actualizar");
                response.setException("No se pudo actualizar el registro");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Verificar si el usuario es administrador para permitir cambios de identificación
            String rolUsuarioActual = extractRoleFromToken(authHeader);
            boolean esAdministrador = "ADMINISTRADOR".equals(rolUsuarioActual);
            
            Usuario usuarioActualizado;
            if (esAdministrador) {
                // Si es administrador, usar el método adminUpdate que permite cambiar identificación
                usuarioActualizado = service.adminUpdate(request);
                response.setMensaje("Usuario actualizado por administrador exitosamente (identificación permitida)");
            } else {
                // Si es usuario normal, usar el método estándar que no permite cambiar identificación
                usuarioActualizado = service.update(request);
                response.setMensaje("Usuario actualizado exitosamente");
            }
            
            if (usuarioActualizado != null) {
                response.setEstado(1);
                response.setDato(usuarioActualizado);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setEstado(0);
                response.setMensaje("Usuario no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setEstado(0);
            response.setMensaje("Error al actualizar usuario: " + e.getMessage());
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/list")
    public ResponseEntity<Object> list() throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.list());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/find/{id}")
    public ResponseEntity<Object> find(@PathVariable("id") Integer id) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.find(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.delete(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/saveWithAuth")
    public ResponseEntity<Object> saveWithAuth(@RequestBody Usuario request) throws Exception {
        DataResponse response = new DataResponse();
        try {
            Usuario usuarioCreado = service.saveWithAuth(request);
            response.setEstado(1);
            response.setMensaje("Usuario y credenciales creados exitosamente");
            response.setDato(usuarioCreado);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setEstado(0);
            response.setMensaje("Error al crear usuario con credenciales: " + e.getMessage());
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/admin/update/{id}")
    public ResponseEntity<Object> adminUpdate(@PathVariable("id") Integer id, @RequestBody Usuario request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) throws Exception {
        DataResponse response = new DataResponse();
        try {
            if (id != request.getId()) {
                response.setEstado(0);
                response.setMensaje("ID no coincide con el usuario a actualizar");
                response.setException("No se pudo actualizar el registro");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Validar que el usuario autenticado tiene rol de ADMIN
            String rolUsuarioActual = extractRoleFromToken(authHeader);
            if(!"ADMINISTRADOR".equals(rolUsuarioActual)) {
                response.setEstado(0);
                response.setMensaje("No tiene permisos de administrador para realizar esta acción");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            
            Usuario usuarioActualizado = service.adminUpdate(request);
            if (usuarioActualizado != null) {
                response.setEstado(1);
                response.setMensaje("Usuario actualizado por administrador exitosamente");
                response.setDato(usuarioActualizado);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setEstado(0);
                response.setMensaje("Usuario no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setEstado(0);
            response.setMensaje("Error al actualizar usuario: " + e.getMessage());
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/findByUsername/{username}")
    public ResponseEntity<Object> findByUsername(@PathVariable("username") String username) throws Exception {
        DataResponse response = new DataResponse();
        try {
            Usuario usuario = service.findByUsername(username);
            if (usuario != null) {
                response.setDato(usuario);
                response.setEstado(1);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMensaje("Usuario no encontrado");
                response.setEstado(0);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setException(e.getMessage());
            response.setEstado(0);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
