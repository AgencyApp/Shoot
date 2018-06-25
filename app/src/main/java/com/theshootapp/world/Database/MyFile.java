package com.theshootapp.world.Database;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hamza on 25-Jun-18.
 */
@Entity(tableName = "file")
public class MyFile {

    @ColumnInfo(name = "file_name")
    private String fileName;

    @PrimaryKey(autoGenerate = true)
    int fid;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }
}
