package ro.rentea.lidldigitalinterviewapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.rentea.lidldigitalinterviewapp.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
