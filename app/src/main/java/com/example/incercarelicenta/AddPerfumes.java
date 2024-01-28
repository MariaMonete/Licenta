package com.example.incercarelicenta;

import com.example.incercarelicenta.clase.Parfum;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AddPerfumes {

    FirebaseFirestore fstore;
    private CollectionReference collection;

    public AddPerfumes(){
        /**
         * Cream o instanta a bazei de date Firestore
         */
        fstore = FirebaseFirestore.getInstance();
        /**
         * Selectam colectia in care vom adauga parfumurile
         */
        collection = fstore.collection("perfumes");
    }


    /**
     * Creaza o lista cu toate parfumurile din csv.
     * Si dupa adauga in baza de date in paralel pentru ca adaugarea
     * sa se faca mai rapid
     *
     * @param inputStream - este fisierul din directorul assets
     */
    public void addInFirebase(InputStream inputStream){
        try {
            List<Parfum> perfumes = readPerfumesFromCSV(inputStream);
            testParallelIndividualWrites(perfumes);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adaugarea se face pe mai multe fire de executie pentru a fi mai rapida
     * si mai eficienta. In cazul unei adaugari secventiale aplicatia se inchide
     *
     * @param datas - lista de parfumuri
     */
    public void testParallelIndividualWrites(List<Parfum> datas) {
        // Create an ExecutorService with a fixed number of threads
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // Create a list to hold Future objects
        List<Future<Void>> futures = new LinkedList<>();

        // Submit tasks to the executor
        for (Parfum data : datas) {
            // Use submit() to get a Future object for each task
            Future<Void> future = executorService.submit(() -> {
                // Perform the Firestore write operation
                collection.document(data.getName()).set(data);
                return null;
            });

            futures.add(future);
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            try {
                // Use get() to wait for the task to complete
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                // Handle exceptions as needed
            }
        }

        // Shutdown the executor service
        executorService.shutdown();
    }


    /**
     * Ia fiecare linie din fisierul csv si creeaza un obiect de tip Parfum.
     * Obiectul de tip Parfum este adaugat in lista de parfumuri doar daca
     * lista de note nu este goala.
     *
     * @param myInputstream - fisierul csv din directorul assets
     * @return - lita de parfumuri
     */
    public List<Parfum> readPerfumesFromCSV(InputStream myInputstream) {
        List<Parfum> perfumes = new ArrayList<>();

        try (InputStream inputStream = myInputstream;
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader reader = new CSVReader(inputStreamReader)) {

            List<String[]> records = reader.readAll();
            for (String[] record : records.subList(1, records.size())) { // Skip the header row
                Parfum perfume = new Parfum();
                perfume.setName(record[0]);
                perfume.setBrand(record[1]);
                perfume.setNotes(List.of(record[3].split(", "))); // Assuming notes are separated by a comma and space
                perfume.setDescription(record[2]);
                perfume.setImgUrl(record[4]);
                //validarea asta te scapa de parfumurile care nu au note
                if(perfume.getNotes().size()>1) {
                    perfumes.add(perfume);
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return perfumes;
    }
}
