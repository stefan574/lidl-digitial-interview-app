package ro.rentea.lidldigitalinterviewapp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.rentea.lidldigitalinterviewapp.entity.validation.EmailAddressConstraint;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CUSTOMERS")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private char[] password;

    @Column(unique = true)
    @EmailAddressConstraint
    private String emailAddress;

}
