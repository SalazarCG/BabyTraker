package com.salazar.babytraker.core.data.mapper

import com.salazar.babytraker.core.data.local.entities.PanalEntity
import com.salazar.babytraker.core.data.local.entities.TomaEntity
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.TipoAlimentacion
import com.salazar.babytraker.core.domain.model.TipoPanal
import com.salazar.babytraker.core.domain.model.Toma

fun TomaEntity.toDomain() = Toma(
    id = id,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = TipoAlimentacion.valueOf(tipo),
    cantidad = cantidad,
    nota = nota
)

fun Toma.toEntity() = TomaEntity(
    id = id,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = tipo.name,
    cantidad = cantidad,
    nota = nota
)

fun PanalEntity.toDomain() = Panal(
    id = id,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = TipoPanal.valueOf(tipo),
    nota = nota
)

fun Panal.toEntity() = PanalEntity(
    id = id,
    timestamp = timestamp,
    fechaDia = fechaDia,
    tipo = tipo.name,
    nota = nota
)
