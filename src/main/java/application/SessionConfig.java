package application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import sessions.LabowletSessionRepository;


import java.util.concurrent.ConcurrentHashMap;

/***
 * This class handles all configurations that deal with the application state and sessions.
 *
 *
 */
@EnableSpringHttpSession
@Configuration
public class SessionConfig {

    /***
     * Instantiates a session repository and instantiates (this will most likely be the first time) the singleton which
     * will hold the entire game state. It will then keep track of the session repository.
     *
     * Generally Spring Sessions does not want you to modify the session repository and let interactions via the
     * HttpSession work with it, however, for our needs, we will be able to expire sessions at will. However, keeping
     * that in mind, if there is a solution to interact through the HttpSession and expiration then that will be the
     * preferred method.
     *
     * @return
     */
    @Bean
    public LabowletSessionRepository sessionRepository(){
        LabowletSessionRepository labowletSessionRepository = new LabowletSessionRepository(new ConcurrentHashMap<>());
        LabowletState applicationState = LabowletState.getInstance();
        applicationState.setLabowletSessionRepository(labowletSessionRepository);
        return labowletSessionRepository;
    }

    /***
     *
     * This allows for the session ID to be placed in the header of the response rather than as a cookie
     *
     * @return the HttpSessionIdResolver with a specified strategy, in this case x-auth-token in the header.
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver(){
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

}
