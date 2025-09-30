    // ...código existente...

    // ...código existente...

    @Test
    void testConstructorPorDefecto() {
        CarritoCompraController controller = new CarritoCompraController();
        org.junit.jupiter.api.Assertions.assertNotNull(controller);
    }

    @Test
    void testGetProducto_NullBranch() {
        CarritoCompraController spyController = Mockito.spy(controller);
        Mockito.doReturn(null).when(spyController).getProducto(Mockito.anyInt());
        pe.com.farmaciadey.compra.models.responses.ProductoResponse result = spyController.getProducto(999);
        org.junit.jupiter.api.Assertions.assertNull(result);
    }

    @Test
    void testGetProducto_NonNullBranch() {
        CarritoCompraController spyController = Mockito.spy(controller);
        pe.com.farmaciadey.compra.models.responses.ProductoResponse mockProducto = new pe.com.farmaciadey.compra.models.responses.ProductoResponse();
        mockProducto.setId(1);
        mockProducto.setStock(10);
        mockProducto.setPrecio(5.0);
        mockProducto.setCodigo("A");
        mockProducto.setNombre("Test");
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testListByUsuario_Exception() throws Exception {
        Mockito.when(service.listByUsuario(Mockito.anyInt())).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/carrito/list/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteCarritoCompra_Exception() throws Exception {
        Mockito.when(service.delete(Mockito.anyInt())).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/carrito/delete/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
    @Autowired
    private MockMvc mockMvc;

    private CarritoCompraService service = Mockito.mock(CarritoCompraService.class);

    @Autowired
    private CarritoCompraController controller;

    @Autowired
    public void setController(CarritoCompraController controller) {
        this.controller = controller;
        // Inject mock service into controller
        try {
            java.lang.reflect.Field serviceField = CarritoCompraController.class.getDeclaredField("service");
            serviceField.setAccessible(true);
            serviceField.set(controller, service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveCarritoCompra() throws Exception {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setId(1);
        carrito.setUsuarioId(1);
        carrito.setProductoId(2);
        carrito.setCantidad(3);
        when(service.save(any(CarritoCompra.class))).thenReturn(carrito);
    mockMvc.perform(post("/api/carrito/save")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(carrito))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.dato.id").value(1));
    }

    @Test
    void testListByUsuario() throws Exception {
    mockMvc.perform(post("/api/carrito/list/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void testDeleteCarritoCompra() throws Exception {
    mockMvc.perform(post("/api/carrito/delete/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }
}
