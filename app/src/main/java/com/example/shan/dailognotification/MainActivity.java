package com.example.shan.dailognotification;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MyRecyclerViewAdapter.ItemClickListener {
    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private View lastSelectedView;
    private Context context;
    private int selectedPosition=-1;
    private ArrayList<String> manufacturerNames;

    private final int notificationId = 1;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        manufacturerNames = new ArrayList<>();
        manufacturerNames.add("Apple");
        manufacturerNames.add("Samsung");
        manufacturerNames.add("Phillips");
        manufacturerNames.add("LG");

        context = this;
        lastSelectedView = null;
        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvManufacturer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, manufacturerNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    public void showDailog(View view){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Delete item "+ which, Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void sendNotification(View v){
        //Create an Intent for the BroadcastReceiver
        Intent buttonIntent =
                new Intent(context, DismissNotificationReceiver.class);
        buttonIntent.putExtra("notificationId", notificationId);

        PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, buttonIntent,0);

        //RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.widget_update_notification);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.baseline_add_alert_black_24dp)
                .addAction(R.drawable.baseline_close_black_24dp, "Dismiss Notification", btPendingIntent);

        try {
            notificationManager.notify(notificationId, builder.build());
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    public PendingIntent getDismissIntent(int notificationId, Context context) {
        //Intent intent = new Intent(context, NotificationActivity.class);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationManager.cancel(notificationId);
        return dismissIntent;
    }

    @Override
    public void onItemClick(View view, int position) {
        view.setSelected(true);
        if(lastSelectedView != null){
            lastSelectedView.setBackgroundColor(Color.TRANSPARENT);
        }
        view.setBackgroundColor(Color.RED);
        lastSelectedView = view;

        selectedPosition = position;

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        manufacturerNames.remove(selectedPosition);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Delete item "+ which, Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
