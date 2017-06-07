package org.yawlfoundation.cloud;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by gary on 16/04/2017.
 */
@Component
public class   XMLHelper {



    @Value("${log.executor.file.location}")
    private String fileLocation;



    XMLHelper(){

    }
    private Integer count=0;

    public Map<String,ProcessInstance> getInstancesFromLog() throws DocumentException, ParseException {
        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(new File(fileLocation));

        Element root = document.getRootElement();
        Element firstWorldElement = root.element("Process");

        Map<String,ProcessInstance> result=new HashMap<String, ProcessInstance>();
        for (Iterator iter = firstWorldElement.elementIterator(); iter.hasNext();)
        {
            Element e = (Element) iter.next();
            List<Element> workItems=e.elements("AuditTrailEntry");
            String id=e.attributeValue("id");
            ProcessInstance instance=new ProcessInstance(String.valueOf(count));
            Map<String,Long> assignTimeMap=new HashMap<String, Long>();


            for(Element element:workItems){
                String event=element.element("EventType").getText();
                String taskName=element.element("WorkflowModelElement").getText();
                if(taskName.startsWith("EVENT")){
                    continue;
                }
                String timeStamp=element.element("Timestamp").getText();
                timeStamp=timeStamp.substring(0,timeStamp.length()-6);
                SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date=format.parse(timeStamp);
                Long time=date.getTime();

                if(event.equals("assign")){
                    assignTimeMap.put(taskName,time);
                }

                if(event.equals("complete")){
                    if(assignTimeMap.get(taskName)==null){
                        instance.addTask(taskName,1000L);
                    }else {

                        instance.addTask(taskName, time - assignTimeMap.get(taskName) );

                    }
                }
            }
            instance.storeBackup();
            result.put(String.valueOf(count++),instance);

        }

       // normalize(result);
        return result;


    }





}
