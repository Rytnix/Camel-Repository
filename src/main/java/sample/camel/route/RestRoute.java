package sample.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.apache.camel.model.rest.RestBindingMode;
import sample.camel.component.Database;
import sample.camel.entity.Employee;
import javax.annotation.Resource;

@Component
public class RestRoute extends RouteBuilder {

    @Resource
    Database database;

    @Override
    public void configure()  {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .enableCORS(true);

        //TEST HELLO WORLD API
        rest("/api")
                .path("/test").consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces("application/json")
                .get().to("direct:hello");

        //FIND ALL EMPLOYEE API
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .description("Get Employee Details")
                .get("/findall").to("direct:show")
                .description("The list of all the Employees")
                .route().routeId("All-Employee")
                .bean(Database.class, "findAllEmployee")
                .endRest();

        //FIND EMPLOYEE BY ID
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/find/{id}").route().routeId("Employee-by-id")
                .bean(Database.class, "findEmployee(${header.id})");


        //CREATE EMPLOYEE API
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .post("/create")
                .type(Employee.class)
                .to("direct:saveData");


        //DELETE EMPLOYEE API
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .post("/delete/{id}")
                .type(Employee.class)
                .to("direct:deleteEmployee");


        //UPDATE EMPLOYEE
        rest("/api")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .post("/update/{id}")
                .type(Employee.class).to("direct:updateEmployee");


        // CONSUMERS
        from("direct:hello").transform().constant("Hello-World");
        from("direct:saveData").process(this::saveEmployeeAndSetToExchange);
        from("direct:deleteEmployee").process(this::deleteEmployeeAndSetToExchange);
        from("direct:updateEmployee").process(this::updateEmployeeAndSetToExchange);
    }


    //DELETE EXCHANGE
    private void deleteEmployeeAndSetToExchange(Exchange exchange) {
        Long empId = exchange.getMessage().getHeader("id", Long.class);
        Message message = new DefaultMessage(exchange.getContext());
        String deleteResponse = database.deleteEmployee(empId);
        message.setBody(deleteResponse);
        exchange.setMessage(message);


    }

    //UPDATE EXCHANGE
    private void updateEmployeeAndSetToExchange(Exchange exchange) {
        Long empId = exchange.getMessage().getHeader("id", Long.class);
        Employee empContent = exchange.getMessage().getBody(Employee.class);
        String updateResponse = database.updateEmployee(empId, empContent.getName(), empContent.getL_name(), empContent.getMob_num(),
                empContent.getEmail_id(), empContent.getJob_pos());
        Message message = new DefaultMessage(exchange.getContext());
        message.setBody(updateResponse);

    }

    // CREATE EXCHANGE
    private void saveEmployeeAndSetToExchange(Exchange exchange) {
        Employee emp = exchange.getMessage().getBody(Employee.class);
        String str = database.createEmployee(emp.getName(), emp.getL_name(), emp.getMob_num()
                , emp.getEmail_id(), emp.getJob_pos());
        Message message = new DefaultMessage(exchange.getContext());
        message.setBody(str);
        exchange.setMessage(message);
    }


}
