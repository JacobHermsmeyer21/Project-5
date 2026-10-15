package com.example.myapplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WaterEntry.class}, version = 1, exportSchema = false)
public abstract class WaterDatabase extends RoomDatabase {
    private static volatile WaterDatabase instance;

    public abstract WaterEntryDao waterEntryDao();

    public static WaterDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (WaterDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.getApplicationContext(), WaterDatabase.class, "water_journal.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}
