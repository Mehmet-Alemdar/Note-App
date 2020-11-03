package com.mehmetalemdar.basitnottut.Model;

public class NoteModel {
    public int id;
    public String title;
    public String note;

    public NoteModel(){

    }
    public NoteModel( String title, String note) {
        this.title = title;
        this.note = note;
    }
}
