package com.taitsmith.daybaker.data;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import static com.taitsmith.daybaker.activities.StepSummaryActivity.SHARED_PREFS;

/**
 * Updates text on the ingredient list widget
 */

public class IngredientWidgetService extends IntentService {
    public static final String ACTION_UPDATE_NEXT_INGREDIENT =
            "com.taitsmith.daybaker.data.action.next_ingredient";
    public static final String ACTION_UPDATE_PREVIOUS_INGREDIENT =
            "com.taitsmith.daybaker.data.action.previous_ingredient";

    private SharedPreferences preferences;
    private String[] ingredients;

    public IngredientWidgetService() {
        super("IngredientWidgetService");
    }

    public static void startActionNextIngredient(Context context) {
        Intent intent = new Intent(context, IngredientWidgetService.class);
        intent.setAction(ACTION_UPDATE_NEXT_INGREDIENT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_UPDATE_NEXT_INGREDIENT.equals(action)) {
                handleActionNextIngredient();
            } else if (ACTION_UPDATE_PREVIOUS_INGREDIENT.equals(action)) {
                handleActionPreviousIngredient();
            }
        }
    }

    public void handleActionNextIngredient() {
        preferences = this.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            int nextIngredient = preferences.getInt("CURRENT_INGREDIENT", 0) + 1;

            ingredients = preferences.getString("INGREDIENTS", null).split("-");
            String widgetText = ingredients[nextIngredient];
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, IngredientWidget.class));

            editor.putInt("CURRENT_INGREDIENT", nextIngredient);
            editor.apply();

            IngredientWidget.updateWidgetText(this, manager, appWidgetIds, widgetText);
        } catch (ArrayIndexOutOfBoundsException e) {
            handleActionPreviousIngredient();
        }
    }

    public void handleActionPreviousIngredient() {
        preferences = this.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = preferences.edit();


        try {
            int nextIngredient = preferences.getInt("CURRENT_INGREDIENT", 0) - 1;

            ingredients = preferences.getString("INGREDIENTS", null).split("-");
            String widgetText = ingredients[nextIngredient];
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, IngredientWidget.class));

            editor.putInt("CURRENT_INGREDIENT", nextIngredient);
            editor.apply();

            IngredientWidget.updateWidgetText(this, manager, appWidgetIds, widgetText);
        } catch (ArrayIndexOutOfBoundsException e) {
            handleActionNextIngredient();
        }
    }
}
