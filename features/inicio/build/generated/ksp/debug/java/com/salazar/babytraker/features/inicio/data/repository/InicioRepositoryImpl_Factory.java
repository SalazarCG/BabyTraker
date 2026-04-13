package com.salazar.babytraker.features.inicio.data.repository;

import com.salazar.babytraker.core.data.local.dao.BabyDao;
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
public final class InicioRepositoryImpl_Factory implements Factory<InicioRepositoryImpl> {
  private final Provider<BabyDao> babyDaoProvider;

  public InicioRepositoryImpl_Factory(Provider<BabyDao> babyDaoProvider) {
    this.babyDaoProvider = babyDaoProvider;
  }

  @Override
  public InicioRepositoryImpl get() {
    return newInstance(babyDaoProvider.get());
  }

  public static InicioRepositoryImpl_Factory create(Provider<BabyDao> babyDaoProvider) {
    return new InicioRepositoryImpl_Factory(babyDaoProvider);
  }

  public static InicioRepositoryImpl newInstance(BabyDao babyDao) {
    return new InicioRepositoryImpl(babyDao);
  }
}
