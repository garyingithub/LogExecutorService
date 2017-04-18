package org.yawlfoundation.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EngineBasedClient;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedServer;

/**
 * Created by gary on 16/04/2017.
 */
@SpringBootApplication
public class LogExectorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogExectorServiceApplication.class, args);
    }

    @Bean
    ServletRegistrationBean testServletRegistrationBean(){
        return new ServletRegistrationBean(new InterfaceB_EnvironmentBasedServer(),"/test/*");
    }

}
