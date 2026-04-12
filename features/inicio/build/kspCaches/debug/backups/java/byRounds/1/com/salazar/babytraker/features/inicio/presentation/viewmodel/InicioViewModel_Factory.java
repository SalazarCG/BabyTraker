package com.salazar.babytraker.features.inicio.presentation.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
  @Override
  public InicioViewModel get() {
    return newInstance();
  }

  public static InicioViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InicioViewModel newInstance() {
    return new InicioViewModel();
  }

  private static final class InstanceHolder {
    private static final InicioViewModel_Factory INSTANCE = new InicioViewModel_Factory();
  }
}
