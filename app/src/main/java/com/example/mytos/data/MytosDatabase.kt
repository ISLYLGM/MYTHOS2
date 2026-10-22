
package com.example.mytos.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(
    entities = [
        UsuarioEntity::class,
        ProdutoEntity::class,
        PedidoEntity::class,
        ItemPedidoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MytosDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun produtoDao(): ProdutoDao

    abstract fun pedidoDao(): PedidoDao

    companion object {

        @Volatile
        private var INSTANCE: MytosDatabase? = null

        fun getDatabase(context: Context): MytosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MytosDatabase::class.java,
                    "mytos_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}