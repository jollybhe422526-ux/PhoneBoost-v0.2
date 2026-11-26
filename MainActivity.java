package com.phoneboost.app;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class MainActivity extends AppCompatActivity {

    private TextView tvModel;
    private TextView tvAndroid;
    private TextView tvStorageTotal;
    private TextView tvStorageUsed;
    private TextView tvStorageFree;
    private TextView tvRamTotal;
    private TextView tvRamFree;
    private TextView tvTips;
    private LinearProgressIndicator progressStorage;
    private LinearProgressIndicator progressRam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvModel = findViewById(R.id.tvModel);
        tvAndroid = findViewById(R.id.tvAndroid);
        tvStorageTotal = findViewById(R.id.tvStorageTotal);
        tvStorageUsed = findViewById(R.id.tvStorageUsed);
        tvStorageFree = findViewById(R.id.tvStorageFree);
        tvRamTotal = findViewById(R.id.tvRamTotal);
        tvRamFree = findViewById(R.id.tvRamFree);
        tvTips = findViewById(R.id.tvTips);
        progressStorage = findViewById(R.id.progressStorage);
        progressRam = findViewById(R.id.progressRam);
        MaterialButton btnRefresh = findViewById(R.id.btnRefresh);

        btnRefresh.setOnClickListener(v -> refreshInfo());

        refreshInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshInfo();
    }

    private void refreshInfo() {
        // Device info
        String manufacturer = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER;
        String model = Build.MODEL == null ? "" : Build.MODEL;
        tvModel.setText("Model: " + capitalize(manufacturer) + " " + model);
        tvAndroid.setText("Android version: " + Build.VERSION.RELEASE
                + " (API " + Build.VERSION.SDK_INT + ")");

        // Internal storage
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long totalStorage = stat.getTotalBytes();
        long freeStorage = stat.getAvailableBytes();
        long usedStorage = totalStorage - freeStorage;
        int usedStoragePercent = totalStorage > 0
                ? (int) ((usedStorage * 100L) / totalStorage) : 0;

        tvStorageTotal.setText("Total: " + formatSize(totalStorage));
        tvStorageUsed.setText("Used: " + formatSize(usedStorage)
                + " (" + usedStoragePercent + "%)");
        tvStorageFree.setText("Available: " + formatSize(freeStorage));
        progressStorage.setProgressCompat(usedStoragePercent, true);

        // RAM
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);
        long totalRam = memInfo.totalMem;
        long freeRam = memInfo.availMem;
        long usedRam = totalRam - freeRam;
        int usedRamPercent = totalRam > 0
                ? (int) ((usedRam * 100L) / totalRam) : 0;

        tvRamTotal.setText("Total RAM: " + formatSize(totalRam));
        tvRamFree.setText("Available RAM: " + formatSize(freeRam)
                + " (" + usedRamPercent + "% in use)");
        progressRam.setProgressCompat(usedRamPercent, true);

        // Recommendations
        tvTips.setText(buildTips(usedStoragePercent, usedRamPercent));
    }

    private String buildTips(int storageUsedPercent, int ramUsedPercent) {
        StringBuilder sb = new StringBuilder();

        if (storageUsedPercent >= 90) {
            sb.append("\u2022 Your storage is almost full. Delete old videos, photos you no longer need, and unused apps.\n");
        } else if (storageUsedPercent >= 75) {
            sb.append("\u2022 Your storage is getting full. Consider removing large files and apps you do not use.\n");
        } else {
            sb.append("\u2022 Your storage looks healthy.\n");
        }

        if (ramUsedPercent >= 85) {
            sb.append("\u2022 Memory is very busy. Close apps you are not using, then tap Refresh.\n");
        } else if (ramUsedPercent >= 70) {
            sb.append("\u2022 Memory use is a little high. Closing apps you finished with can help.\n");
        } else {
            sb.append("\u2022 Your memory usage looks normal.\n");
        }

        sb.append("\u2022 Restart your phone once in a while. This is a safe way to refresh the system.\n");
        sb.append("\u2022 Keep Android and your apps updated for the best performance and security.\n");
        sb.append("\u2022 Note: Android manages memory automatically, so an empty RAM is not always better.");

        return sb.toString();
    }

    private String formatSize(long bytes) {
        return Formatter.formatFileSize(this, bytes);
    }

    private String capitalize(String text) {
        if (text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
