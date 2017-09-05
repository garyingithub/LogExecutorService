package org.yawlfoundation.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedServer;
import org.yawlfoundation.cloud.metrics.InfluxDBGaugeWriter;
/**
 * Created by gary on 16/04/2017.
 */

@SpringBootApplication
public class LogExectorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogExectorServiceApplication.class, args);
    }





    @Bean
    ServletRegistrationBean testServletRegistrationBean(InfluxDBGaugeWriter writer,XMLHelper helper){
        InterfaceB_EnvironmentBasedServer server=new InterfaceB_EnvironmentBasedServer();
        server.set_controller(new LogExectorController(writer,helper));
        return new ServletRegistrationBean(server,"/test/*");
    }

}
