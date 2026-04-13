package com.salazar.babytraker.features.inicio.domain.usecase

import com.salazar.babytraker.core.data.local.dao.BabyDao
import com.salazar.babytraker.core.domain.model.ResumenDia
import com.salazar.babytraker.core.domain.model.TipoPanal
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetResumenDiarioUseCase @Inject constructor(
    private val repository: InicioRepository,
    private val babyDao: BabyDao
) {
    operator fun invoke(fechaDia: Long, babyId: Long): Flow<Result<ResumenDia>> {
        val tomasFlow = repository.getTomasPorDia(fechaDia, babyId)
        val panalesFlow = repository.getPanalesPorDia(fechaDia, babyId)
        val journalFlow = babyDao.getJournalByDay(babyId, fechaDia)

        return combine(tomasFlow, panalesFlow, journalFlow) { tomasResult, panalesResult, journal ->
            val tomas = tomasResult.getOrNull() ?: emptyList()
            val panales = panalesResult.getOrNull() ?: emptyList()

            val resumen = ResumenDia(
                fechaDia = fechaDia,
                babyId = babyId,
                totalTomas = tomas.size,
                totalPipis = panales.count { it.tipo == TipoPanal.PIPI },
                totalCacas = panales.count { it.tipo == TipoPanal.CACA },
                totalMixtos = panales.count { it.tipo == TipoPanal.MIXTO },
                comentario = journal?.comentario,
                fotoUri = journal?.fotoUri
            )
            
            Result.success(resumen)
        }
    }
}
