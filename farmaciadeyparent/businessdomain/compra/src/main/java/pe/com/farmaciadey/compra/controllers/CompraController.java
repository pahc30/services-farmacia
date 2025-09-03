package pe.com.farmaciadey.compra.controllers;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.farmaciadey.compra.models.Compra;
import pe.com.farmaciadey.compra.models.responses.DataResponse;
import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import pe.com.farmaciadey.compra.services.CompraService;
import pe.com.farmaciadey.compra.utils.Utils;

import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/api/compra")
public class CompraController {

    @Autowired
   private CompraService service;

   @Autowired
   private CarritoCompraService carritoCompraService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    @PostMapping(value = "/save")
    public ResponseEntity<Object> save(@RequestBody Compra request) throws Exception {
        DataResponse response = new DataResponse();
        try {

            request.setCodigo(UUID.randomUUID().toString());
            request.setSubtotal(0.0);
            request.getDetalleCompra().forEach(x -> {               
                
                ProductoResponse producto = getProducto(x.getProductoId());
                if (producto == null) {
                    throw new RuntimeException("El producto no existe");
                }
    
                if (producto.getStock() < x.getCantidad()) {
                    throw new RuntimeException("El producto no cuenta con stock suficiente");
                }

                x.setCompra(request);
                x.setPrecio(producto.getPrecio());
                x.setSubtotal(producto.getPrecio() * x.getCantidad());
                request.setSubtotal(request.getSubtotal() + x.getSubtotal());
            });

            response.setDato(service.save(request));

            request.getDetalleCompra().forEach(x -> { 
                updateStock(x.getProductoId(), x.getCantidad());
                carritoCompraService.delete(x.getCarritoCompraId());
            });

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/list/{usuarioId}")
    public ResponseEntity<Object> list(@PathVariable("usuarioId") Integer usuarioId) throws Exception {
        DataResponse response = new DataResponse();
        try {
            List<Compra> compras = service.listByUsuario(usuarioId);
            compras.forEach(o -> {
                o.setMetodoPago(getMetodoPago(o.getMetodoPagoId()));

                o.getDetalleCompra().forEach(d -> {
                    d.setProducto(getProducto(d.getProductoId()));
                });
            });
            response.setDato(compras);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ProductoResponse getProducto(Integer productoId) {
        // String baseUrlService = "http://BUSINESSDOMAIN-PRODUCT/producto";
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(Utils.BASE_URL_PRODUCTO)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", Utils.BASE_URL_PRODUCTO))
                .build();

        JsonNode block = build.method(HttpMethod.POST).uri("/api/producto/find/" + productoId)
                .retrieve().bodyToMono(JsonNode.class).block();

        if (block.get("dato") == null) {
            return null;
        }

        ProductoResponse o = new ProductoResponse();
        o.setId(block.get("dato").get("id").asInt());
        o.setPrecio(block.get("dato").get("precio").asDouble());
        o.setStock(block.get("dato").get("stock").asInt());
        o.setCodigo(block.get("dato").get("codigo").asText());
        o.setNombre(block.get("dato").get("nombre").asText());
        o.setUrl(block.get("dato").get("url").asText());
        o.setCategoria(block.get("dato").get("categoria").get("nombre").asText());

        return o;
    }

    private String getMetodoPago(Integer metodoId) {
        // String baseUrlService = "http://BUSINESSDOMAIN-PRODUCT/producto";
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(Utils.BASE_URL_METODO_PAGO)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", Utils.BASE_URL_METODO_PAGO))
                .build();

        JsonNode block = build.method(HttpMethod.POST).uri("/api/metodopago/find/" + metodoId)
                .retrieve().bodyToMono(JsonNode.class).block();

        if (block.get("dato") == null) {
            return null;
        }

        return block.get("dato").get("tipo").asText();
    }

    private void updateStock(Integer productoId, Integer cantidadComprada){
        // String baseUrlService = "http://BUSINESSDOMAIN-PRODUCT/producto";
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(Utils.BASE_URL_PRODUCTO)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", Utils.BASE_URL_PRODUCTO))
                .build();

        JsonNode block = build.method(HttpMethod.POST).uri("/api/producto/change/" + productoId).bodyValue(cantidadComprada)
                .retrieve().bodyToMono(JsonNode.class).block();

        if (block.get("dato") == null) {
            throw new RuntimeException("No se pudo actualizar el stock de un producto");
        }
    }

}
