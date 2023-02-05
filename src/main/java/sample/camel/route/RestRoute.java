/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.apache.camel.model.rest.RestBindingMode;
import sample.camel.component.Database;
import sample.camel.entity.Employee;

/**
 * A Camel route that calls the REST service using a timer
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class RestRoute extends RouteBuilder {

	@Autowired
	Database database;
	@Override
	public void configure() throws Exception {

		restConfiguration().component("servlet").bindingMode(RestBindingMode.json);

		//TEST HELLO WORLD API
		rest().path("/api/test").consumes("application/json").produces("application/json")
				.get().to("direct:hello");

		//FIND ALL EMPLOYEE API
		rest("/api").consumes(MediaType.APPLICATION_JSON_VALUE).produces(MediaType.APPLICATION_JSON_VALUE).description("Get Employe Details")
				.get("/findAllEmployee").to("direct:show").description("The list of all the Employees")
				.route().routeId("all-employee")
				.bean(Database.class, "findAllEmployee")
				.endRest();

		//FIND EMPLOYEE BY ID
        rest("/api").consumes(MediaType.APPLICATION_JSON_VALUE).produces(MediaType.APPLICATION_JSON_VALUE)
						.get("/findEmployee/{id}").route().routeId("Employe-by-id")
						.bean(Database.class,"findEmployee(${header.id})");


		//CREATE EMPLOYEE API
       rest("/api")
				.consumes(MediaType.APPLICATION_JSON_VALUE).produces(MediaType.APPLICATION_JSON_VALUE)
				.post("/create").type(Employee.class).to("direct:saveData");
	   //DELETE EMPLOYE API
		rest("/api")
				.consumes(MediaType.APPLICATION_JSON_VALUE).produces(MediaType.APPLICATION_JSON_VALUE)
						.post("/delete/{id}").type(Employee.class).to("direct:deleteEmployee");
      //UPDATE EMPLOYEE
		rest()
				.consumes(MediaType.APPLICATION_JSON_VALUE).produces(MediaType.APPLICATION_JSON_VALUE)
				.post("/update/{id}").type(Employee.class).to("direct:updateEmployee");
		from("direct:saveData").process(this::saveEmployeAndSetToExchange);
        from("direct:deleteEmployee").process(this::deleteEmployeAndSetToExchange);
		from("direct:updateEmployee").process(this::updateEmployeAndSetToExchange);
		}


		//DELETE EXCHANGE
	private void deleteEmployeAndSetToExchange(Exchange exchange) {
		Long empId = exchange.getMessage().getHeader("id", Long.class);
		Message message = new DefaultMessage(exchange.getContext());
		String deleteResponse = database.deleteEmploye(empId);
		message.setBody(deleteResponse);
		exchange.setMessage(message);


	}
	 //UPDATE EXCHANGE
		private void updateEmployeAndSetToExchange(Exchange exchange) {
			Long empId = exchange.getMessage().getHeader("id",Long.class);
			Employee empContent = exchange.getMessage().getBody(Employee.class);
			String updateResponse = database.updateEmployee(empId,empContent.getName(),empContent.getL_name(),empContent.getMob_num(),
					empContent.getEmail_id(),empContent.getJob_pos());
		    Message message = new DefaultMessage(exchange.getContext());
			message.setBody(updateResponse);

		}

 // CREATE EXCHANGE
	private void saveEmployeAndSetToExchange(Exchange exchange) {
		Employee emp =  exchange.getMessage().getBody(Employee.class);

		String str = database.createEmployee(emp.getName(),emp.getL_name(),emp.getMob_num()
		                                    ,emp.getEmail_id(),emp.getJob_pos());
		Message message = new DefaultMessage(exchange.getContext());
		message.setBody(str);
		exchange.setMessage(message);
	}


}
