package com.example.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType type;
    //触发者id
    private int actorId;
    //触发对象
    private int entityType;

    private int entityId;

    private int entityOwnerId;
    //触发现场数据
    private Map<String, String> exts = new HashMap<String, String>();


    //jSON出来需要一个默认的构造函数
    public EventModel() {

    }

    //构造器
    public EventModel(EventType type) {
        this.type = type;
    }

    //getter and setter
    //把所有setter的都返回this EventModel;
    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }
}
