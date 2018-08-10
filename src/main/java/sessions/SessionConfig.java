package sessions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;


import java.util.concurrent.ConcurrentHashMap;

@EnableSpringHttpSession
@Configuration
public class SessionConfig {

    @Bean
    public LabowletSessionRepository sessionRepository(){
        return new LabowletSessionRepository(new ConcurrentHashMap<>());
    }
}
