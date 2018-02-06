package com.example.nowcoder.async;

import java.util.List;

public interface EventHandler {
    void doHandle(EventModel model);

    //这个handler关注某些特别种类的event,只要是发生了这些type的event都要处理
    List<EventType> getSupportEventTypes();
}
