package com.example.incercarelicenta.fragmente;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.incercarelicenta.ParfumByNotaActivity;
import com.example.incercarelicenta.adapter.CategoryAdapter;
import com.example.incercarelicenta.R;
import com.example.incercarelicenta.clase.CategorieNote;
import com.example.incercarelicenta.clase.Categorie;
import com.example.incercarelicenta.interfete.RecyclerViewInterfaceString;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class SearchFragment extends Fragment implements RecyclerViewInterfaceString {

    private FirebaseFirestore db;
    private List<Categorie> allNotes;

    private HashMap<CategorieNote,List<String>> allCategories;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private HashMap<String, Integer> categoryImageMap=new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        db = FirebaseFirestore.getInstance();
        allCategories = new HashMap<>();
        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        allNotes = new ArrayList<>();

        db.collection("perfumes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> notes = (List<String>) document.get("notes");
                            for (String note : notes) {
                                String normalizedNote = note.replaceAll("[()]", "")
                                        .replaceAll("\\.", "").replaceAll("and","")
                                        .replaceAll("And","")
                                        .replaceAll("AND","")
                                        .replaceAll("-"," ")
                                        .toUpperCase().trim();
                                if(normalizedNote.length()<=15 && getCategorieForNota(normalizedNote)!=null) {
                                    CategorieNote category = getCategorieForNota(normalizedNote);
                                    Categorie existingCategorii = findParentItemByCategory(allNotes,category.toString());

                                    if(existingCategorii!=null ){
                                        if(!existingCategorii.containsNote(normalizedNote))
                                            existingCategorii.addNote(normalizedNote);
                                    }
                                    else{
                                        Categorie categorii = new Categorie(category.toString());
                                        categorii.addNote(normalizedNote);
                                        allNotes.add(categorii);
                                    }
                                    if (!allCategories.containsKey(category)) {
                                        allCategories.put(category, new ArrayList<>());
                                    }
                                    List<String> noteList = allCategories.get(category);
                                    if (!noteList.contains(normalizedNote)) {
                                        noteList.add(normalizedNote);
                                    }
                                } 
                            }
                        }
                        initCategoryImageMap();
                        CategoryAdapter
                                parentItemAdapter
                                = new CategoryAdapter(
                                this, allNotes,categoryImageMap);

                        recyclerView
                                .setAdapter(parentItemAdapter);
//                        adapter = new CategorieAdapter(allCategories,categoryImageMap);
//                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "Eroare la obținerea datelor", Toast.LENGTH_LONG).show();
                    }
                });



        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private static Categorie findParentItemByCategory(List<Categorie> list, String category) {
        for (Categorie obj : list) {
            if (obj.getParentItemTitle().equals(category)) {
                return obj;
            }
        }
        return null;
    }

    private void initCategoryImageMap() {
        categoryImageMap.put(CategorieNote.CITRUS.toString(), R.drawable.citrus);
        categoryImageMap.put(CategorieNote.FLORAL.toString(), R.drawable.floral);
        categoryImageMap.put(CategorieNote.FRUITY.toString(), R.drawable.fruity);
        categoryImageMap.put(CategorieNote.MUSK.toString(), R.drawable.musk);
        categoryImageMap.put(CategorieNote.GOURMAND.toString(), R.drawable.gourmand);
        categoryImageMap.put(CategorieNote.GREENNOTES.toString(), R.drawable.green);
        categoryImageMap.put(CategorieNote.SPICY.toString(), R.drawable.spices);
        categoryImageMap.put(CategorieNote.WOODY.toString(), R.drawable.woody);
    }

    private CategorieNote getCategorieForNota(String nota) {
        if (nota.toLowerCase().contains("floral")||nota.toLowerCase().contains("jasmi")||nota.toLowerCase().contains("orchid")||nota.toLowerCase().contains("fleur")||nota.toLowerCase().contains("hibiscus")||nota.toLowerCase().contains("galbanum")||nota.toLowerCase().contains("violet")||nota.toLowerCase().contains("lychee")||nota.toLowerCase().contains("neroli")||nota.toLowerCase().contains("labdanum")||nota.toLowerCase().contains("floral")||nota.toLowerCase().contains("gardenia")||nota.toLowerCase().contains("tuberose")||nota.toLowerCase().contains("flower")||nota.toLowerCase().contains("blossom")||nota.toLowerCase().contains("carnation")||nota.toLowerCase().contains("calla")||nota.toLowerCase().contains("chamomile")||nota.toLowerCase().contains("cyclamen")||nota.toLowerCase().contains("dahlia")||nota.toLowerCase().contains("daisy")||nota.toLowerCase().contains("dandelion")||nota.toLowerCase().contains("freesia")||nota.toLowerCase().contains("geranium")||nota.toLowerCase().contains("gerbera")||nota.toLowerCase().contains("gladiolus")||nota.toLowerCase().contains("heliotrope")||nota.toLowerCase().contains("honeydew")||nota.toLowerCase().contains("hortensia")||nota.toLowerCase().contains("hyacinth")||nota.toLowerCase().contains("iris")||nota.toLowerCase().contains("jasmine")||nota.toLowerCase().contains("lavender")||nota.toLowerCase().contains("lilac")||nota.toLowerCase().contains("lily")||nota.toLowerCase().contains("lotus")||nota.toLowerCase().contains("magnolia")||nota.toLowerCase().contains("floral")||nota.toLowerCase().contains("mimosa")||nota.toLowerCase().contains("narcissus")||nota.toLowerCase().contains("orchis")||nota.toLowerCase().contains("osmanthus")||nota.toLowerCase().contains("peony")||nota.toLowerCase().contains("poppy")||nota.toLowerCase().contains("rose")||nota.toLowerCase().contains("sweet pea")||nota.toLowerCase().contains("tulip")||nota.toLowerCase().contains("wisteria")||nota.toLowerCase().contains("ylang")) {
            return CategorieNote.FLORAL;
        }else if (nota.toLowerCase().contains("fruity")||nota.toLowerCase().contains("papaya")||nota.toLowerCase().contains("bergamot")||nota.toLowerCase().contains("cassis")||nota.toLowerCase().contains("apple")||nota.toLowerCase().contains("apricot")||nota.toLowerCase().contains("banana")||nota.toLowerCase().contains("berry")||nota.toLowerCase().contains("cherry")||nota.toLowerCase().contains("currant")||nota.toLowerCase().contains("fig")||nota.toLowerCase().contains("fruit")||nota.toLowerCase().contains("grape")||nota.toLowerCase().contains("litchi")||nota.toLowerCase().contains("kiwi")||nota.toLowerCase().contains("guava")||nota.toLowerCase().contains("mango")||nota.toLowerCase().contains("melon")||nota.toLowerCase().contains("nectarine")||nota.toLowerCase().contains("peach")||nota.toLowerCase().contains("pear")||nota.toLowerCase().contains("pineapple")||nota.toLowerCase().contains("pitahaya")||nota.toLowerCase().contains("plum")||nota.toLowerCase().contains("pomegranate")||nota.toLowerCase().contains("berri")) {
            return CategorieNote.FRUITY;
        } else if (nota.toLowerCase().contains("citrus")||nota.toLowerCase().contains("tangerine")||nota.toLowerCase().contains("lemon")||nota.toLowerCase().contains("grapefruit")||nota.toLowerCase().contains("clementine")||nota.toLowerCase().contains("mandarin")||nota.toLowerCase().contains("lime")||nota.toLowerCase().contains("orange")||nota.toLowerCase().contains("yuzu")) {
            return CategorieNote.CITRUS;
        } else if (nota.toLowerCase().contains("aloe")||nota.toLowerCase().contains("patch")||nota.toLowerCase().contains("thyme")||nota.toLowerCase().contains("mint")||nota.toLowerCase().contains("leaf")||nota.toLowerCase().contains("basil")||nota.toLowerCase().contains("cactus")||nota.toLowerCase().contains("leaves")||nota.toLowerCase().contains("fern")||nota.toLowerCase().contains("grass")||nota.toLowerCase().contains("green")||nota.toLowerCase().contains("juniper")||nota.toLowerCase().contains("rosemary")||nota.toLowerCase().contains("tea")||nota.toLowerCase().contains("tree")) {
            return CategorieNote.GREENNOTES;
        } else if (nota.toLowerCase().contains("spicy")||nota.toLowerCase().contains("smok")||nota.toLowerCase().contains("cumin")||nota.toLowerCase().contains("ciga")||nota.toLowerCase().contains("benzoin")||nota.toLowerCase().contains("balsamic")||nota.toLowerCase().contains("incense")||nota.toLowerCase().contains("benzoin")||nota.toLowerCase().contains("safran")||nota.toLowerCase().contains("herbs")||nota.toLowerCase().contains("patchouli")||nota.toLowerCase().contains("whiskey")||nota.toLowerCase().contains("tobacco")||nota.toLowerCase().contains("myrrh")||nota.toLowerCase().contains("eucalyptus")||nota.toLowerCase().contains("spice")||nota.toLowerCase().contains("cardamom")||nota.toLowerCase().contains("cinnamon")||nota.toLowerCase().contains("clove")||nota.toLowerCase().contains("coffee")||nota.toLowerCase().contains("coriander")||nota.toLowerCase().contains("curry")||nota.toLowerCase().contains("ginger")||nota.toLowerCase().contains("licorice")||nota.toLowerCase().contains("nutmeg")||nota.toLowerCase().contains("oriental")||nota.toLowerCase().contains("pepper")||nota.toLowerCase().contains("saffron")||nota.toLowerCase().contains("sesame")||nota.toLowerCase().contains("tonka")||nota.toLowerCase().contains("bay")) {
            return CategorieNote.SPICY;
        } else if (nota.toLowerCase().contains("gourmand")||nota.toLowerCase().contains("marshmallow")||nota.toLowerCase().contains("bubble")||nota.toLowerCase().contains("gum")||nota.toLowerCase().contains("cocoa")||nota.toLowerCase().contains("pumpkin")||nota.toLowerCase().contains("meringue")||nota.toLowerCase().contains("choco")||nota.toLowerCase().contains("biscuit")||nota.toLowerCase().contains("butter")||nota.toLowerCase().contains("cacao")||nota.toLowerCase().contains("cake")||nota.toLowerCase().contains("candy")||nota.toLowerCase().contains("candi")||nota.toLowerCase().contains("caramel")||nota.toLowerCase().contains("chocolate")||nota.toLowerCase().contains("coconut")||nota.toLowerCase().contains("cookie")||nota.toLowerCase().contains("cream")||nota.toLowerCase().contains("cupcake")||nota.toLowerCase().contains("dates")||nota.toLowerCase().contains("nut")||nota.toLowerCase().contains("honey")||nota.toLowerCase().contains("milk")||nota.toLowerCase().contains("praline")||nota.toLowerCase().contains("jam")||nota.toLowerCase().contains("sugar")||nota.toLowerCase().contains("sweet")||nota.toLowerCase().contains("syrup")||nota.toLowerCase().contains("toffee")||nota.toLowerCase().contains("vanilla")||nota.toLowerCase().contains("pistachio")||nota.toLowerCase().contains("almond")) {
            return CategorieNote.GOURMAND;
        } else if (nota.toLowerCase().contains("woody")||nota.toLowerCase().contains("pine")||nota.toLowerCase().contains("resin")||nota.toLowerCase().contains("seed")||nota.toLowerCase().contains("moss")||nota.toLowerCase().contains("wood")||nota.toLowerCase().contains("tree")||nota.toLowerCase().contains("bark")||nota.toLowerCase().contains("beech")||nota.toLowerCase().contains("spruce")||nota.toLowerCase().contains("cedar")||nota.toLowerCase().contains("oud")||nota.toLowerCase().contains("ebony")||nota.toLowerCase().contains("oak")||nota.toLowerCase().contains("patchouli")||nota.toLowerCase().contains("sandal")||nota.toLowerCase().contains("vetiver")) {
            return CategorieNote.WOODY;
        } else if (nota.toLowerCase().contains("musk")||nota.toLowerCase().contains("gris")||nota.toLowerCase().contains("amber")||nota.toLowerCase().contains("cashm")||nota.toLowerCase().contains("water")||nota.toLowerCase().contains("aqua")||nota.toLowerCase().contains("animal")||nota.toLowerCase().contains("orris")||nota.toLowerCase().contains("powder")||nota.toLowerCase().contains("cotton")||nota.toLowerCase().contains("linen")||nota.toLowerCase().contains("fresh")||nota.toLowerCase().contains("marine")||nota.toLowerCase().contains("salt")||nota.toLowerCase().contains("air")||nota.toLowerCase().contains("civet")||nota.toLowerCase().contains("raisin")||nota.toLowerCase().contains("birch")||nota.toLowerCase().contains("amber")||nota.toLowerCase().contains("ambrette")||nota.toLowerCase().contains("ambroxan")||nota.toLowerCase().contains("leather")||nota.toLowerCase().contains("skin")||nota.toLowerCase().contains("suede")||nota.toLowerCase().contains("sea")) {
            return CategorieNote.MUSK;
        }
        else{
            return null;
        }
    }

    @Override
    public void onItemClick(String name) {
        Toast.makeText(this.getContext(),"Ai apasat pe nota " + name,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), ParfumByNotaActivity.class);
        intent.putExtra("nota",name);

        startActivity(intent);
    }
}
