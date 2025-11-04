package com.farmaciadey.data.repository

import com.farmaciadey.data.api.ApiClient
import com.farmaciadey.data.models.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarritoRepository {
    
    private val apiService = ApiClient.createService()

    suspend fun limpiarCarrito(usuarioId: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.clearCarrito(usuarioId)
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Error al limpiar carrito: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}