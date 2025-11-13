package pe.com.farmaciadey.usuario.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import pe.com.farmaciadey.usuario.models.Usuario;
import pe.com.farmaciadey.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> list(){
        return repository.list();
    }

    public Usuario save(Usuario o) throws Exception{

        if(repository.findByUsername(o.getUsername()) != null){
            throw new Exception("El Username " + o.getUsername() + " ya existe.");
        }

        if(repository.findByIdentificacion(o.getIdentificacion()) != null){
            throw new Exception("La Identificacion " + o.getIdentificacion() + " ya existe.");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        o.setPassword(passwordEncoder.encode(o.getPassword()));
        return repository.save(o);
    }

    public Usuario update(Usuario o) throws Exception {
        Optional<Usuario> res = repository.findById(o.getId());
        if(res.isPresent()){
            Usuario usuario = res.get();
            
            // 🔒 REGLA DE NEGOCIO: La identificación NO se puede cambiar
            // Solo debería permitirse en casos excepcionales por un administrador
            if(!usuario.getIdentificacion().equals(o.getIdentificacion())) {
                throw new Exception("La identificación no se puede modificar por seguridad. Contacte al administrador si es necesario.");
            }
            
            // 🔒 REGLA DE NEGOCIO: Username solo puede cambiarlo admin o el mismo usuario
            if(!usuario.getUsername().equals(o.getUsername())) {
                Usuario existingUsername = repository.findByUsername(o.getUsername());
                if(existingUsername != null && !existingUsername.getId().equals(o.getId())) {
                    throw new Exception("El Username " + o.getUsername() + " ya existe.");
                }
                // Nota: En una implementación completa, aquí validaríamos permisos
                usuario.setUsername(o.getUsername());
            }
            
            // ✅ Campos que SÍ se pueden actualizar libremente
            usuario.setNombres(o.getNombres());
            usuario.setApellidos(o.getApellidos());
            usuario.setTelefono(o.getTelefono());
            usuario.setEmail(o.getEmail());
            usuario.setDireccion(o.getDireccion());
            
            // 🔒 REGLA DE NEGOCIO: Solo admin puede cambiar roles
            // Por ahora permitimos el cambio, pero en producción debería validarse
            if(!usuario.getRol().equals(o.getRol())) {
                // TODO: Validar que quien hace la request tiene permisos de admin
                usuario.setRol(o.getRol());
            }
            
            // Solo actualizar la contraseña si se proporciona una nueva
            if(o.getPassword() != null && !o.getPassword().isEmpty()){
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                usuario.setPassword(passwordEncoder.encode(o.getPassword()));
            }

            return repository.save(usuario);
        }
        return null;
    }

    /**
     * 🔑 Método especial para administradores: permite cambiar identificación
     * Solo debe usarse en casos excepcionales (errores de captura, cambios legales, etc.)
     */
    public Usuario adminUpdate(Usuario o) throws Exception {
        Optional<Usuario> res = repository.findById(o.getId());
        if(res.isPresent()){
            Usuario usuario = res.get();
            
            // 🔍 Validar username único (si ha cambiado)
            if(!usuario.getUsername().equals(o.getUsername())) {
                Usuario existingUsername = repository.findByUsername(o.getUsername());
                if(existingUsername != null && !existingUsername.getId().equals(o.getId())) {
                    throw new Exception("El Username " + o.getUsername() + " ya existe.");
                }
                usuario.setUsername(o.getUsername());
            }
            
            // 🔑 ADMIN PUEDE cambiar identificación (con validación)
            if(!usuario.getIdentificacion().equals(o.getIdentificacion())) {
                Usuario existingIdentificacion = repository.findByIdentificacion(o.getIdentificacion());
                if(existingIdentificacion != null && !existingIdentificacion.getId().equals(o.getId())) {
                    throw new Exception("La Identificación " + o.getIdentificacion() + " ya existe.");
                }
                usuario.setIdentificacion(o.getIdentificacion());
            }
            
            // Actualizar todos los campos
            usuario.setNombres(o.getNombres());
            usuario.setApellidos(o.getApellidos());
            usuario.setTelefono(o.getTelefono());
            usuario.setEmail(o.getEmail());
            usuario.setDireccion(o.getDireccion());
            usuario.setRol(o.getRol());
            
            // Solo actualizar la contraseña si se proporciona una nueva
            if(o.getPassword() != null && !o.getPassword().isEmpty()){
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                usuario.setPassword(passwordEncoder.encode(o.getPassword()));
            }

            return repository.save(usuario);
        }
        return null;
    }

    public boolean delete(Integer id){
        Optional<Usuario> res = repository.findById(id);
        if(res.isPresent()){
            Usuario usuario = res.get();
            usuario.setEliminado(1);
            repository.save(usuario);
            return true;
        }
        return false;
    }

    public Usuario find(Integer id){
        return repository.find(id);
    }

    public Usuario findByUsername(String username){
        return repository.findByUsername(username);
    }

    public Usuario saveWithAuth(Usuario usuario) throws Exception {
        // Primero guardamos el usuario en el servicio de usuarios
        Usuario usuarioCreado = save(usuario);
        
        // Luego creamos las credenciales en el servicio de auth
        try {
            WebClient webClient = WebClient.builder().build();
            
            // Preparar los datos para el servicio auth
            String authRequest = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"rol\":\"%s\"}",
                usuario.getUsername(),
                usuario.getPassword(), // Enviar password sin encriptar para auth
                usuario.getRol()
            );
            
            // Llamar al servicio auth
            webClient.post()
                .uri("http://farmacia-auth:7011/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            return usuarioCreado;
        } catch (Exception e) {
            // Si falla la creación en auth, eliminar el usuario creado
            delete(usuarioCreado.getId());
            throw new Exception("Error al crear credenciales de autenticación: " + e.getMessage());
        }
    }
}
