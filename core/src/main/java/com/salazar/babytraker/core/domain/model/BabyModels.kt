package com.salazar.babytraker.core.domain.model

enum class TipoAlimentacion { PECHO, BIBERON }
enum class TipoPanal { PIPI, CACA, MIXTO }

data class Toma(
    val id: Long = 0,
    val timestamp: Long,
    val fechaDia: Long,
    val tipo: TipoAlimentacion,
    val cantidad: Int? = null,
    val nota: String? = null
)

data class Panal(
    val id: Long = 0,
    val timestamp: Long,
    val fechaDia: Long,
    val tipo: TipoPanal,
    val nota: String? = null
)

data class ResumenDia(
    val fechaDia: Long,
    val totalTomas: Int,
    val totalPipis: Int,
    val totalCacas: Int,
    val totalMixtos: Int
)
