package ro.rentea.lidldigitalinterviewapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.rentea.lidldigitalinterviewapp.entity.Token;

import java.util.List;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    List<Token> findByToken(int token);

    List<Token> findByEmailAddress(String emailAddress);

    List<Token> findByEmailAddressAndPassword(String emailAddress, char[] password);

}
