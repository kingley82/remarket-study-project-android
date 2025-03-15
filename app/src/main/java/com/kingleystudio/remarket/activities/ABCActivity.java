package com.kingleystudio.remarket.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class ABCActivity extends AppCompatActivity {
    public void newActivity(Class activity, Bundle instance) {
        newActivity(activity, false, instance);
    }

    public void newActivity(Class activity) {
        newActivity(activity, false, null);
    }

    public void newActivity(Class activity, boolean hot, Bundle instance) {
        Intent intent = new Intent(getApplicationContext(), activity);
        if (hot)
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (instance != null)
            intent.putExtras(instance);
        startActivity(intent);
    }
}
