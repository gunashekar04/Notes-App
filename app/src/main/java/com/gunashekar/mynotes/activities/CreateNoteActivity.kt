package com.gunashekar.mynotes.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gunashekar.mynotes.R
import com.gunashekar.mynotes.database.NotesDatabase
import com.gunashekar.mynotes.entities.Note
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.layout_miscellaneous.*
import kotlinx.android.synthetic.main.layout_miscellaneous.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class CreateNoteActivity : AppCompatActivity() {

    private lateinit var selectedNoteColor: String
    private lateinit var selectedImagePath: String
    private var alreadyAvailableNote: Note? = null
    private var dialogAddURL: AlertDialog? = null
    private var dialogDeleteNote: AlertDialog? = null
    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private val REQUEST_CODE_SELECT_IMAGE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        inputNoteTitle.requestFocus()

        imageBack.setOnClickListener {
            onBackPressed()
        }

        textDateTime.text = SimpleDateFormat ("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())

        imageSave.setOnClickListener {
            saveNote()
        }

        selectedNoteColor = "#313131"
        selectedImagePath = ""

        if(intent.getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = intent.getSerializableExtra("note")!! as Note
            setViewOrUpdateNote()
        }

        imageRemoveWebURL.setOnClickListener {
            textWebURL.text = null
            layoutWebURL.visibility = View.GONE
        }

        imageRemoveImage.setOnClickListener {
            imageNote.setImageBitmap(null)
            imageNote.visibility = View.GONE
            imageRemoveImage.visibility = View.GONE
            selectedImagePath = ""
        }

        initMiscellaneous()
        setSubtitleIndicatorColor()
    }

    private fun setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote!!.title)
        inputNoteSubtitle.setText(alreadyAvailableNote!!.subtitle)
        inputNote.setText(alreadyAvailableNote!!.noteText)
        textDateTime.text = alreadyAvailableNote!!.dateTime

        if (alreadyAvailableNote!!.imagePath != null && !alreadyAvailableNote!!.imagePath!!.trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote!!.imagePath))
            imageNote.visibility = View.VISIBLE
            imageRemoveImage.visibility = View.VISIBLE
            selectedImagePath = alreadyAvailableNote!!.imagePath!!
        }

        if(alreadyAvailableNote!!.webLink != null && !alreadyAvailableNote!!.webLink!!.trim().isEmpty()){
            textWebURL.text = alreadyAvailableNote!!.webLink
            layoutWebURL.visibility = View.VISIBLE
        }
    }

    private fun saveNote(){

        val noteTitle = inputNoteTitle.text.toString().trim()
        val noteSubtitle: String = inputNoteSubtitle.text.toString().trim()
        val noteText = inputNote.text.toString().trim()
        val dateTime = textDateTime.text.toString().trim()

        if(noteTitle.isEmpty()){
            inputNoteTitle.error = "Title required"
            inputNoteTitle.requestFocus()
            return
        } else if(noteSubtitle.isEmpty()){
            inputNoteSubtitle.error = "Subtitle required"
            inputNoteSubtitle.requestFocus()
            return
        } else if(noteText.isEmpty()){
            inputNote.error = "Notes text is required"
            inputNote.requestFocus()
            return
        }

        val note = Note(noteTitle,dateTime,noteSubtitle,noteText)
        note.color = selectedNoteColor
        note.imagePath = selectedImagePath

        if(layoutWebURL.visibility == View.VISIBLE){
            note.webLink = textWebURL.text.toString()
        }

        if (alreadyAvailableNote != null) {
            note.id = alreadyAvailableNote!!.id
        }

        class SaveNote : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                NotesDatabase.getDatabase(applicationContext).noteDao().insertNote(note)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                Toast.makeText(this@CreateNoteActivity, "$noteTitle added", Toast.LENGTH_SHORT).show()
                val intent = Intent()
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
        SaveNote().execute()
    }

    private fun initMiscellaneous() {
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(layoutMiscellaneous)
        textMiscellaneous.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        viewColor1.setOnClickListener{
            selectedNoteColor = "#313131"
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        viewColor2.setOnClickListener{
            selectedNoteColor = "#317a55"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        viewColor3.setOnClickListener{
            selectedNoteColor = "#FF4842"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        viewColor4.setOnClickListener{
            selectedNoteColor = "#3A52Fc"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        viewColor5.setOnClickListener{
            selectedNoteColor = "#000000"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(R.drawable.ic_done)
            setSubtitleIndicatorColor()
        }

        if (alreadyAvailableNote != null && alreadyAvailableNote!!.color != null && !alreadyAvailableNote!!.color!!.trim().isEmpty()){
            when(alreadyAvailableNote!!.color){
                "#317a55" -> layoutMiscellaneous.viewColor2.performClick()
                "#FF4842" -> layoutMiscellaneous.viewColor3.performClick()
                "#3A52Fc" -> layoutMiscellaneous.viewColor4.performClick()
                "#000000" -> layoutMiscellaneous.viewColor5.performClick()
            }
        }

        layoutAddImage.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                selectImage()
            }
        }

        layoutAddUrl.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showAddURLDialog()
        }

        if (alreadyAvailableNote != null) {
            layoutDeleteNote.visibility = View.VISIBLE
            layoutDeleteNote.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showDeleteNoteDialog()
            }
        }
    }

    private fun showDeleteNoteDialog() {
        var localDialogDeleteNote = this.dialogDeleteNote
        if (localDialogDeleteNote == null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    findViewById(R.id.layoutDeleteNoteContainer)
            )
            builder.setView(view)
            localDialogDeleteNote = builder.create()
            if(localDialogDeleteNote.window != null) {
                localDialogDeleteNote.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            view.findViewById<TextView>(R.id.textDeleteNote).setOnClickListener {
                class DeleteNoteTask : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg params: Void?): Void? {
                        NotesDatabase.getDatabase(applicationContext).noteDao().deleteNote(alreadyAvailableNote!!)
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        val intent = Intent()
                        intent.putExtra("isNoteDeleted", true)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                DeleteNoteTask().execute()
            }

            view.findViewById<TextView>(R.id.textCancel).setOnClickListener {
                localDialogDeleteNote.dismiss()
            }
        }
        localDialogDeleteNote!!.show()
    }


    private fun setSubtitleIndicatorColor(){
        val gradientDrawable : GradientDrawable = viewSubtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor))
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage()
            } else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK){
            if(data != null){
                val selectedImageUri = data.data
                if(selectedImageUri != null){
                    try {
                        val inputStream = contentResolver.openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageNote.setImageBitmap(bitmap)
                        imageNote.visibility = View.VISIBLE
                        imageRemoveImage.visibility = View.VISIBLE
                        selectedImagePath = getPathFromUri(selectedImageUri)!!
                    } catch (exception: Exception){
                        Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        val filePath: String?
        val cursor = contentResolver
                .query(contentUri,null,null,null,null)
        if (cursor == null){
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    private fun showAddURLDialog() {
        var localDialogAddURL = this.dialogAddURL
        if(localDialogAddURL == null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    findViewById(R.id.layoutAddUrlContainer)
            )
            builder.setView(view)

            localDialogAddURL = builder.create()
            if(localDialogAddURL.window != null) {
                localDialogAddURL.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            val inputURL: EditText = view.findViewById(R.id.inputURL)
            inputURL.requestFocus()

            view.findViewById<TextView>(R.id.textAdd).setOnClickListener {
                if(inputURL.text.toString().trim().isEmpty()){
                    Toast.makeText(this@CreateNoteActivity, "Enter URL", Toast.LENGTH_SHORT).show()
                }else if (!Patterns.WEB_URL.matcher(inputURL.text.toString()).matches()){
                    Toast.makeText(this@CreateNoteActivity, "Enter valid URL", Toast.LENGTH_SHORT).show()
                }else {
                    textWebURL.text = inputURL.text.toString()
                    layoutWebURL.visibility = View.VISIBLE
                    localDialogAddURL.dismiss()
                }
            }
            view.findViewById<TextView>(R.id.textCancel).setOnClickListener {
                localDialogAddURL.dismiss()
            }
        }
        localDialogAddURL!!.show()
    }
}