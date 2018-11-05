package com.gmail.at.boban.talevski.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Ingredient;
import com.gmail.at.boban.talevski.bakingapp.ui.RecipeDetailsFragment;

import java.util.ArrayList;
import java.util.List;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle bundle = intent.getExtras();
        ArrayList<Ingredient> ingredientList =
                bundle.getParcelableArrayList(RecipeDetailsFragment.EXTRA_INGREDIENT_LIST);
        return new ListRemoteViewsFactory(this.getApplicationContext(), ingredientList);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = ListRemoteViewsFactory.class.getSimpleName();

    private Context context;
    private ArrayList<Ingredient> ingredientList;

    public ListRemoteViewsFactory(Context context, ArrayList<Ingredient> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredientList == null ? 0 : ingredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        views.setTextViewText(R.id.widget_list_text, ingredientList.get(i).toString(context));
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}