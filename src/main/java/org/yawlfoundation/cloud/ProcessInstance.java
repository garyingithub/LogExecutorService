package org.yawlfoundation.cloud;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by gary on 16/04/2017.
 */
public class ProcessInstance {



    private String id;

    private List<Pair<String,Long>> taskDurationList=new ArrayList<>();
    private List<Pair<String,Long>> backup=new ArrayList<>();

    final private Logger logger= LoggerFactory.getLogger(ProcessInstance.class);

    public void storeBackup(){

        for(Pair<String,Long> pair:taskDurationList){
            backup.add(pair);
        }

    }

    public void restore(){

        taskDurationList.clear();
        for(Pair<String,Long> pair:backup){
            taskDurationList.add(pair);
        }

    }
    public ProcessInstance(String id){
        this.id=id;
        this.taskDurationList=new ArrayList<>();
    }

    public void addTask(String taskName,Long durationTime){
        this.taskDurationList.add(new Pair<>(taskName, durationTime/100 ));

    }

    // 单位为毫秒
    public Long getDurationTime(String taskName){
        Pair<String ,Long> temp=null;
        for(Pair<String,Long> pair:this.taskDurationList){
            if(pair.getKey().equals(taskName)){
                temp=pair;
                break;
            }
        }
        Long result;
        if(temp==null){
            result=100L;
            logger.info("Wrong log "+taskName);
            restore();
        }else {
            result = temp.getValue();
            this.taskDurationList.remove(temp);
            if(this.taskDurationList.size()==0){
                this.restore();
            }
        }

        return result;
    }


    public Pair<String, Long> next(String[] taskNames){

        for(Pair<String,Long> pair:this.taskDurationList){
            for(String taskName:taskNames) {
                if (taskName.equals(pair.getKey())) {
                    return pair;
                }
            }
        }
        this.restore();
        throw new RuntimeException("No such task");
    }



}
