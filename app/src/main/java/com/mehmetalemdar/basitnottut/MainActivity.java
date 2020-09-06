package com.mehmetalemdar.basitnottut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase database;

    ListView listViewNotes;
    ArrayList<String> arrayListNoteTitle;
    ArrayList<Integer> arrayListİd;
    ArrayList<String> arrayListNote;
    ArrayAdapter arrayAdapter;

    ImageView imageViewFeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewFeather=findViewById(R.id.featherİmage);



        imageViewFeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CountDownTimer(5000, 5000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //imageViewFeatherAnimation();
                        imageViewFeather.setVisibility(View.INVISIBLE);
                        ObjectAnimator animator=ObjectAnimator.ofFloat(imageViewFeather,"translationY",-15f);
                        animator.setDuration(500);
                        animator.start();

                    }

                    @Override
                    public void onFinish() {
                        imageViewFeather.setVisibility(View.VISIBLE);
                        ObjectAnimator animator=ObjectAnimator.ofFloat(imageViewFeather,"translationY",4f);
                        animator.setDuration(1500);
                        animator.start();
                    }
                }.start();
            }
        });

        listViewNotes=findViewById(R.id.listViewNote);
        arrayListNoteTitle=new ArrayList<>();
        arrayListNote=new ArrayList<>();
        arrayListİd=new ArrayList<>();

        //veritabanındaki veriler listview itemlarında görüntülenmesini sağlıyor
        arrayAdapter=new ArrayAdapter(this,R.layout.row,arrayListNoteTitle);
        listViewNotes.setAdapter(arrayAdapter);


        //tıklanan itemın index numarasına göre veritabnındaki o id numaralı bilgileri ikinci aktiviteye aktarıyor
        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("noteId",arrayListİd.get(position));
                intent.putExtra("info","old");
                startActivity(intent);


            }
        });

        listViewNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int which_position=position;
                String noteDeleteName=arrayAdapter.getItem(position).toString();
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert)
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setTitle("Sil")
                        .setMessage("-"+noteDeleteName.toUpperCase()+"-\nSilmek İstediğine Emin Misin?")
                        .setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                which=which_position;
                                deletedData(which);
                                arrayListNoteTitle.remove(which);
                                arrayListNote.remove(which);
                                arrayListİd.remove(which);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                Dialog dialog = alert.create();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_design);
                dialog.show();

                return true;
            }
        });
        getData();
    }

    /*public void imageViewFeatherAnimation(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageViewFeather,"translationX",150f);
        animator.setDuration(1000);
        animator.start();
    }

     */


    //veri tabanındaki veriler türlerine göre listelere aktarılıyor
    public void getData(){
        try {
            database=this.openOrCreateDatabase("Notes",MODE_PRIVATE,null);
            Cursor cursor=database.rawQuery("SELECT * FROM notes",null);

            int idIndex=cursor.getColumnIndex("id");
            int titleIndex=cursor.getColumnIndex("notetitlesql");
            int noteIndex=cursor.getColumnIndex("note");



            while (cursor.moveToNext()){

                String noteTextLength=cursor.getString(titleIndex);
                if(noteTextLength.length()>30){
                    arrayListNoteTitle.add(noteTextLength.substring(0,30)+"...");
                }
                else{
                    arrayListNoteTitle.add(noteTextLength);
                }
                arrayListİd.add(cursor.getInt(idIndex));
                arrayListNote.add(cursor.getString(noteIndex));

            }
            Collections.reverse(arrayListNoteTitle); //en son eklenen notu yukarıya ekliyor
            Collections.reverse(arrayListİd); //en son eklenen not yukarıya geldiğinden idler ile list indexleri ters düşmesin diye idleri ters çevirip eşitliyorum
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //verileri silmek için metod
    public  int deletedData(Integer idPosition){
        try {
            database=getApplicationContext().openOrCreateDatabase("Notes",MODE_PRIVATE,null);
            database.execSQL("DELETE FROM notes WHERE id=?",new String[] {String.valueOf(arrayListİd.get(idPosition))});

        }
        catch (Exception e){
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(),"SİLİNDİ",Toast.LENGTH_SHORT).show();
        return idPosition;
    }

    
    public void clicked(View view){

        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
        intent.putExtra("info","new");
        startActivity(intent);
    }

}