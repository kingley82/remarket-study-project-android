package com.kingleystudio.remarket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kingleystudio.remarket.utils.Logs;

public abstract class ABCActivity extends AppCompatActivity {

    public void newActivity(Class activity) {
        newActivity(activity, null);
    }

    public void newActivity(Class activity, Bundle instance) {
        Intent intent = new Intent(getApplicationContext(), activity);
        if (activity == NewAdActivity.class || activity == PaymentActivity.class)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        else if (activity == LoginActivity.class)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
        else
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("prev_activity", activity.getSimpleName());
        if (instance != null)
            intent.putExtras(instance);
        startActivity(intent);
    }

    public void newActivityNewTask(Class activity) {
        Intent intent = new Intent(getApplicationContext(), activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
