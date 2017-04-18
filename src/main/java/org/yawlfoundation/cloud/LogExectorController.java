package org.yawlfoundation.cloud;

import org.dom4j.DocumentException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;
import org.yawlfoundation.yawl.exceptions.YAWLException;

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

public class LogExectorController extends InterfaceBWebsideController {

    Map<String, ProcessInstance> processInstanceMap;
    private String _handle = null;

    private boolean connected() {
        return _handle != null ;
    }


    public LogExectorController() {
        super();
        try {
            this.processInstanceMap=XMLHelper.XML_HELPER.getInstancesFromLog();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void handleEnabledWorkItemEvent(final WorkItemRecord workItemRecord) {

        if (! connected()) try {
            _handle = connect("test:1", "test");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final WorkItemRecord record;
        try {
            record=checkOut(workItemRecord.getID(),_handle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (YAWLException e) {
            throw new RuntimeException(e);
        }

        String caseId=record.getCaseID();
        if(caseId.contains(".")){
            caseId=caseId.split("\\.")[0];
        }
        String taskName=record.getTaskName();

        Long durationTime=processInstanceMap.get(caseId).getDurationTime(taskName);

        ScheduledExecutorService executor= Executors.newScheduledThreadPool(5);
        executor.schedule(new Runnable() {
            public void run() {

                try {

                    System.out.println(checkInWorkItem(record.getID(),getOutputData(record.getTaskName()),
                            getOutputData(record.getTaskName()),"",_handle));
                    System.out.println(record.getTaskName());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JDOMException e) {
                    e.printStackTrace();
                }
            }
        },100, TimeUnit.MILLISECONDS);


    }

    public YParameter[] describeRequiredParams() {
        YParameter[] params = new YParameter[2];
        params[0] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        params[0].setDataTypeAndName("unsignedLong", "data", XSD_NAMESPACE);
        params[0].setDocumentation("The status message to post to Twitter");

        params[1] = new YParameter(null, YParameter._OUTPUT_PARAM_TYPE);
        params[1].setDataTypeAndName("unsignedLong", "data", XSD_NAMESPACE);
        params[1].setDocumentation("The status message to post to Twitter");
        return params;
    }

    public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {

    }

    private Element getOutputData(String taskName){
        Element output=new Element(taskName);
        Element result=new Element("data");
        result.setText("1");
        output.addContent(result);
        return output;
    }
}
