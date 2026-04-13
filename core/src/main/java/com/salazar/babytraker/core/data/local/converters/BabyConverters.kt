package com.salazar.babytraker.core.data.local.converters

import androidx.room.TypeConverter

class BabyConverters {
    // Si decidimos usar Enums en las Entities, los convertimos aquí.
    // Por simplicidad y rendimiento en Room, a veces se usan Strings directamente en la Entity,
    // pero para un código profesional, usamos Enums en Dominio y los mapeamos.
    
    // De momento, las Entities usan Strings. Si el usuario prefiere Enums en DB:
    /*
    @TypeConverter
    fun fromTipoAlimentacion(value: TipoAlimentacion): String = value.name
    
    @TypeConverter
    fun toTipoAlimentacion(value: String): TipoAlimentacion = TipoAlimentacion.valueOf(value)
    */
}
