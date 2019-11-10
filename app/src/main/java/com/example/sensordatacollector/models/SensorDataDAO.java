package com.example.sensordatacollector.models;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SensorDataDAO {
    @Query("SELECT * FROM sensorData")
    List<SensorDataModel> getAllItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addItem(SensorDataModel item);

    @Query("DELETE from sensorData WHERE id = :id")
    int deleteItem(long id);

    @Query("SELECT * from sensorData WHERE id = :id")
    Cursor getItem(long id);

    @Query("DELETE FROM sensorData")
    int deleteTable();
}
