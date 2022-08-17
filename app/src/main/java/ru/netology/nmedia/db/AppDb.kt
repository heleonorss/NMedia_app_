package ru.netology.nmedia.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Ключевая идея: мы хотим получить синглтон на все приложение для доступа к БД (через метод getInstance),
// Это некая симуляция нашей БД, кот принимает на вход другую БД
// Когда мы создадим экземпляр AppDb, то получим доступ к постДао и создадим репозиторий
// Это некая точка входа в Room

@Database(
    entities = [PostEntity::class],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context)
                    .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context, AppDb::class.java, "app.db" // имя файла, куда созданная БД сохранится
            ).allowMainThreadQueries() // разрешить обрабатывать запросы на основном потоке
                .build()
    }
}