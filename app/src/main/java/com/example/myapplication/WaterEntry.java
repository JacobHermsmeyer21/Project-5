package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "water_entries")
public class WaterEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public int amountMl;

    @NonNull
    public String note = "";

    public long recordedAt;

    public WaterEntry() {
    }

    @Ignore
    public WaterEntry(int amountMl, @NonNull String note, long recordedAt) {
        this.amountMl = amountMl;
        this.note = note;
        this.recordedAt = recordedAt;
    }
}
