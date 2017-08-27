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
 * From old recipe widget to cycle through ingredients. Will be re-implemented
 * later so I'm keeping it.
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

    //display the next ingredient (or the first)
    public void handleActionNextIngredient() {
        preferences = this.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = preferences.edit();
        ingredients = preferences.getString("INGREDIENTS", null).split("-");
        int nextIngredient;

        if (preferences.getInt("CURRENT_INGREDIENT", 0) +1 == ingredients.length) {
            nextIngredient = 0;
        } else {
            nextIngredient = preferences.getInt("CURRENT_INGREDIENT", 0) + 1;
        }

        String widgetText = ingredients[nextIngredient];
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, IngredientWidget.class));

        editor.putInt("CURRENT_INGREDIENT", nextIngredient);
        editor.apply();

        IngredientWidget.updateWidgetText(this, manager, appWidgetIds, null);
    }

    //display the previous ingredient (or the last)
    public void handleActionPreviousIngredient() {
        preferences = this.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = preferences.edit();
        ingredients = preferences.getString("INGREDIENTS", null).split("-");
        int nextIngredient;

        if (preferences.getInt("CURRENT_INGREDIENT", 0) == 0) {
            nextIngredient = ingredients.length - 1;
        } else {
            nextIngredient = preferences.getInt("CURRENT_INGREDIENT", 0) - 1;
        }
        String widgetText = ingredients[nextIngredient];
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, IngredientWidget.class));

        editor.putInt("CURRENT_INGREDIENT", nextIngredient);
        editor.apply();

        IngredientWidget.updateWidgetText(this, manager, appWidgetIds, null);
    }
}
