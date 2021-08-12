package ro.rentea.lidldigitalinterviewapp.service;

import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.entity.Token;

public interface TokenService {

    int getTokenByEmailAddress(Customer customer);

    Token createToken(Customer customer);

    Token updateToken(String oldEmailAddress, Customer customer);

    void deleteByEmailAddress(String emailAddress);

    void doesTokenExist(int token);

}
