package com.salazar.babytraker.features.inicio.presentation.viewmodel;

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
  private final Provider<InicioRepository> repositoryProvider;

  private final Provider<GetResumenDiarioUseCase> getResumenDiarioUseCaseProvider;

  public InicioViewModel_Factory(Provider<InicioRepository> repositoryProvider,
      Provider<GetResumenDiarioUseCase> getResumenDiarioUseCaseProvider) {
    this.repositoryProvider = repositoryProvider;
    this.getResumenDiarioUseCaseProvider = getResumenDiarioUseCaseProvider;
  }

  @Override
  public InicioViewModel get() {
    return newInstance(repositoryProvider.get(), getResumenDiarioUseCaseProvider.get());
  }

  public static InicioViewModel_Factory create(Provider<InicioRepository> repositoryProvider,
      Provider<GetResumenDiarioUseCase> getResumenDiarioUseCaseProvider) {
    return new InicioViewModel_Factory(repositoryProvider, getResumenDiarioUseCaseProvider);
  }

  public static InicioViewModel newInstance(InicioRepository repository,
      GetResumenDiarioUseCase getResumenDiarioUseCase) {
    return new InicioViewModel(repository, getResumenDiarioUseCase);
  }
}
