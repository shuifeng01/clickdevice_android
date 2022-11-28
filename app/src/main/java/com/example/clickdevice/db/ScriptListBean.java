package com.example.clickdevice.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "script2")
public class ScriptListBean {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private String createTime;

    private String updateTime;

    private String scriptJson;
    //循环次数，-1时无限循环
    private int forTime;

    public ScriptListBean() {
    }

    @Ignore
    public ScriptListBean(String name, String createTime, String updateTime, String scriptJson,int forTime) {
        this.name = name;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.scriptJson = scriptJson;
        this.forTime = forTime;
    }

    public int getForTime() {
        return forTime;
    }

    public void setForTime(int forTime) {
        this.forTime = forTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStringId(){
        return id+"";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getScriptJson() {
        return scriptJson;
    }

    public void setScriptJson(String scriptJson) {
        this.scriptJson = scriptJson;
    }
}
