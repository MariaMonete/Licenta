package com.example.incercarelicenta.clase;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String Username;
    private String Email;
    private List<Parfum> ListaParfFav;
    private List<String> NoteFav;
    private boolean isModerator;
    @PropertyName("moderator")
    public boolean isModerator() {
        return isModerator;
    }
    @PropertyName("moderator")
    public void setModerator(boolean moderator) {
        isModerator = false;
    }

    public List<String> getNoteFav() {
        return NoteFav;
    }
    public void setNoteFav(List<String> noteFav) {
        NoteFav = noteFav;
    }
    public User() {
        ListaParfFav = new ArrayList<>();
    }
    public String getUsername() {
        return Username;
    }
    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public List<Parfum> getListaParfFav() {
        return ListaParfFav;
    }

    public void setListaParfFav(List<Parfum> listaParfFav) {
        ListaParfFav = listaParfFav;
    }


}
