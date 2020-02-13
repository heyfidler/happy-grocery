package com.fidflop.happygrocery;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.fidflop.happygrocery.com.fidflop.happygrocery.model.GroceryItem;
import com.fidflop.happygrocery.com.fidflop.happygrocery.model.GroceryList;

/**
 * Implementation of App Widget functionality.
 */
public class HappyGroceryWidget extends AppWidgetProvider {
    private static GroceryList groceryList;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.happy_grocery_widget);

        if (groceryList != null) {
            views.setTextViewText(R.id.widget_title_label, context.getText(R.string.app_name) + "\n" + groceryList.getName());

            if (groceryList.getGroceryItems() != null) {
                StringBuilder sb = new StringBuilder();
                for (GroceryItem groceryItem:groceryList.getGroceryItems()) {
                    sb.append(groceryItem.getName()).append("\n");
                }
                views.setTextViewText(R.id.widget_list_items, sb.toString());
            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        groceryList = intent.getParcelableExtra("groceryList");
        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
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

