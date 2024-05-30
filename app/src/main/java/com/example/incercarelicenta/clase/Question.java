package com.example.incercarelicenta.clase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question {
    private String questionText;
    private List<String> answers;
    private List<Integer> imageIds;
    private Map<String, List<String>> subcategories;

    public Question(String questionText, List<String> answers, List<Integer> imageIds) {
        this.questionText = questionText;
        this.answers = answers;
        this.imageIds = imageIds;
        this.subcategories = new HashMap<>();
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<Integer> getImageIds() {
        return imageIds;
    }

    public Map<String, List<String>> getSubcategories() {
        return subcategories;
    }

    public void addSubcategory(String answer, List<String> subcategoryAnswers) {
        subcategories.put(answer, subcategoryAnswers);
    }
}

