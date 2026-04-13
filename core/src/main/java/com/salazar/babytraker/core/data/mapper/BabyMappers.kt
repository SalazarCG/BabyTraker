package com.salazar.babytraker.core.data.mapper

import com.salazar.babytraker.core.data.local.entities.BabyEntity
import com.salazar.babytraker.core.data.local.entities.PanalEntity
import com.salazar.babytraker.core.data.local.entities.TomaEntity
import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.TipoAlimentacion
import com.salazar.babytraker.core.domain.model.TipoPanal
import com.salazar.babytraker.core.domain.model.Toma

fun BabyEntity.toDomain() = Baby(
    id = id,
    nombre = nombre,
    fechaNacimiento = fechaNacimiento,
    fotoUri = fotoUri
)

fun Baby.toEntity() = BabyEntity(
    id = id,
    nombre = nombre,
    fechaNacimiento = fechaNacimiento,
    fotoUri = fotoUri
)

fun TomaEntity.toDomain() = Toma(
    id = id,
    babyId = babyId,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = TipoAlimentacion.valueOf(tipo),
    cantidad = cantidad,
    nota = nota
)

fun Toma.toEntity() = TomaEntity(
    id = id,
    babyId = babyId,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = tipo.name,
    cantidad = cantidad,
    nota = nota
)

fun PanalEntity.toDomain() = Panal(
    id = id,
    babyId = babyId,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = TipoPanal.valueOf(tipo),
    nota = nota
)

fun Panal.toEntity() = PanalEntity(
    id = id,
    babyId = babyId,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = tipo.name,
    nota = nota
)
