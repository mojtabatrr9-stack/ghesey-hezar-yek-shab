package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "custom_levels")
data class CustomLevelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val originCity: String,
    val destinationCity: String,
    val biomeName: String,
    val wavesCount: Int,
    val blueprintJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "player_progress")
data class PlayerProgressEntity(
    @PrimaryKey val id: Int = 1,
    val goldCoins: Int = 350,
    val silkCount: Int = 35,
    val saffronCount: Int = 25,
    val highestUnlockedLevel: Int = 1,
    val completedStarsJson: String = "{}",
    val wagonArmorLevel: Int = 1,
    val camelEquipLevel: Int = 1,
    val permanentGuardLevel: Int = 1,
    val heroRankLevel: Int = 1,
    val endlessBestWave: Int = 0,
    val bossRaidClears: Int = 0,
    val soundEnabled: Boolean = true
)

@Dao
interface CaravanDao {
    @Query("SELECT * FROM custom_levels ORDER BY updatedAt DESC")
    fun observeCustomLevels(): Flow<List<CustomLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomLevel(entity: CustomLevelEntity): Long

    @Query("DELETE FROM custom_levels WHERE id = :id")
    suspend fun deleteCustomLevel(id: Long)

    @Query("SELECT COUNT(*) FROM custom_levels")
    suspend fun getCustomLevelCount(): Int

    @Query("SELECT * FROM player_progress WHERE id = 1")
    fun observePlayerProgress(): Flow<PlayerProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE id = 1")
    suspend fun getPlayerProgressOnce(): PlayerProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayerProgress(progress: PlayerProgressEntity)
}

@Database(
    entities = [CustomLevelEntity::class, PlayerProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CaravanDatabase : RoomDatabase() {
    abstract fun caravanDao(): CaravanDao

    companion object {
        @Volatile
        private var INSTANCE: CaravanDatabase? = null

        fun getInstance(context: Context): CaravanDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CaravanDatabase::class.java,
                    "caravan_silk_road.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
