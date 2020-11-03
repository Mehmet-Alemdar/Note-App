package com.mehmetalemdar.basitnottut;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mehmetalemdar.basitnottut.Model.NoteModel;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity2 extends AppCompatActivity {

    EditText editTextTitle;
    EditText editTextNote;

    ImageButton imageButtonSave;
    ImageButton imageButtonBack;
    ImageButton imageButtonDelete;

    SQLiteDatabase database;
    public int noteId;

    String info;
    String noteTitleStr;
    String noteStr;

    //editText ten alınan verilen stringlere atandı


    // ses ile yazı yazmak için
    final int REQ_CODE_SPEECH=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        imageButtonBack=findViewById(R.id.imageButtonBack);
        imageButtonDelete=findViewById(R.id.imageButtonDelete);
        imageButtonSave=findViewById(R.id.imageButtonSave);

        editTextTitle=findViewById(R.id.editTextTitle);
        editTextNote=findViewById(R.id.editTextNote);


        // veri tabanındaki verileri id numarasına göre geri getiriyor
        Intent intent=getIntent();
        info=intent.getStringExtra("info");
        if(info.matches("new")){
            imageButtonSave.setImageResource(R.drawable.ic_outline_save_24);
        }
        else {
            noteId=intent.getIntExtra("noteId",1);
            imageButtonSave.setImageResource(R.drawable.ic_baseline_system_update_alt_24);
            try {
                database=this.openOrCreateDatabase("Notes",MODE_PRIVATE,null);
                Cursor cursor=database.rawQuery("SELECT * FROM notes WHERE id=?",new String[] {String.valueOf(noteId)});

                int noteTitleIx=cursor.getColumnIndex("notetitlesql");
                int noteIx=cursor.getColumnIndex("note");

                while (cursor.moveToNext()){
                    noteTitleStr=cursor.getString(noteTitleIx);
                    noteStr=cursor.getString(noteIx);
                    editTextTitle.setText(cursor.getString(noteTitleIx));
                    editTextNote.setText(cursor.getString(noteIx));
                }
                cursor.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // backİmage kodları
    public void back(View view) {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //ses ile yazı yazmak için
    public void voiceClick(View view){
        voice();
    }

    public void voice(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"bir şeyler söyleyin...");
        try {
            startActivityForResult(intent,REQ_CODE_SPEECH);

        }catch (ActivityNotFoundException a){
            a.getStackTrace();
            a.getMessage();
        }
    }

    //ses ile yazı yazmak için
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_CODE_SPEECH:
            {
                if(resultCode==RESULT_OK && data!=null){

                    ArrayList<String> res=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String voiceText=res.get(0);
                    editTextNote.setText(editTextNote.getText()+" "+voiceText);
                }
                break;
            }
        }
    }

    //sil butonuna tıklandığında
    public void deleteBtn(View view){

        if(editTextNote.getText().toString().trim().equals("") && editTextTitle.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"SİLİNECEK NOT YOK",Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity2.this, R.style.Theme_AppCompat_Dialog_Alert)
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setTitle("Sil")
                    .setMessage("Silmek İstediğine Emin Misin?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteData();
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            Dialog dialog = alert.create();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_design);
            dialog.show();
        }
    }

    //verileri silmek için metod
    public void deleteData(){

        try {
            database=getApplicationContext().openOrCreateDatabase("Notes",MODE_PRIVATE,null);
            database.execSQL("DELETE FROM notes WHERE id=?",new String[] {String.valueOf(noteId)});
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Intent intent=new Intent(MainActivity2.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(info.matches("new")){
            Toast.makeText(getApplicationContext(),"HİÇ VAR OLMADI BİLE",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"SİLİNDİ",Toast.LENGTH_SHORT).show();
        }
        startActivity(intent);

    }

    // girilen veriler veritabınana ekleniyor (Save ya da Update butonuna tıklandığında)
    public void saveOrUpdateClick(View view){
        String toastText;
        String toastPositiveButtonText;
        String toastNoteText;

        if(info.matches("new")){
            toastText="Başlık Olmadan Kaydedilsin Mi?";
            toastNoteText="Not Olmadan Kaydedilsin Mi?";
            toastPositiveButtonText="Save";

        }else{
            toastText="Başlık Olmadan Güncellensin Mi?";
            toastNoteText="Not Olmadan Güncellensin Mi?";
            toastPositiveButtonText="Up Date";
        }

        if(editTextTitle.getText().toString().trim().equals("")&& editTextNote.getText().toString().trim().equals("")){
            Intent intent=new Intent(MainActivity2.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(editTextTitle.getText().toString().trim().equals("")){
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity2.this, R.style.Theme_AppCompat_Dialog_Alert)
                    .setIcon(R.drawable.ic_baseline_assignment_late_24)
                    .setTitle("BAŞLIK GİRİLMEDİ")
                    .setMessage(toastText)
                    .setPositiveButton(toastPositiveButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveOrUpdate();
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            Dialog dialog = alert.create();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_design);
            dialog.show();
        }
        else if(editTextNote.getText().toString().trim().equals("")){
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity2.this, R.style.Theme_AppCompat_Dialog_Alert)
                    .setIcon(R.drawable.ic_baseline_assignment_late_24)
                    .setTitle("NOT GİRİLMEDİ")
                    .setMessage(toastNoteText)
                    .setPositiveButton(toastPositiveButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveOrUpdate();
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            Dialog dialog = alert.create();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_design);
            dialog.show();
        }
        else{
            saveOrUpdate();
        }
    }
    //kaydetme ya da güncelleme metodu
    public void saveOrUpdate(){
        NoteModel noteModel = new NoteModel();
        noteModel.title = editTextTitle.getText().toString().trim();
        noteModel.note = editTextNote.getText().toString().trim();

        if(info.matches("new")){

            try {
                database=this.openOrCreateDatabase("Notes",MODE_PRIVATE,null);
                database.execSQL("CREATE TABLE IF NOT EXISTS notes(id INTEGER PRIMARY KEY,notetitlesql VARCHAR,note VARCHAR)");

                String sqlString="INSERT INTO notes(notetitlesql,note) VALUES(?,?)";
                SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
                sqLiteStatement.bindString(1,noteModel.title);
                sqLiteStatement.bindString(2,noteModel.note);
                sqLiteStatement.execute();

            }catch (Exception e){
                e.printStackTrace();
            }
            Intent intent=new Intent(MainActivity2.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast.makeText(getApplicationContext(),"KAYDEDİLDİ",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        else {
            if(editTextTitle.getText().toString().matches(noteTitleStr) && editTextNote.getText().toString().matches(noteStr)){
                Intent intent=new Intent(MainActivity2.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else{
                try {

                    database=this.openOrCreateDatabase("Notes",MODE_PRIVATE,null);
                    String sqlString="UPDATE notes SET notetitlesql=?,note=? WHERE id=?";
                    SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
                    sqLiteStatement.bindString(1,noteModel.title);
                    sqLiteStatement.bindString(2,noteModel.note);
                    sqLiteStatement.bindString(3,String.valueOf(noteId));
                    sqLiteStatement.execute();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent=new Intent(MainActivity2.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(getApplicationContext(),"GÜNCELLENDİ",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
    }

    public void shareClick(View view){
        share();
    }
    public void share(){
        if(editTextTitle.getText().toString().trim().equals("")&&editTextNote.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(),"PAYLAŞMAK İÇİN NOT YAZIN",Toast.LENGTH_SHORT).show();
        }
        else if(editTextNote.getText().toString().matches("")&&editTextTitle.getText().toString().trim().equals("")==false){
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,editTextTitle.getText().toString());
            intent.setType("text/plain");
            intent=Intent.createChooser(intent,"notu paylaş");
            startActivity(intent);
        }
        else if(editTextTitle.getText().toString().trim().equals("")&&editTextNote.getText().toString().trim().equals("")==false){
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,editTextNote.getText().toString());
            intent.setType("text/plain");
            intent=Intent.createChooser(intent,"notu paylaş");
            startActivity(intent);
        }
        else {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,editTextTitle.getText().toString()+":\n"+editTextNote.getText().toString());
            intent.setType("text/plain");
            intent=Intent.createChooser(intent,"notu paylaş");
            startActivity(intent);
        }
    }

}