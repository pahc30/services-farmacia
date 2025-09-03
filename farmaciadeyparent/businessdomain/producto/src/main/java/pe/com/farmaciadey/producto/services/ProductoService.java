package pe.com.farmaciadey.producto.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.farmaciadey.producto.models.Producto;
import pe.com.farmaciadey.producto.repository.ProductoRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository repository;

    public List<Producto> list(){
        return repository.list();
    }

    public Producto save(Producto o){
        return repository.save(o);
    }

    public Producto update(Producto o){
        Optional<Producto> res = repository.findById(o.getId());
        if(res.isPresent()){
            Producto producto = res.get();
            producto.setNombre(o.getNombre());
            producto.setDescripcion(o.getDescripcion());
            producto.setPrecio(o.getPrecio());
            producto.setStock(o.getStock());
            producto.setCategoria(o.getCategoria());
            producto.setUrl(o.getUrl());

            return repository.save(producto);
        }
        return null;
    }

    public Producto change(Integer id, Integer cantidadComprada){
        Optional<Producto> res = repository.findById(id);
        if(res.isPresent()){
            Producto producto = res.get();
            producto.setStock(producto.getStock() - cantidadComprada);

            return repository.save(producto);
        }
        return null;
    }

    public boolean delete(Integer id){
        Optional<Producto> res = repository.findById(id);
        if(res.isPresent()){
            Producto producto = res.get();
            producto.setEliminado(1);
            repository.save(producto);
            return true;
        }
        return false;
    }

    public Producto find(Integer id){
        return repository.find(id);
    }
}

