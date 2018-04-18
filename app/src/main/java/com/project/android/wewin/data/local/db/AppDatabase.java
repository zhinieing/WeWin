package com.project.android.wewin.data.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.project.android.wewin.data.local.db.dao.HomeWorkDao;
import com.project.android.wewin.data.local.db.dao.TaskDao;
import com.project.android.wewin.data.local.db.entity.HomeWorkRoom;
import com.project.android.wewin.data.local.db.entity.TaskRoom;

/**
 * Created by pengming on 2017/11/24.
 */

@Database(entities = {HomeWorkRoom.class, TaskRoom.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase{

    public abstract HomeWorkDao homeWorkDao();

    public abstract TaskDao taskDao();
}
