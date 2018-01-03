package com.project.android.wewin.data.local.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.project.android.wewin.data.remote.model.HomeWork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public class AppDatabaseManager {
    private static final String DATABASE_NAME = "homeworks-db";

    private final MutableLiveData<Boolean> mIsLoadingHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<List<HomeWork>> mHomeWorkList = new MutableLiveData<>();

    private static AppDatabaseManager INSTANCE = null;

    private AppDatabase mDB = null;

    private AppDatabaseManager() {
    }

    public static AppDatabaseManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AppDatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppDatabaseManager();
                }
            }
        }
        return INSTANCE;
    }

    public void createDB(Context context) {
        new AsyncTask<Context, Void, Void>() {
            @Override
            protected Void doInBackground(Context... params) {
                Context context = params[0].getApplicationContext();
                mDB = Room.databaseBuilder(context,
                        AppDatabase.class, DATABASE_NAME).build();
                return null;
            }
        }.execute(context.getApplicationContext());
    }

    public void insertHomeWorkList(final List<HomeWork> homeWorkRoomList) {
        /*new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                mDB.beginTransaction();
                try {
                    mDB.homeWorkDao().insertHomeWorkList(homeWorkRoomList);
                    mDB.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDB.endTransaction();
                }
                return null;
            }
        }.execute();*/
    }

    public LiveData<List<HomeWork>> loadHomeWorkList() {
        mIsLoadingHomeWorkList.setValue(true);
        /*new AsyncTask<Void, Void, List<HomeWork>>() {
            @Override
            protected List<HomeWork> doInBackground(Void... voids) {
                List<HomeWork> results = new ArrayList<>();
                mDB.beginTransaction();
                try {
                    results.addAll(mDB.homeWorkDao().loadAllHomeWorks());
                    mDB.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDB.endTransaction();
                }
                return results;
            }

            @Override
            protected void onPostExecute(List<HomeWork> aVoid) {
                super.onPostExecute(aVoid);
                mIsLoadingHomeWorkList.setValue(false);
                mHomeWorkList.setValue(aVoid);
            }

            @Override
            protected void onCancelled(List<HomeWork> aVoid) {
                super.onCancelled(aVoid);
                mIsLoadingHomeWorkList.setValue(false);
            }
        }.execute();*/
        return mHomeWorkList;
    }

    public LiveData<Boolean> isLoadingHomeWorkList() {
        return mIsLoadingHomeWorkList;
    }

}
