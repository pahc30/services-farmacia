package pe.com.farmaciadey.producto.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.farmaciadey.producto.models.Categoria;
import pe.com.farmaciadey.producto.repository.CategoriaRepository;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository repository;

    public List<Categoria> list(){
        return repository.list();
    }

    public Categoria save(Categoria o){
        return repository.save(o);
    }

    public Categoria update(Categoria o){
        Optional<Categoria> res = repository.findById(o.getId());
        if(res.isPresent()){
            Categoria metodo = res.get();
            metodo.setNombre(o.getNombre());
            metodo.setDescripcion(o.getDescripcion());

            return repository.save(metodo);
        }
        return null;
    }

    public boolean delete(Integer id){
        Optional<Categoria> res = repository.findById(id);
        if(res.isPresent()){
            Categoria metodo = res.get();
            metodo.setEliminado(1);
            repository.save(metodo);
            return true;
        }
        return false;
    }

    public Categoria find(Integer id){
        return repository.find(id);
    }
}

