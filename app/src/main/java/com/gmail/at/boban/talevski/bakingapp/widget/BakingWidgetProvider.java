package com.gmail.at.boban.talevski.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.adapter.ListWidgetService;
import com.gmail.at.boban.talevski.bakingapp.model.Ingredient;
import com.gmail.at.boban.talevski.bakingapp.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.at.boban.talevski.bakingapp.ui.RecipeDetailsFragment.EXTRA_INGREDIENT_LIST;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<Ingredient> ingredientList, String recipeName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Set the adapter to the listview
        Intent adapterIntent = new Intent(context, ListWidgetService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_INGREDIENT_LIST, (ArrayList<? extends Parcelable>) ingredientList);
        adapterIntent.putExtras(bundle);
        views.setRemoteAdapter(R.id.widget_list, adapterIntent);

        // Set the click handler on the listview to open the app
        views.setOnClickPendingIntent(R.id.widget_root_linear_layout, pendingIntent);

        // set recipe name to the text view in the widget
        views.setTextViewText(R.id.widget_recipe_name, recipeName);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // do nothing as updates are only triggered when the user sees a recipe in the app
    }

    public static void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager,
                                           int[] appWidgetIds, List<Ingredient> ingredientList, String recipeName) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, ingredientList, recipeName);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

