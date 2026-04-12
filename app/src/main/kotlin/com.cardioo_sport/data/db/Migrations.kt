package com.cardioo_sport.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    /**
     * v1 -> v2
     * - user: support multiple accounts (auto id + name)
     * - sport_measurement: link each row to a userId (FK)
     */
    val MIGRATION_1_2: Migration =
        object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `height` REAL NOT NULL,
                        `heightUnit` TEXT NOT NULL,
                        `weightUnit` TEXT NOT NULL,
                        `dateOfBirthIso` TEXT,
                        `gender` TEXT,
                        `stepLength` REAL NOT NULL
                    )
                    """.trimIndent(),
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `sport_measurement` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userId` INTEGER NOT NULL,
                        `timestampEpochMillis` INTEGER NOT NULL,
                        `morning_steps` INTEGER,
                        `noon_steps` INTEGER,
                        `running_distance` REAL,
                        `cycling_distance` REAL,
                        `stretching` INTEGER NOT NULL DEFAULT 0,
                        `notes` TEXT,
                        FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )

                db.execSQL("CREATE INDEX IF NOT EXISTS `index_sport_measurement_timestampEpochMillis` ON `sport_measurement` (`timestampEpochMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_sport_measurement_userId` ON `sport_measurement` (`userId`)")
            }
        }
}
