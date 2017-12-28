package com.project.android.wewin.data.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.project.android.wewin.data.local.db.dao.HomeWorkDao;
import com.project.android.wewin.data.local.db.entity.HomeWorkRoom;

/**
 * Created by pengming on 2017/11/24.
 */

@Database(entities = {HomeWorkRoom.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase{

    public abstract HomeWorkDao homeWorkDao();
}
