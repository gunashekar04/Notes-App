package com.gunashekar.mynotes.activities

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gunashekar.mynotes.R
import com.gunashekar.mynotes.adapters.NotesAdapter
import com.gunashekar.mynotes.database.NotesDatabase
import com.gunashekar.mynotes.entities.Note
import com.gunashekar.mynotes.listeners.NotesListener
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NotesListener {

    val REQUEST_CODE_ADD_NOTE = 1
    val REQUEST_CODE_UPDATE_NOTE = 2
    val REQUEST_CODE_SHOW_NOTES = 3
    var noteClickedPosition = -1
    val notesList: ArrayList<Note> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageAddNoteMain.setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }

        rvNotes.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        rvNotes.adapter = NotesAdapter(notesList, this)
        getNotes(REQUEST_CODE_SHOW_NOTES, false)
    }

    override fun onNoteClicked(note: Note, position: Int) {
        noteClickedPosition = position
        val intent = Intent(applicationContext, CreateNoteActivity::class.java)
        intent.putExtra("isViewOrUpdate",true)
        intent.putExtra("note", note as Serializable)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE)
    }

    private fun getNotes(requestCode: Int, isNoteDeleted: Boolean){
        class GetNotes : AsyncTask<Void, Void, List<Note>>(){
            override fun doInBackground(vararg params: Void?): List<Note> {
                return NotesDatabase
                    .getDatabase(applicationContext)
                    .noteDao().getAllNotes()
            }

            override fun onPostExecute(notes: List<Note>) {
                super.onPostExecute(notes)
//                if(notesList.isEmpty()){
//                    notesList.addAll(notes)
//                    rvNotes.adapter!!.notifyDataSetChanged()
//                } else {
//                    notesList.add(0, notes[0])
//                    rvNotes.adapter!!.notifyItemInserted(0)
//                }
//                rvNotes.smoothScrollToPosition(0)
                 if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                     notesList.addAll(notes)
                     rvNotes.adapter!!.notifyDataSetChanged()
                 } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                     notesList.add(0, notes[0])
                     rvNotes.adapter!!.notifyItemInserted(0)
                     rvNotes.smoothScrollToPosition(0)
                 } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                     notesList.removeAt(noteClickedPosition)
                     if (isNoteDeleted) {
                         rvNotes.adapter!!.notifyItemRemoved(noteClickedPosition)
                         rvNotes.adapter!!.notifyDataSetChanged()
                     }else {
                         notesList.add(noteClickedPosition, notes[noteClickedPosition])
                         rvNotes.adapter!!.notifyItemChanged(noteClickedPosition)
                     }
                 }
            }
        }
        GetNotes().execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == Activity.RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE, false)
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false))
            }
        }
    }
}