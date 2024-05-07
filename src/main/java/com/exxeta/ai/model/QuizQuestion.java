package com.exxeta.ai.model;

public record QuizQuestion(Difficulty difficulty, String title, String question, String answer) {

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
