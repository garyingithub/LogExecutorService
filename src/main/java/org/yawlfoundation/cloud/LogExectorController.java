package org.yawlfoundation.cloud;


import org.dom4j.DocumentException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.actuate.metrics.Metric;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.ClusterInterfaceBWebsideController;
import org.yawlfoundation.yawl.exceptions.YAWLException;
import org.yawlfoundation.cloud.metrics.InfluxDBGaugeWriter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gary on 16/04/2017.
 */

public class LogExectorController extends ClusterInterfaceBWebsideController {

    private Map<String, ProcessInstance> processInstanceMap;


    private final Logger logger= LoggerFactory.getLogger(LogExectorController.class);

    private Map<String,String> choicesStorage=new HashMap<>();
    public LogExectorController(InfluxDBGaugeWriter writer,XMLHelper helper) {

        super();
        this.writer=writer;
        try {
            this.processInstanceMap=helper.getInstancesFromLog();
        } catch (DocumentException | ParseException e) {
            throw new RuntimeException(e);
        }
        logger.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

    }
    private Executor executor=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
    public void handleEnabledWorkItemEvent(final WorkItemRecord workItemRecord) {

        String engineId=String.valueOf(Integer.valueOf(workItemRecord.engineId));
        String caseId=workItemRecord.getCaseID();

        if(caseId.contains(".")){
            caseId=caseId.split("\\.")[0];
        }
        caseId=engineId+" "+caseId;
      //  if(last_task_timestamps.get(caseId)!=null){
      //            writer.set(new Metric<Number>(engineId+"_time_span",System.currentTimeMillis()-last_task_timestamps.get(caseId)));
      //  }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final WorkItemRecord record;
                try {
                     record=checkOut(workItemRecord);
                } catch (IOException | YAWLException e) {
                    throw new RuntimeException(e);
                }


                String caseId=record.getCaseID();



                if(caseId.contains(".")){
                    caseId=caseId.split("\\.")[0];
                }
                String pos=String.valueOf(Integer.valueOf(caseId)%processInstanceMap.size());
                ProcessInstance instance =processInstanceMap.get(pos);


                String taskName=record.getTaskName();

                String choice="";
                if(taskName.contains("--")){
                    String[] choices=taskName.split("--")[1].split("-");

                    if(choices!=null&&choices.length>0) {

                        Pair<String, Long> next = instance.next(choices);
                        choice = next.getKey();
                    }
                }
                Long durationTime=instance.getDurationTime(taskName);

                final String finalChoice = choice;
                logger.info(caseId+" "+String.valueOf(pos)+" "+taskName + " ?" + String.valueOf(durationTime));

                try {
                    TimeUnit.MILLISECONDS.sleep(durationTime);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
                try {
                    checkInWorkItem(record,record.getDataList(),
                            getOutputData(record.getTaskName(), finalChoice),null);
                } catch (IOException | JDOMException e) {
                    throw new RuntimeException(e);
                }

            }
        });





    }

    public YParameter[] describeRequiredParams() {
        YParameter[] params = new YParameter[2];
        params[0] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        params[0].setDataTypeAndName("string", "splitTaskNames", XSD_NAMESPACE);
        params[0].setDocumentation("optional task name as follows");

        params[1] = new YParameter(null, YParameter._OUTPUT_PARAM_TYPE);
        params[1].setDataTypeAndName("string", "splitTaskNames", XSD_NAMESPACE);
        params[0].setDocumentation("optional task name as follows");

        return params;
    }

    public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {

    }

    private Element getOutputData(String taskName,String value){
        taskName=taskName.replace(" ","_");
        Element output=new Element(taskName);
        Element result=new Element("splitTaskNames");
        result.setText(value);
        output.addContent(result);
        return output;
    }
}
