package com.gunashekar.mynotes.listeners

import com.gunashekar.mynotes.entities.Note

interface NotesListener {
    fun onNoteClicked(note: Note,position: Int)
}