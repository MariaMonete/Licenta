package com.example.incercarelicenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.example.incercarelicenta.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout=findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Conectare"));
        tabLayout.addTab(tabLayout.newTab().setText("Înregistrare"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("perfumes").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                int index = 0;
//                for (DocumentSnapshot document : task.getResult()) {
//                    DocumentReference docRef = db.collection("perfumes").document(document.getId());
//                    docRef.update("index", index)
//                            .addOnSuccessListener(aVoid -> Log.d("UpdateIndex", "DocumentSnapshot successfully updated!"))
//                            .addOnFailureListener(e -> Log.w("UpdateIndex", "Error updating document", e));
//                    index++;
//                }
//            } else {
//                Log.w("UpdateIndex", "Error getting documents.", task.getException());
//            }
//        });


    }
}