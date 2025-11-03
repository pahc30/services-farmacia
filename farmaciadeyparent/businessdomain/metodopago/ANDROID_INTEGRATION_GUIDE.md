# 📱 Integración de Pagos con Android - Farmacia DeY

## ✅ Estado Actual
Según el README del proyecto, ya existe una aplicación Android en `farmacia-android/`, pero **aún NO está configurada para pagos**.

## 🎯 Lo que necesitas hacer para Android

### 1. Configuración de Stripe en Android

#### Agregar Dependencia en `build.gradle` (app)
```gradle
dependencies {
    // Stripe Android SDK
    implementation 'com.stripe:stripe-android:20.+'
    
    // Para las llamadas HTTP al backend
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
}
```

#### Permisos en `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 2. Configuración de Stripe en la App

#### Archivo `strings.xml`
```xml
<resources>
    <!-- Stripe Keys - CAMBIAR PARA PRODUCCIÓN -->
    <string name="stripe_publishable_key">pk_test_tu_clave_publica_aqui</string>
    
    <!-- URLs del Backend -->
    <string name="api_base_url">http://10.0.2.2:9000</string> <!-- Para emulador -->
    <!-- Para dispositivo real: http://TU_IP:9000 -->
</resources>
```

### 3. Inicializar Stripe en la Aplicación

#### `Application.kt` o `Application.java`
```kotlin
class FarmaciaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Stripe
        PaymentConfiguration.init(
            applicationContext,
            getString(R.string.stripe_publishable_key)
        )
    }
}
```

### 4. Crear Servicio para llamadas al Backend

#### `ApiService.kt`
```kotlin
interface ApiService {
    @POST("metodopago/crear-intent")
    suspend fun crearPaymentIntent(@Body request: PaymentIntentRequest): PaymentIntentResponse
    
    @POST("metodopago/confirmar/{transaccionId}")
    suspend fun confirmarPago(@Path("transaccionId") transaccionId: Long): ConfirmacionResponse
}

data class PaymentIntentRequest(
    val compraId: Long,
    val monto: Double,
    val moneda: String = "usd",
    val descripcion: String
)

data class PaymentIntentResponse(
    val success: Boolean,
    val transaccionId: Long,
    val clientSecret: String,
    val message: String
)
```

### 5. Activity de Pago

#### `PaymentActivity.kt`
```kotlin
class PaymentActivity : AppCompatActivity() {
    private lateinit var stripe: Stripe
    private lateinit var paymentSheet: PaymentSheet
    private var clientSecret: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Stripe
        stripe = Stripe(
            this,
            PaymentConfiguration.getInstance(this).publishableKey
        )
        
        // Configurar PaymentSheet
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        
        // Crear Payment Intent
        crearPaymentIntent()
    }
    
    private fun crearPaymentIntent() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.crearPaymentIntent(
                    PaymentIntentRequest(
                        compraId = intent.getLongExtra("compraId", 0),
                        monto = intent.getDoubleExtra("monto", 0.0),
                        descripcion = "Compra en Farmacia DeY"
                    )
                )
                
                if (response.success) {
                    clientSecret = response.clientSecret
                    presentarPago()
                } else {
                    // Manejar error
                    showError(response.message)
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }
    
    private fun presentarPago() {
        clientSecret?.let { secret ->
            paymentSheet.presentWithPaymentIntent(
                secret,
                PaymentSheet.Configuration(
                    merchantDisplayName = "Farmacia DeY",
                    customer = null, // O configurar customer si tienes
                    googlePay = PaymentSheet.GooglePayConfiguration(
                        environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
                        countryCode = "PE"
                    )
                )
            )
        }
    }
    
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                showSuccess("¡Pago completado exitosamente!")
                finish()
            }
            is PaymentSheetResult.Canceled -> {
                showInfo("Pago cancelado")
            }
            is PaymentSheetResult.Failed -> {
                showError("Error en el pago: ${paymentSheetResult.error.message}")
            }
        }
    }
}
```

### 6. Integración en el Flujo de Compra

#### En tu Activity de Checkout
```kotlin
// Botón de pagar
btnPagar.setOnClickListener {
    val intent = Intent(this, PaymentActivity::class.java).apply {
        putExtra("compraId", compraActual.id)
        putExtra("monto", compraActual.total)
    }
    startActivity(intent)
}
```

## 🚀 Próximos Pasos para Android

### 1. Verificar la App Existente
```bash
# Navegar a la carpeta de Android (si existe)
cd farmacia-android/

# Verificar estructura
ls -la
```

### 2. Si NO existe la app Android, crearla:
1. Crear nuevo proyecto en Android Studio
2. Seleccionar API 26+ (Android 8.0)
3. Configurar para conectar con los microservicios

### 3. Configurar Red para Desarrollo
```xml
<!-- En android/app/src/main/res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain> <!-- Emulador -->
        <domain includeSubdomains="true">192.168.1.xxx</domain> <!-- Tu IP -->
    </domain-config>
</network-security-config>
```

### 4. Testing en Desarrollo
```kotlin
// Usar datos de prueba de Stripe
val testCards = mapOf(
    "visa" to "4242424242424242",
    "mastercard" to "5555555555554444",
    "declined" to "4000000000000002"
)
```

## 📋 Lista de Verificación

### Para Desarrollo ✅
- [ ] Agregar dependencia de Stripe Android
- [ ] Configurar clave pública de prueba
- [ ] Crear Activity de pago
- [ ] Integrar con flujo de compra
- [ ] Probar con tarjetas de prueba

### Para Producción ⚠️
- [ ] Cambiar a claves de producción de Stripe
- [ ] Configurar URLs de producción
- [ ] Habilitar ProGuard/R8
- [ ] Firmar APK para release
- [ ] Probar en dispositivos reales

## 🔧 Troubleshooting Común

### Error de Red
```kotlin
// Verificar conexión con el backend
private fun testConnection() {
    lifecycleScope.launch {
        try {
            val response = ApiClient.apiService.healthCheck()
            Log.d("Network", "Backend conectado: $response")
        } catch (e: Exception) {
            Log.e("Network", "Error conectando backend: ${e.message}")
        }
    }
}
```

### Logs para Debug
```kotlin
// En tu Activity de pago
Log.d("Payment", "Iniciando pago para compra: $compraId")
Log.d("Payment", "Monto: $monto")
Log.d("Payment", "Client Secret: $clientSecret")
```