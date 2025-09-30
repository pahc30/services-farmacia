package pe.com.farmaciadey.compra.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import com.fasterxml.jackson.databind.JsonNode;

import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import pe.com.farmaciadey.compra.utils.Utils;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class CompraIntegrationService {

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

    public ProductoResponse getProducto(Integer productoId) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(Utils.BASE_URL_PRODUCTO)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", Utils.BASE_URL_PRODUCTO))
                .build();

        JsonNode block = build.method(org.springframework.http.HttpMethod.POST).uri("/api/producto/find/" + productoId)
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

    public String getMetodoPago(Integer metodoId) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(Utils.BASE_URL_METODO_PAGO)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", Utils.BASE_URL_METODO_PAGO))
                .build();

        JsonNode block = build.method(org.springframework.http.HttpMethod.POST).uri("/api/metodopago/find/" + metodoId)
                .retrieve().bodyToMono(JsonNode.class).block();

        if (block.get("dato") == null) {
            return null;
        }

        return block.get("dato").get("tipo").asText();
    }

    public void updateStock(Integer productoId, Integer cantidadComprada){
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(Utils.BASE_URL_PRODUCTO)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", Utils.BASE_URL_PRODUCTO))
                .build();

        JsonNode block = build.method(org.springframework.http.HttpMethod.POST).uri("/api/producto/change/" + productoId).bodyValue(cantidadComprada)
                .retrieve().bodyToMono(JsonNode.class).block();

        if (block.get("dato") == null) {
            throw new RuntimeException("No se pudo actualizar el stock de un producto");
        }
    }
}