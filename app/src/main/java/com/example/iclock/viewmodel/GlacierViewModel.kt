package com.example.iclock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class GlacierEntry(
    val year: Int = 0,
    val ndsi: Float = 0f,
    val temp_celsius: Float = 0f,
    val area_km2: Float = 0f,
    val vol_km3: Float = 0f,
    val alert: String = "STABLE"
)

data class GlacierState(
    val isLoading: Boolean = true,
    val glacierName: String = "",
    val country: String = "",
    val yearCritical: Int = 0,
    val modelUsed: String = "",
    val rfR2: Float = 0f,
    val historical: List<GlacierEntry> = emptyList(),
    val predictions: List<GlacierEntry> = emptyList(),
    val error: String? = null
)

class GlacierViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _state = MutableStateFlow(GlacierState())
    val state: StateFlow<GlacierState> = _state

    init {
        loadGlacierData()
    }

    private fun loadGlacierData() {
        viewModelScope.launch {
            try {
                val doc = db.collection("glaciers")
                    .document("mer_de_glace")
                    .get()
                    .await()

                val name        = doc.getString("glacier") ?: ""
                val country     = doc.getString("country") ?: ""
                val yearCrit    = (doc.getLong("year_glacier_critical") ?: 0).toInt()
                val modelUsed   = doc.getString("model_used") ?: ""
                val metrics     = doc.get("model_metrics") as? Map<*, *>
                val rfR2        = (metrics?.get("random_forest_r2") as? Double)?.toFloat() ?: 0f

                val historicalSnap = db.collection("glaciers")
                    .document("mer_de_glace")
                    .collection("historical")
                    .orderBy("year")
                    .get()
                    .await()

                val historical = historicalSnap.documents.map { d ->
                    GlacierEntry(
                        year        = (d.getLong("year") ?: 0).toInt(),
                        ndsi        = (d.getDouble("ndsi") ?: 0.0).toFloat(),
                        temp_celsius = (d.getDouble("temp_celsius") ?: 0.0).toFloat(),
                        area_km2    = (d.getDouble("area_km2") ?: 0.0).toFloat(),
                        vol_km3     = (d.getDouble("vol_km3") ?: 0.0).toFloat(),
                        alert       = d.getString("alert") ?: "STABLE"
                    )
                }

                val predictionsSnap = db.collection("glaciers")
                    .document("mer_de_glace")
                    .collection("predictions")
                    .orderBy("year")
                    .get()
                    .await()

                val predictions = predictionsSnap.documents.map { d ->
                    GlacierEntry(
                        year        = (d.getLong("year") ?: 0).toInt(),
                        ndsi        = (d.getDouble("ndsi") ?: 0.0).toFloat(),
                        temp_celsius = (d.getDouble("temp_celsius") ?: 0.0).toFloat(),
                        area_km2    = (d.getDouble("area_km2") ?: 0.0).toFloat(),
                        vol_km3     = (d.getDouble("vol_km3") ?: 0.0).toFloat(),
                        alert       = d.getString("alert") ?: "STABLE"
                    )
                }

                _state.value = GlacierState(
                    isLoading   = false,
                    glacierName = name,
                    country     = country,
                    yearCritical = yearCrit,
                    modelUsed   = modelUsed,
                    rfR2        = rfR2,
                    historical  = historical,
                    predictions = predictions
                )

            } catch (e: Exception) {
                _state.value = GlacierState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}