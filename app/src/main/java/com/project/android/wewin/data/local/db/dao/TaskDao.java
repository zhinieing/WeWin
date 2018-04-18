package com.project.android.wewin.data.local.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.project.android.wewin.data.local.db.entity.TaskRoom;

import java.util.List;

/**
 * Created by zhoutao on 2018/4/18.
 */

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks")
    List<TaskRoom> loadAllTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaskList(List<TaskRoom> task);
}
