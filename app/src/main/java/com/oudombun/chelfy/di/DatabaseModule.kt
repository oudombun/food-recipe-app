package com.oudombun.chelfy.di

import android.content.Context
import androidx.room.Room
import com.oudombun.chelfy.data.local.database.RecipesDao
import com.oudombun.chelfy.data.local.database.RecipesDatabase
import com.oudombun.chelfy.core.common.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,RecipesDatabase::class.java, DATABASE_NAME)
        .build()

    @Singleton
    @Provides
    fun provideRecipeDao(database: RecipesDatabase): RecipesDao {
       return database.recipesDao()
    }
}