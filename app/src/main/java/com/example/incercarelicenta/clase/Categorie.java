package com.example.incercarelicenta.clase;

import java.util.ArrayList;
import java.util.List;

public class Categorie {
    private String categorieTitle;
    private List<String> noteList;

    public Categorie(
            String categorieTitle)
    {

        this.categorieTitle = categorieTitle;
        this.noteList = new ArrayList<>();
    }

    public String getParentItemTitle()
    {
        return categorieTitle;
    }

    public List<String> getNoteList()
    {
        return noteList;
    }

    public void addNote(String note){
        noteList.add(note);
    }

    public boolean containsNote(String note){
        if(noteList.contains(note)){
            return true;
        }
        else {
            return false;
        }
    }
}
