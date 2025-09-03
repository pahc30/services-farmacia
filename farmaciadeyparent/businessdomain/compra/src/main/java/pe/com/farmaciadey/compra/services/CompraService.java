package pe.com.farmaciadey.compra.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.farmaciadey.compra.models.Compra;
import pe.com.farmaciadey.compra.repository.CompraRepository;


@Service
public class CompraService {
    @Autowired
    private CompraRepository repository;

    public Compra save(Compra o){
        o.setFecha(new Date());
        o.setTotal(o.getSubtotal() + (o.getSubtotal() * o.getIgv()));
        return repository.save(o);
    }

    public List<Compra> listByUsuario(Integer usuarioId){
        return repository.list(usuarioId);
    }
}
