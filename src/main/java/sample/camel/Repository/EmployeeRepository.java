package sample.camel.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sample.camel.entity.Employee;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee,Long> {

}
