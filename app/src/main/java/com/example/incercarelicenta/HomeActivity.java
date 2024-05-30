package com.example.incercarelicenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.incercarelicenta.databinding.ActivityHomeBinding;
import com.example.incercarelicenta.fragmente.HomeFragment;
import com.example.incercarelicenta.fragmente.ProfileFragment;
import com.example.incercarelicenta.fragmente.SearchFragment;
import com.example.incercarelicenta.fragmente.SugestiiFragment;


public class HomeActivity extends AppCompatActivity {


    ActivityHomeBinding binding;

    /**
     * Objiect utilizat pentru adaugarea de parfumuri in Firebase
     * dintr-un fisier csv
     */
    AddPerfumes addPerfumes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        //////
//        if(!Python.isStarted()){
//            Python.start(new AndroidPlatform(this));
//        }
//        Python python=Python.getInstance();
//        PyObject result =python.getModule("main").callAttr("hello_world");
//        String message = result.toString();
//
//        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.searchBy:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.sugestii:
                    replaceFragment(new SugestiiFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
                }

        );
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

}