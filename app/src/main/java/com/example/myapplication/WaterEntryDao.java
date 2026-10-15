package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WaterEntryDao {
    @Insert
    void insert(WaterEntry entry);

    @Query("SELECT * FROM water_entries ORDER BY recordedAt DESC, id DESC")
    List<WaterEntry> getAll();
}
