package pe.com.farmaciadey.usuario.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Usuario update(Usuario o){
        Optional<Usuario> res = repository.findById(o.getId());
        if(res.isPresent()){
            Usuario usuario = res.get();
            usuario.setNombres(o.getNombres());
            usuario.setApellidos(o.getApellidos());
            usuario.setTelefono(o.getTelefono());
            usuario.setEmail(o.getEmail());
            usuario.setDireccion(o.getDireccion());
            usuario.setRol(o.getRol());
            
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            usuario.setPassword(passwordEncoder.encode(o.getPassword()));

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
}
