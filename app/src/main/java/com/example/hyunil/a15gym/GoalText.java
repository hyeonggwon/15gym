package com.example.hyunil.a15gym;

/**
 * Created by user on 2017-11-22.
 */

public class GoalText {
    private String kindOfExercise;
    private String numberOfExercise;

    public GoalText(String kindOfExercise, String numberOfExercise) {
        this.kindOfExercise = kindOfExercise;
        this.numberOfExercise = numberOfExercise;
    }

    public GoalText() { /* Intentionally blanked */}

    public String getKindOfExercise() {
        return kindOfExercise;
    }

    public String getNumberOfExercise() {
        return numberOfExercise;
    }

    public void setKindOfExercise(String kindOfExercise) {
        this.kindOfExercise = kindOfExercise;
    }

    public void setNumberOfExercise(String numberOfExercise) {
        this.numberOfExercise = numberOfExercise;
    }
}
