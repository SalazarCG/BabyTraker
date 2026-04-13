package com.salazar.babytraker.features.inicio.domain.usecase

import com.salazar.babytraker.core.domain.model.ResumenDia
import com.salazar.babytraker.core.domain.model.TipoPanal
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Caso de Uso Crítico: Combina datos de Tomas y Pañales para un día específico
 * para devolver un objeto de dominio ResumenDia.
 */
class GetResumenDiarioUseCase @Inject constructor(
    private val repository: InicioRepository
) {
    operator fun invoke(fechaDia: Long): Flow<Result<ResumenDia>> {
        return combine(
            repository.getTomasPorDia(fechaDia),
            repository.getPanalesPorDia(fechaDia)
        ) { tomasResult, panalesResult ->
            
            val tomas = tomasResult.getOrNull() ?: emptyList()
            val panales = panalesResult.getOrNull() ?: emptyList()

            val resumen = ResumenDia(
                fechaDia = fechaDia,
                totalTomas = tomas.size,
                totalPipis = panales.count { it.tipo == TipoPanal.PIPI },
                totalCacas = panales.count { it.tipo == TipoPanal.CACA },
                totalMixtos = panales.count { it.tipo == TipoPanal.MIXTO }
            )
            
            Result.success(resumen)
        }
    }
}
