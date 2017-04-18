package org.yawlfoundation.cloud;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gary on 16/04/2017.
 */
public class ProcessInstance {

    private String id;
    private Map<String,Long> taskDurationTimeMap;


    public ProcessInstance(String id){
        this.id=id;
        this.taskDurationTimeMap=new HashMap<String, Long>();
    }

    public void addTask(String taskName,Long durationTime){
        this.taskDurationTimeMap.put(taskName,durationTime);
    }

    // 单位为毫秒
    public Long getDurationTime(String taskName){
        return taskDurationTimeMap.get(taskName);
    }



}
