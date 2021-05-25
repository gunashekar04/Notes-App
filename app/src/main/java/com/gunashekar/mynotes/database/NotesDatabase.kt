package com.gunashekar.mynotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gunashekar.mynotes.dao.NoteDao
import com.gunashekar.mynotes.entities.Note
import java.security.AccessControlContext

@Database(entities = arrayOf(Note::class), version = 1, exportSchema = false)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao
    companion object{
        @Volatile
        private var notesDatabase: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {
            val tempInstance = notesDatabase
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotesDatabase::class.java,
                        "notes_db"
                ).build()
                notesDatabase = instance
                return instance
            }
        }
    }
}



//abstract class NotesDatabase: RoomDatabase() {
//
//    @Volatile
//    private var notesDatabase: NotesDatabase? = null
//
//    fun getDatabase(context: Context): NotesDatabase{
//        if (notesDatabase == null){
//            notesDatabase = Room.databaseBuilder(
//                    context.applicationContext,
//                    NotesDatabase::class.java,
//                    "notes_db"
//            ).build()
//        }
//        return notesDatabase as NotesDatabase
//    }
//        abstract fun noteDao(): NoteDao
//}



