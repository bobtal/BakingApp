package com.gmail.at.boban.talevski.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.adapter.ListWidgetService;
import com.gmail.at.boban.talevski.bakingapp.ui.MainActivity;
import com.gmail.at.boban.talevski.bakingapp.utils.SharedPreferencesUtils;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Grab the recipe name from shared preferences and set it to the text view in the widget
        String recipeName = SharedPreferencesUtils.getRecipeNameFromSharedPreferences(context);
        views.setTextViewText(R.id.widget_recipe_name, recipeName);

        // Set the adapter to the listview
        Intent adapterIntent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list, adapterIntent);

        // Set the click handler on the listview to open the app
        views.setOnClickPendingIntent(R.id.widget_root_linear_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // just call updateBakingWidgets which is triggered for a manual update when the
        // user opens a recipe
        // there are no automatic updates required since the state is saved in shared preferences
        // and when it changes, a manual update is triggered by calling updateBakingWidgets
        BakingWidgetProvider.updateBakingWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateBakingWidgets(
            Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

