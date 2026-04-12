package com.salazar.babytraker.features.inicio.data.repository;

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
public final class InicioRepositoryImpl_Factory implements Factory<InicioRepositoryImpl> {
  @Override
  public InicioRepositoryImpl get() {
    return newInstance();
  }

  public static InicioRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InicioRepositoryImpl newInstance() {
    return new InicioRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final InicioRepositoryImpl_Factory INSTANCE = new InicioRepositoryImpl_Factory();
  }
}
