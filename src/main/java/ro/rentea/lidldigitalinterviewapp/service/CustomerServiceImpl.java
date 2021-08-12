package ro.rentea.lidldigitalinterviewapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.entity.validation.CustomerValidator;
import ro.rentea.lidldigitalinterviewapp.exception.CustomerAlreadyExistsException;
import ro.rentea.lidldigitalinterviewapp.exception.CustomerNotFoundException;
import ro.rentea.lidldigitalinterviewapp.repository.CustomerRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Iterable<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return getOptionalCustomerById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Optional<Customer> getOptionalCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer.getId() != null) {
            getOptionalCustomerById(customer.getId()).stream()
                    .findFirst()
                    .ifPresent((dbCustomer) -> {
                        throw new CustomerAlreadyExistsException(dbCustomer.getId());
                    });
        }

        CustomerValidator.validate(customer);

        tokenService.createToken(customer);

        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer customer) {
        CustomerValidator.validate(customer);

        return getOptionalCustomerById(id)
                .map(dbCustomer -> {
                    String oldEmailAddress = dbCustomer.getEmailAddress();

                    dbCustomer.setPassword(customer.getPassword());
                    dbCustomer.setEmailAddress(customer.getEmailAddress());

                    Customer updatedCustomer = customerRepository.save(dbCustomer);

                    tokenService.updateToken(oldEmailAddress, customer);

                    return updatedCustomer;
                })
                .orElseGet(() -> {
                    tokenService.createToken(customer);

                    return customerRepository.save(customer);
                });
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer dbCustomer = getOptionalCustomerById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.deleteById(id);

        String emailAddress = dbCustomer.getEmailAddress();
        tokenService.deleteByEmailAddress(emailAddress);
    }

}
