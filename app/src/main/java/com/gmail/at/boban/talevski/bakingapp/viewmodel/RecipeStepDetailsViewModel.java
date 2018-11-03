package com.gmail.at.boban.talevski.bakingapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.gmail.at.boban.talevski.bakingapp.model.Step;

import java.util.List;

public class RecipeStepDetailsViewModel extends ViewModel {
    private List<Step> stepList;
    private MutableLiveData<Integer> stepPosition;
    private String recipeName;

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public LiveData<Integer> getStepPosition() {
        return stepPosition;
    }

    public void setStepPosition(int stepPosition) {
        if (this.stepPosition == null) {
            this.stepPosition = new MutableLiveData<>();
        }
        this.stepPosition.setValue(stepPosition);
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
