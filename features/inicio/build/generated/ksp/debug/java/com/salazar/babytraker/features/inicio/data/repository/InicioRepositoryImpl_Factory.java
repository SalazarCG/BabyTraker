package com.salazar.babytraker.features.inicio.data.repository;

import com.salazar.babytraker.core.data.local.dao.BabyDao;
import com.salazar.babytraker.core.data.local.preferences.BabyPreferences;
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

  private final Provider<BabyPreferences> babyPreferencesProvider;

  public InicioRepositoryImpl_Factory(Provider<BabyDao> babyDaoProvider,
      Provider<BabyPreferences> babyPreferencesProvider) {
    this.babyDaoProvider = babyDaoProvider;
    this.babyPreferencesProvider = babyPreferencesProvider;
  }

  @Override
  public InicioRepositoryImpl get() {
    return newInstance(babyDaoProvider.get(), babyPreferencesProvider.get());
  }

  public static InicioRepositoryImpl_Factory create(Provider<BabyDao> babyDaoProvider,
      Provider<BabyPreferences> babyPreferencesProvider) {
    return new InicioRepositoryImpl_Factory(babyDaoProvider, babyPreferencesProvider);
  }

  public static InicioRepositoryImpl newInstance(BabyDao babyDao, BabyPreferences babyPreferences) {
    return new InicioRepositoryImpl(babyDao, babyPreferences);
  }
}
