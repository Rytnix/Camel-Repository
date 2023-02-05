package sample.camel.component;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sample.camel.Repository.EmployeeRepository;
import sample.camel.entity.Employee;

import java.util.Optional;

@Component
public class Database {

//    @Resource
    @Autowired
EmployeeRepository employeeRepository;


    public Iterable<Employee> findAllEmployee() {
        System.out.println(employeeRepository.findAll());
        return employeeRepository.findAll();
    }

    public Employee findEmployee(Long id) {
        return employeeRepository.findById(id).get();
    }

    public String createEmployee(String name,
                                 String l_name,
                                 String mob_num,
                                 String email_id,
                                 String job_pos) {
        Employee emp = new Employee();

        emp.setName(name);
        emp.setL_name(l_name);
        emp.setMob_num(mob_num);
        emp.setEmail_id(email_id);
        emp.setJob_pos(job_pos);
        employeeRepository.save(emp);
        return "employee "+name+ " created";
    }

    public String updateEmployee(long emp_id,
            String f_name,
            String l_name,
            String mob_num,
            String email_id,
            String job_pos) {
        Optional<Employee> emp = employeeRepository.findById(emp_id);
        if (f_name != null)
            emp.get().setName(f_name);
        if (l_name != null)
            emp.get().setL_name(l_name);
        if (mob_num != null)
            emp.get().setMob_num(mob_num);
        if (email_id != null)
            emp.get().setMob_num(email_id);
        if (job_pos != null)
            emp.get().setJob_pos(job_pos);
        employeeRepository.save(emp.get());
        return "employee Updated";

    }

    public String deleteEmploye(Long empId) {
        employeeRepository.deleteById(empId);
        return "Employee with id: "+empId+" Deleted";
    }
}
