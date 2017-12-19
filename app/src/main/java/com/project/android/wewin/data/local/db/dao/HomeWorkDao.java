package com.project.android.wewin.data.local.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.project.android.wewin.data.local.db.entity.HomeWork;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

@Dao
public interface HomeWorkDao {

    @Query("SELECT * FROM homeworks")
    List<HomeWork> loadAllHomeWorks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHomeWorkList(List<HomeWork> homeworks);


}
