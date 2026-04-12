package com.cardioo_sport.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cardioo_sport.data.db.dao.SportMeasurementDao
import com.cardioo_sport.data.db.dao.UserDao
import com.cardioo_sport.data.db.entity.SportMeasurementEntity
import com.cardioo_sport.data.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        SportMeasurementEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun measurementDao(): SportMeasurementDao
}
