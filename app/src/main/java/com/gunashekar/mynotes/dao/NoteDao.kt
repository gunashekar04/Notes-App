package com.gunashekar.mynotes.dao

import androidx.room.*
import com.gunashekar.mynotes.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

}