package com.theshootapp.world.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hamza on 26-Jun-18.
 */
@Entity(tableName = "suggestion")
public class MyContact {

    @ColumnInfo(name = "User_id")
    String contactUid;


    @PrimaryKey(autoGenerate = true)
    int cid;

    public String getContactUid() {
        return contactUid;
    }

    public void setContactUid(String contactUid) {
        this.contactUid = contactUid;
    }
}
