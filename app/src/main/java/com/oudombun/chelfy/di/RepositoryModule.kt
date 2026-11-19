package com.oudombun.chelfy.di

import com.oudombun.chelfy.data.repository.DefaultRecipeRepository
import com.oudombun.chelfy.data.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRecipeRepository(repository: DefaultRecipeRepository): RecipeRepository
}
