package com.tecknobit.gluky;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static com.tecknobit.equinoxbackend.resourcesutils.ResourcesProvider.CUSTOM_CONFIGURATION_FILE_PATH;
import static com.tecknobit.equinoxbackend.resourcesutils.ResourcesProvider.DEFAULT_CONFIGURATION_FILE_PATH;

/**
 * The {@code Launcher} class is useful to launch <b>Gluky's backend service</b>
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@EnableAutoConfiguration
@PropertySources({
        @PropertySource(value = "classpath:" + DEFAULT_CONFIGURATION_FILE_PATH),
        @PropertySource(value = "file:" + CUSTOM_CONFIGURATION_FILE_PATH, ignoreResourceNotFound = true)
})
@EnableJpaRepositories("com.tecknobit.*")
@EntityScan("com.tecknobit.*")
@ComponentScan(value = {"com.tecknobit.gluky.*", "com.tecknobit.equinoxbackend.environment.configuration"})
@SpringBootApplication
public class Launcher {

    /**
     * Main method to start the backend
     *
     * @param args Custom arguments to share with {@link SpringApplication}
     */
    public static void main(String[] args) {
        EquinoxController.initEquinoxEnvironment(Launcher.class, args);
        SpringApplication.run(Launcher.class, args);
    }

}
