package org.yawlfoundation.yawl.engine.interfce.interfaceB;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.springframework.boot.actuate.metrics.Metric;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.exceptions.YAWLException;
import org.yawlfoundation.cloud.metrics.InfluxDBGaugeWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.yawlfoundation.cloud.metrics.InfluxDBGaugeWriter;
/**
 * Created by gary on 26/05/2017.
 */
public
class ClusterInterfaceBWebsideController extends InterfaceBWebsideController {

    private Map<String,InterfaceBWebsideController> controllerMap=new HashMap<>();

    private Map<String,String> engineIdMap=new HashMap<>();

    private Map<String,String> sessionHandleMap=new HashMap<>();

    protected final Map<String,Long> last_task_timestamps=new HashMap<String, Long>();


    protected InfluxDBGaugeWriter writer;

    public WorkItemRecord checkOut(WorkItemRecord wir)
            throws IOException, YAWLException {
        String engineId=String.valueOf(Integer.valueOf(wir.engineId));
       // String caseId=wir.getCaseID();
      //  if(caseId.contains(".")){
      //      caseId=caseId.split("\\.")[0];
      //  }
      //  if(last_task_timestamps.get(caseId)!=null){
      //      writer.set(new Metric<Number>(engineId+"_time_span",System.currentTimeMillis()-last_task_timestamps.get(caseId)));
      //  }

        String engineAddress=wir.engineAddress;

        if(controllerMap.get(engineAddress)==null){
            controllerMap.put(engineAddress,new ClusterInterfaceBWebsideController());
            if(!engineAddress.startsWith("http")){
                controllerMap.get(engineAddress).setUpInterfaceBClient("http://"+engineAddress+ "/yawl/ib");
            }else {
                controllerMap.get(engineAddress).setUpInterfaceBClient(engineAddress+ "/org/yawlfoundation/yawl/ib");
            }
            sessionHandleMap.put(engineAddress,controllerMap.get(engineAddress).connect(this.engineLogonName,this.engineLogonPassword));
            engineIdMap.put(engineAddress,engineId);
        }

        long start=System.currentTimeMillis();
        WorkItemRecord record=controllerMap.get(engineAddress).checkOut(wir.getID(),sessionHandleMap.get(engineAddress));
        writer.set(new Metric<Number>(engineId+"_response_time", System.currentTimeMillis()-start));
        record.engineId=engineId;
        record.engineAddress=engineAddress;
        return record;

    }


    public String checkInWorkItem(WorkItemRecord workItemRecord, Element inputData,
                                  Element outputData, String logPredicate)
            throws IOException, JDOMException{

        String engineId=workItemRecord.engineId;
        String engineAddress=workItemRecord.engineAddress;

        long start=System.currentTimeMillis();
        String result=controllerMap.get(engineAddress).checkInWorkItem(workItemRecord.getID(),inputData,outputData,sessionHandleMap.get(engineAddress));
        writer.set(new Metric<Number>(engineId+"_response_time",System.currentTimeMillis()-start));
        String caseId=workItemRecord.getCaseID();
        if(caseId.contains(".")){
            caseId=caseId.split("\\.")[0];
        }
        caseId=engineId+" "+caseId;
        last_task_timestamps.put(caseId,System.currentTimeMillis());
        return result;
    }


    @Override
    public void handleEnabledWorkItemEvent(WorkItemRecord enabledWorkItem) {

    }

    @Override
    public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {

    }
}
