
package sample.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@SpringBootApplication(scanBasePackages = {"sample.camel"})
public class RestApplication {


    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

}
 
