package com.example.clickdevice.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScriptListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScriptDataBean(ScriptListBean... scriptDataBeans);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateScriptDataBean(ScriptListBean... scriptDataBeans);

    @Delete
    void deleteScriptDataBean(ScriptListBean... scriptDataBeans);

    @Query("SELECT * FROM script2")
    List<ScriptListBean> loadAllScriptDataBean();

    @Query("SELECT * FROM script2")
    LiveData<List<ScriptListBean>> loadLiveDataOfAllScriptDataBean();

    //分页查询 size为每次查询数据的个数  page为第几个数据开始
    @Query("SELECT * FROM script2 order by id limit :size offset :page")
    LiveData<List<ScriptListBean>> loadLiveDataOfScriptDataBeanForPage(int size, int page);

    //分页查询 size为每次查询数据的个数  page为第几个数据开始
    @Query("SELECT * FROM script2 order by id limit :size offset :page")
    List<ScriptListBean> loadScriptDataBeanForPage(int size, int page);
}
