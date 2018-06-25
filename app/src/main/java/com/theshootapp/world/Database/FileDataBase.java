package com.theshootapp.world.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by hamza on 25-Jun-18.
 */

@Database(entities = {MyFile.class},version = 1)
public abstract class FileDataBase extends RoomDatabase {

    private static FileDataBase INSTANCE;

    public abstract FileDao fileDao();

    public static FileDataBase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), FileDataBase.class, "user-database").build();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }
}