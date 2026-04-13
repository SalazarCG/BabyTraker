package com.salazar.babytraker.core.domain.model

enum class TipoAlimentacion { PECHO, BIBERON }
enum class TipoPanal { PIPI, CACA, MIXTO }

data class Baby(
    val id: Long = 0,
    val nombre: String,
    val fechaNacimiento: Long,
    val fotoUri: String? = null
)

data class Toma(
    val id: Long = 0,
    val babyId: Long,
    val timestamp: Long,
    val fechaDia: Long,
    val tipo: TipoAlimentacion,
    val cantidad: Int? = null,
    val nota: String? = null
)

data class Panal(
    val id: Long = 0,
    val babyId: Long,
    val timestamp: Long,
    val fechaDia: Long,
    val tipo: TipoPanal,
    val nota: String? = null
)

data class ResumenDia(
    val fechaDia: Long,
    val babyId: Long,
    val totalTomas: Int,
    val totalPipis: Int,
    val totalCacas: Int,
    val totalMixtos: Int,
    val comentario: String? = null,
    val fotoUri: String? = null
)
