package com.theshootapp.world.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


/**
 * Created by hamza on 26-Jun-18.
 */
@Database(entities = {MyContact.class},version = 1)
public abstract class SuggestionsDatabase  extends RoomDatabase {

    private static SuggestionsDatabase INSTANCE;

    public abstract ContactDao fileDao();

    public static SuggestionsDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SuggestionsDatabase.class, "suggestion-database").build();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }



}
