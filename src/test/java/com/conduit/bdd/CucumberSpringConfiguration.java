package com.conduit.bdd;

import com.conduit.ConduitApplication;
import com.conduit.application.user.UserRepository;
import com.conduit.test.InMemoryUserRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = {ConduitApplication.class, CucumberSpringConfiguration.TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    @TestConfiguration(proxyBeanMethods = false)
    static class TestConfig {
        @Bean
        @Primary
        UserRepository userRepository() {
            return new InMemoryUserRepository();
        }
    }
}