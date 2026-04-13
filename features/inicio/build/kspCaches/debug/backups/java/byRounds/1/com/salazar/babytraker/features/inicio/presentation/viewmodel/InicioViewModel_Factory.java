package com.salazar.babytraker.features.inicio.presentation.viewmodel;

import com.salazar.babytraker.core.data.local.preferences.BabyPreferences;
import com.salazar.babytraker.core.domain.repository.BabyRepository;
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository;
import com.salazar.babytraker.features.inicio.domain.usecase.GetResumenDiarioUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class InicioViewModel_Factory implements Factory<InicioViewModel> {
  private final Provider<BabyRepository> babyRepositoryProvider;

  private final Provider<InicioRepository> inicioRepositoryProvider;

  private final Provider<BabyPreferences> babyPreferencesProvider;

  private final Provider<GetResumenDiarioUseCase> getResumenDiarioUseCaseProvider;

  public InicioViewModel_Factory(Provider<BabyRepository> babyRepositoryProvider,
      Provider<InicioRepository> inicioRepositoryProvider,
      Provider<BabyPreferences> babyPreferencesProvider,
      Provider<GetResumenDiarioUseCase> getResumenDiarioUseCaseProvider) {
    this.babyRepositoryProvider = babyRepositoryProvider;
    this.inicioRepositoryProvider = inicioRepositoryProvider;
    this.babyPreferencesProvider = babyPreferencesProvider;
    this.getResumenDiarioUseCaseProvider = getResumenDiarioUseCaseProvider;
  }

  @Override
  public InicioViewModel get() {
    return newInstance(babyRepositoryProvider.get(), inicioRepositoryProvider.get(), babyPreferencesProvider.get(), getResumenDiarioUseCaseProvider.get());
  }

  public static InicioViewModel_Factory create(Provider<BabyRepository> babyRepositoryProvider,
      Provider<InicioRepository> inicioRepositoryProvider,
      Provider<BabyPreferences> babyPreferencesProvider,
      Provider<GetResumenDiarioUseCase> getResumenDiarioUseCaseProvider) {
    return new InicioViewModel_Factory(babyRepositoryProvider, inicioRepositoryProvider, babyPreferencesProvider, getResumenDiarioUseCaseProvider);
  }

  public static InicioViewModel newInstance(BabyRepository babyRepository,
      InicioRepository inicioRepository, BabyPreferences babyPreferences,
      GetResumenDiarioUseCase getResumenDiarioUseCase) {
    return new InicioViewModel(babyRepository, inicioRepository, babyPreferences, getResumenDiarioUseCase);
  }
}
