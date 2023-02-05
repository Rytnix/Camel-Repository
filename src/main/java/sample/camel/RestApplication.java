
package sample.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"sample.camel"})
public class RestApplication {


    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

}
 
