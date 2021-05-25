package com.gunashekar.mynotes.adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.gunashekar.mynotes.R
import com.gunashekar.mynotes.entities.Note
import com.gunashekar.mynotes.listeners.NotesListener
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.item_container_note.view.*
import java.util.*

class NotesAdapter(private val notes: List<Note>, private val notesListener: NotesListener) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private lateinit var timer: Timer
    private lateinit var notesSource: List<Note>

    class NoteViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_container_note, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.view.apply {
            textTitle.text = notes[position].title
            textSubtitle.text = notes[position].subtitle
            textDateTime.text = notes[position].dateTime
        }
        val gradientDrawable : GradientDrawable = holder.view.layoutNote.background as GradientDrawable
        if (notes[position].color != null){
            gradientDrawable.setColor(Color.parseColor(notes[position].color))
        } else {
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }

        if (notes[position].imagePath != null){
            holder.view.roundedImage.apply {
                setImageBitmap(BitmapFactory.decodeFile(notes[position].imagePath))
                visibility = View.VISIBLE
            }
        } else {
            holder.view.roundedImage.visibility = View.GONE
        }

        holder.view.layoutNote.setOnClickListener {
            notesListener.onNoteClicked(notes[position], position)
        }
    }

}