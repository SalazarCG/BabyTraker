package com.salazar.babytraker.core.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bebes")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val fechaNacimiento: Long,
    val fotoUri: String? = null
)

@Entity(
    tableName = "tomas",
    indices = [Index(value = ["fechaDia"]), Index(value = ["babyId"])]
)
data class TomaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val timestamp: Long,
    val fechaDia: Long, // Normalizado a las 00:00:00
    val tipo: String, // PECHO, BIBERON
    val cantidad: Int? = null, // ml si es biberon
    val nota: String? = null
)

@Entity(
    tableName = "panales",
    indices = [Index(value = ["fechaDia"]), Index(value = ["babyId"])]
)
data class PanalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val timestamp: Long,
    val fechaDia: Long, // Normalizado a las 00:00:00
    val tipo: String, // PIPI, CACA, MIXTO
    val nota: String? = null
)

@Entity(
    tableName = "citas_medicas",
    indices = [Index(value = ["babyId"])]
)
data class CitaMedicaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val fechaHora: Long,
    val titulo: String,
    val descripcion: String? = null,
    val pediatra: String? = null
)

@Entity(
    tableName = "diarios_diarios",
    indices = [Index(value = ["fechaDia", "babyId"], unique = true)]
)
data class DailyJournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val fechaDia: Long,
    val comentario: String? = null,
    val fotoUri: String? = null
)
