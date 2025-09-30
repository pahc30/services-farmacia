package pe.com.farmaciadey.compra.controllers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

import pe.com.farmaciadey.compra.models.CarritoCompra;
import pe.com.farmaciadey.compra.models.responses.CarritoResponse;
import pe.com.farmaciadey.compra.models.responses.DataResponse;
import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import pe.com.farmaciadey.compra.services.CarritoCompraService;
import pe.com.farmaciadey.compra.utils.Utils;

import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/api/carrito")

public class CarritoCompraController {

    public CarritoCompraController() {}

    public CarritoCompraController(CarritoCompraService service, WebClient.Builder webClientBuilder) {
        this.service = service;
        this.webClientBuilder = webClientBuilder;
    }

    @Autowired
    CarritoCompraService service;

    @Autowired
    WebClient.Builder webClientBuilder;

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

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataResponse> save(@RequestBody CarritoCompra request) throws Exception {
        DataResponse response = new DataResponse();
        try {

            ProductoResponse producto = getProducto(request.getProductoId());

            if (producto == null) {
                response.setException("El producto no existe");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (producto.getStock() < request.getCantidad()) {
                response.setException("El producto no cuenta con stock suficiente");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            response.setDato(service.save(request));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/list/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataResponse> list(@PathVariable("usuarioId") Integer usuarioId) throws Exception {
        DataResponse response = new DataResponse();
        try {
            List<CarritoCompra> data = service.listByUsuario(usuarioId);
            List<CarritoResponse> dato = new ArrayList<>();

            for (CarritoCompra carritoCompra : data) {
                CarritoResponse item = new CarritoResponse();
                item.setId(carritoCompra.getId());
                item.setCantidad(carritoCompra.getCantidad());
                item.setProducto(getProducto(carritoCompra.getProductoId()));
                dato.add(item);
            }

            response.setDato(dato);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataResponse> delete(@PathVariable("id") Integer id) throws Exception {
        DataResponse response = new DataResponse();
        try {
            response.setDato(service.delete(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setException(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    ProductoResponse getProducto(Integer id) {
        // String baseUrlService = "http://BUSINESSDOMAIN-PRODUCT/producto";
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(Utils.BASE_URL_PRODUCTO)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", Utils.BASE_URL_PRODUCTO))
                .build();

        JsonNode block = build.method(HttpMethod.POST).uri("/api/producto/find/" + id)
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

}
