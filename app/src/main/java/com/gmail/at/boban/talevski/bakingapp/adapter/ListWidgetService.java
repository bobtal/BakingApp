package com.gmail.at.boban.talevski.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = ListRemoteViewsFactory.class.getSimpleName();

    private Context context;
    private List<String> ingredientList;

    public ListRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        // Grab the set of ingredients from shared preferences as a Set<String> and convert it
        // to a List<String>
        Set<String> ingredientSet = SharedPreferencesUtils.getIngredientSetFromSharedPreferences(context);
        ingredientList = new ArrayList<>(ingredientSet);
    }

    @Override
    public void onDataSetChanged() {
        // just do the same thing from onCreate to reinitialize ingredientList to
        // the current data found in shared preferences
        onCreate();
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
        views.setTextViewText(R.id.widget_list_text, ingredientList.get(i));
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