package com.cardioo_sport.di

import android.content.Context
import androidx.room.Room
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.cardioo_sport.data.db.AppDatabase
import com.cardioo_sport.data.db.Migrations
import com.cardioo_sport.data.db.dao.SportMeasurementDao
import com.cardioo_sport.data.db.dao.UserDao
import com.cardioo_sport.data.repository.MeasurementRepositoryImpl
import com.cardioo_sport.data.repository.UserRepositoryImpl
import com.cardioo_sport.domain.repository.MeasurementRepository
import com.cardioo_sport.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { ctx.preferencesDataStoreFile("cardioo_sport_session.preferences_pb") },
        )

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "cardioo_sport.db")
            .addMigrations(Migrations.MIGRATION_1_2)
            .build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideMeasurementDao(db: AppDatabase): SportMeasurementDao = db.measurementDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds abstract fun bindUserRepo(impl: UserRepositoryImpl): UserRepository
    @Binds abstract fun bindMeasurementRepo(impl: MeasurementRepositoryImpl): MeasurementRepository
}

