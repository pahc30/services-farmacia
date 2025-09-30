
package pe.com.farmaciadey.compra.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import pe.com.farmaciadey.compra.models.responses.ProductoResponse;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompraIntegrationServiceTest {
    @Mock
    private Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    @SuppressWarnings("rawtypes")
    private RequestHeadersSpec requestHeadersSpec;
    @Mock
    private ResponseSpec responseSpec;
    @InjectMocks
    private CompraIntegrationService service;

    @Test
    void testGetProductoReturnsResponse() {
        ObjectNode datoNode = JsonNodeFactory.instance.objectNode();
        datoNode.put("id", 1);
        datoNode.put("precio", 10.0);
        datoNode.put("stock", 5);
        datoNode.put("codigo", "A1");
        datoNode.put("nombre", "Paracetamol");
        datoNode.put("url", "img.jpg");
        ObjectNode categoriaNode = JsonNodeFactory.instance.objectNode();
        categoriaNode.put("nombre", "Medicamento");
        datoNode.set("categoria", categoriaNode);
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.set("dato", datoNode);
        when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultUriVariables(anyMap())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    when(webClient.method(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(rootNode));
        ProductoResponse resp = service.getProducto(1);
        assertNotNull(resp);
        assertEquals(1, resp.getId());
        assertEquals(10.0, resp.getPrecio());
        assertEquals(5, resp.getStock());
        assertEquals("A1", resp.getCodigo());
        assertEquals("Paracetamol", resp.getNombre());
        assertEquals("img.jpg", resp.getUrl());
        assertEquals("Medicamento", resp.getCategoria());
    }

    @Test
    void testGetProductoReturnsNull() {
    ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
    // Simula que el método get("dato") retorna null
    JsonNode mockNode = mock(JsonNode.class);
    when(mockNode.get("dato")).thenReturn(null);
    when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
    when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.defaultUriVariables(anyMap())).thenReturn(webClientBuilder);
    when(webClientBuilder.build()).thenReturn(webClient);
    when(webClient.method(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(mockNode));
    ProductoResponse resp = service.getProducto(1);
    assertNull(resp);
    }

    @Test
    void testGetMetodoPagoReturnsTipo() {
        ObjectNode datoNode = JsonNodeFactory.instance.objectNode();
        datoNode.put("tipo", "Efectivo");
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.set("dato", datoNode);
        when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultUriVariables(anyMap())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    when(webClient.method(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(rootNode));
        String tipo = service.getMetodoPago(1);
        assertEquals("Efectivo", tipo);
    }

    @Test
    void testGetMetodoPagoReturnsNull() {
    JsonNode mockNode = mock(JsonNode.class);
    when(mockNode.get("dato")).thenReturn(null);
    when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
    when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.defaultUriVariables(anyMap())).thenReturn(webClientBuilder);
    when(webClientBuilder.build()).thenReturn(webClient);
    when(webClient.method(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(mockNode));
    String tipo = service.getMetodoPago(1);
    assertNull(tipo);
    }

    @Test
    void testUpdateStockSuccess() {
        ObjectNode datoNode = JsonNodeFactory.instance.objectNode();
        datoNode.put("ok", true);
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        rootNode.set("dato", datoNode);
        when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultUriVariables(anyMap())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    when(webClient.method(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    //noinspection unchecked
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(rootNode));
        assertDoesNotThrow(() -> service.updateStock(1, 5));
    }

    @Test
    void testUpdateStockThrowsException() {
    JsonNode mockNode = mock(JsonNode.class);
    when(mockNode.get("dato")).thenReturn(null);
    when(webClientBuilder.clientConnector(any())).thenReturn(webClientBuilder);
    when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
    when(webClientBuilder.defaultUriVariables(anyMap())).thenReturn(webClientBuilder);
    when(webClientBuilder.build()).thenReturn(webClient);
    when(webClient.method(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    //noinspection unchecked
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(mockNode));
    Exception ex = assertThrows(RuntimeException.class, () -> service.updateStock(1, 5));
    assertTrue(ex.getMessage().contains("No se pudo actualizar"));
    }
}
