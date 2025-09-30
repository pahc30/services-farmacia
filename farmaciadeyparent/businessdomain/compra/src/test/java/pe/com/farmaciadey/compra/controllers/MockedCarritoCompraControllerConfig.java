package pe.com.farmaciadey.compra.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MockedCarritoCompraControllerConfig {
    @Autowired
    private CarritoCompraService carritoCompraService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Bean
    public CarritoCompraController carritoCompraController() {
        return new CarritoCompraController(carritoCompraService, webClientBuilder) {
            @Override
            public pe.com.farmaciadey.compra.models.responses.ProductoResponse getProducto(Integer id) {
                pe.com.farmaciadey.compra.models.responses.ProductoResponse producto = new pe.com.farmaciadey.compra.models.responses.ProductoResponse();
                producto.setId(1);
                producto.setStock(10);
                producto.setPrecio(1.0);
                producto.setCodigo("A");
                producto.setNombre("Test");
                producto.setUrl("url");
                producto.setCategoria("cat");
                return producto;
            }
        };
    }
}
