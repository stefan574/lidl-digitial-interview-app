package ro.rentea.lidldigitalinterviewapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.entity.Token;
import ro.rentea.lidldigitalinterviewapp.exception.IncorrectEmailAddressOrPasswordException;
import ro.rentea.lidldigitalinterviewapp.exception.InvalidTokenException;
import ro.rentea.lidldigitalinterviewapp.exception.TokenNotFoundException;
import ro.rentea.lidldigitalinterviewapp.repository.TokenRepository;

import java.util.Random;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public int getTokenByEmailAddress(@RequestBody Customer customer) {
        Token dbToken =
                tokenRepository.findByEmailAddressAndPassword(customer.getEmailAddress(), customer.getPassword())
                        .stream()
                        .findFirst()
                        .orElseThrow(IncorrectEmailAddressOrPasswordException::new);

        int token = new Random().nextInt(1000 - 100) + 100;
        dbToken.setToken(token);
        tokenRepository.save(dbToken);

        return token;
    }

    @Override
    public Token createToken(Customer customer) {
        Token token = new Token();
        token.setPassword(customer.getPassword());
        token.setEmailAddress(customer.getEmailAddress());

        return tokenRepository.save(token);
    }

    @Override
    public Token updateToken(String oldEmailAddress, Customer customer) {
        return tokenRepository.findByEmailAddress(oldEmailAddress).stream()
                .findFirst()
                .map(dbToken -> {
                    dbToken.setPassword(customer.getPassword());
                    dbToken.setEmailAddress(customer.getEmailAddress());

                    return tokenRepository.save(dbToken);
                })
                .orElseThrow(() -> new TokenNotFoundException(oldEmailAddress));
    }

    @Override
    public void deleteByEmailAddress(String emailAddress) {
        Token dbToken = tokenRepository.findByEmailAddress(emailAddress).stream()
                .findFirst()
                .orElseThrow(() -> new TokenNotFoundException(emailAddress));

        tokenRepository.delete(dbToken);
    }

    @Override
    public void doesTokenExist(int token) {
        tokenRepository.findByToken(token).stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTokenException(token));
    }

}
