package ro.rentea.lidldigitalinterviewapp.service;

import ro.rentea.lidldigitalinterviewapp.entity.Customer;

public interface CustomerService {

    Iterable<Customer> getAllCustomers();

    Customer getCustomerById(Long id);

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Long id, Customer dbCustomer);

    void deleteCustomer(Long id);

}
