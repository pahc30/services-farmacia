package pe.com.farmaciadey.compra.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.farmaciadey.compra.models.CarritoCompra;
import pe.com.farmaciadey.compra.repository.CarritoCompraRepository;

@Service
public class CarritoCompraService {
    @Autowired
    private CarritoCompraRepository repository;

    public CarritoCompra save(CarritoCompra o){
        CarritoCompra carritoCompra = repository.find(o.getUsuarioId(), o.getProductoId());
        if(carritoCompra == null){
            return repository.save(o);
        }

        carritoCompra.setCantidad(o.getCantidad());
        return repository.save(carritoCompra);
    }

    public List<CarritoCompra> listByUsuario(Integer usuarioId){
        return repository.list(usuarioId);
    }

    public boolean delete(Integer id){
        Optional<CarritoCompra> res = repository.findById(id);
        if(res.isPresent()){
            CarritoCompra carrito = res.get();
            carrito.setEliminado(1);
            repository.save(carrito);
            return true;
        }
        return false;
    }
}
