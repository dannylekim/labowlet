package servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.session.web.http.DefaultCookieSerializer;

@SpringBootApplication
public class LabowletApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabowletApplication.class, args);
    }

}
