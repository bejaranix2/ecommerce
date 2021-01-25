package com.bejaranix.ecommerce.database

import androidx.room.*


@Entity
data class Search(
        @ColumnInfo(name = "q") val search: String
){
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

}

@Dao
interface SearchDao {
    @Query("SELECT * FROM search")
    fun getAll(): List<Search>


    @Query("SELECT * FROM search WHERE q = :search ")
    fun findByName(search: String): List<Search>

    @Insert
    fun insertAll(vararg users: Search)

    @Delete
    fun delete(user: Search)
}

@Database(entities = arrayOf(Search::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
}