package com.gmail.at.boban.talevski.bakingapp.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.gmail.at.boban.talevski.bakingapp.model.Step;

import java.util.List;

public class RecipeStepDetailsViewModel extends ViewModel {
    private List<Step> stepList;
    private int stepPosition;

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public int getStepPosition() {
        return stepPosition;
    }

    public void setStepPosition(int stepPosition) {
        this.stepPosition = stepPosition;
    }

}
