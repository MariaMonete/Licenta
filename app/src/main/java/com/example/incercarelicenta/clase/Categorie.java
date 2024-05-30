package com.example.incercarelicenta.clase;

import java.util.ArrayList;
import java.util.List;

public class Categorie {

    // Declaration of the variables
    private String categorieTitle;
    private List<String> noteList;

    // Constructor of the class
    // to initialize the variables
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
