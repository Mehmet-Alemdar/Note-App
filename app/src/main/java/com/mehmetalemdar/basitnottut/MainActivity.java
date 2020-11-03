package com.mehmetalemdar.basitnottut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.mehmetalemdar.basitnottut.Adapter.RecyclerAdapter;
import com.mehmetalemdar.basitnottut.Model.NoteModel;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.RowHolder.Listener {

    SQLiteDatabase database;

    RecyclerView mRecyclerView;
    RecyclerAdapter recyclerAdapter;
    ArrayList<NoteModel> noteModelArrayList;

    ImageView imageViewFeather;
    SearchView mSearchView;
    ImageView mSearchImage;

    int searchImageClickNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewFeather=findViewById(R.id.featherImage);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mSearchImage = findViewById(R.id.searchImage);
        mSearchView = findViewById(R.id.searchView);

        noteModelArrayList = new ArrayList<>();

        mSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchImageClickNumber ++ ;
                if (searchImageClickNumber % 2 == 0){
                    mSearchView.setVisibility(View.VISIBLE);
                    mSearchImage.setImageResource(R.drawable.ic_close);



                }else{
                    mSearchView.setVisibility(View.INVISIBLE);
                    mSearchImage.setImageResource(R.drawable.ic_search);
                    mSearchView.setQuery("",false);
                    mSearchView.clearFocus();
                    searchImageClickNumber = 1 ;

                }
            }
        });

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

        getData();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new RecyclerAdapter(noteModelArrayList,this);
        mRecyclerView.setAdapter(recyclerAdapter);

    }

    //veri tabanındaki veriler türlerine göre listelere aktarılıyor
    public void getData(){
        try {
            database=this.openOrCreateDatabase("Notes",MODE_PRIVATE,null);
            Cursor cursor=database.rawQuery("SELECT * FROM notes",null);

            int idIndex=cursor.getColumnIndex("id");
            int titleIndex=cursor.getColumnIndex("notetitlesql");
            int noteIndex=cursor.getColumnIndex("note");

            NoteModel mNoteModel;
            String noteStr;
            String titleStr;
            while (cursor.moveToNext()){
                mNoteModel = new NoteModel();

                mNoteModel.id = cursor.getInt(idIndex);
                titleStr = cursor.getString(titleIndex);
                noteStr = cursor.getString(noteIndex);

                titleStr = titleStr.trim();
                mNoteModel.title = titleStr;
                noteStr = noteStr.trim();
                mNoteModel.note = noteStr;

                noteModelArrayList.add(mNoteModel);


            }
            Collections.reverse(noteModelArrayList);
            recyclerAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //verileri silmek için metod
    public  int deletedData(Integer idPosition){
        try {
            database=getApplicationContext().openOrCreateDatabase("Notes",MODE_PRIVATE,null);
            database.execSQL("DELETE FROM notes WHERE id=?",new String[] {String.valueOf(noteModelArrayList.get(idPosition).id)});

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

    @Override
    public void onItemClickListener(int position) {

        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
        intent.putExtra("noteId",noteModelArrayList.get(position).id);
        intent.putExtra("info","old");
        startActivity(intent);
    }

    @Override
    public void onLongItemClickListener(int position) {

        final int which_position=position;
        String noteDeleteName=String.valueOf(noteModelArrayList.get(position).title);
        String noteContent = String.valueOf(noteModelArrayList.get(position).note);
        noteContent = noteContent.trim();
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert)
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setTitle("Practical Reading or Delete")
                .setMessage("-"+noteDeleteName.toUpperCase()+"-\n" + "\n"+noteContent + "\n"+"\nSilmek İster Misin ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        which=which_position;
                        deletedData(which);

                        noteModelArrayList.remove(which);

                        recyclerAdapter.notifyDataSetChanged();
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