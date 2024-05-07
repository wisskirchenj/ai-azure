package com.exxeta.ai.model;

import java.util.List;

public record QuizQuestions(List<QuizQuestion> questions) {

    public record QuizQuestion(Difficulty difficulty, String title, String question, String answer) {
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
