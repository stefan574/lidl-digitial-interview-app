package ro.rentea.lidldigitalinterviewapp.entity.validation;

import org.junit.Test;
import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.exception.InvalidEmailAddressException;

public class CustomerValidatorTest {

    @Test
    public void validate_correctEmailAddress_noExceptionIsThrown() {
        Customer customer = new Customer();
        customer.setEmailAddress("a@a.com");

        CustomerValidator.validate(customer);
    }

    @Test(expected = InvalidEmailAddressException.class)
    public void validate_faultyEmailAddress_throwsInvalidEmailAddressException() {
        Customer customer = new Customer();
        customer.setEmailAddress("faultyEmailAddress");

        CustomerValidator.validate(customer);
    }

}
