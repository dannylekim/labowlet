
import application.LabowletApplication;
import application.SessionConfig;
import application.controllers.PlayerController;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sessions.LabowletSessionRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Filter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LabowletApplication.class)
@ContextConfiguration(classes = SessionConfig.class)
@WebAppConfiguration
public class SessionTest {

    @InjectMocks
    private PlayerController playerController;

    @Autowired
    private WebApplicationContext webApplicationContext;


    private MockMvc mockMvc;

    @InjectMocks
    private MockHttpSession session;

    @Test
    public void test() throws Exception {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        MockHttpServletRequest reqt = new MockHttpServletRequest();
        reqt.setSession(session);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(playerController).build();

        MockHttpServletRequestBuilder reqBuilder = MockMvcRequestBuilders
                .post("/player")
                .param("name", "Tester")
                .session(session);

        ResultActions result = mockMvc.perform(reqBuilder);
        result.andExpect(status().isOk());
    }
}
