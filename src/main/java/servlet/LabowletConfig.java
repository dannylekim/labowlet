package servlet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import sessions.LabowletSessionRepository;


import java.util.concurrent.ConcurrentHashMap;

@EnableSpringHttpSession
@Configuration
public class LabowletConfig {

    @Bean
    public LabowletSessionRepository sessionRepository(){
        LabowletSessionRepository labowletSessionRepository = new LabowletSessionRepository(new ConcurrentHashMap<>());
        LabowletState applicationState = LabowletState.getInstance();
        applicationState.setLabowletSessionRepository(labowletSessionRepository);
        return labowletSessionRepository;
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver(){
        return HeaderHttpSessionIdResolver.xAuthToken();
    }
}
