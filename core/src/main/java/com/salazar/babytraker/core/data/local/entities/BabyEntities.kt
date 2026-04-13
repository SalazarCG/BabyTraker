package com.salazar.babytraker.core.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tomas",
    indices = [Index(value = ["fechaDia"])]
)
data class TomaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val fechaDia: Long, // Normalizado a las 00:00:00
    val tipo: String, // PECHO, BIBERON
    val cantidad: Int? = null, // ml si es biberon
    val nota: String? = null
)

@Entity(
    tableName = "panales",
    indices = [Index(value = ["fechaDia"])]
)
data class PanalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val fechaDia: Long, // Normalizado a las 00:00:00
    val tipo: String, // PIPI, CACA, MIXTO
    val nota: String? = null
)

@Entity(tableName = "citas_medicas")
data class CitaMedicaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaHora: Long,
    val titulo: String,
    val descripcion: String? = null,
    val pediatra: String? = null
)
