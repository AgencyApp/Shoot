package com.theshootapp.world.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by hamza on 26-Jun-18.
 */
@Dao
public interface ContactDao {

    @Query("SELECT * FROM suggestion")
    List<MyContact> getAll();

    @Insert
    void insertAll(MyContact... contacts);

    @Delete
    void delete(MyContact contact);
}
