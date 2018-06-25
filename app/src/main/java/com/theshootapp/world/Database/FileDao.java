package com.theshootapp.world.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by hamza on 25-Jun-18.
 */

@Dao
public interface FileDao {


    @Query("SELECT * FROM file")
    List<MyFile> getAll();

    @Insert
    void insertAll(MyFile... files);

    @Delete
    void delete(MyFile file);
}
