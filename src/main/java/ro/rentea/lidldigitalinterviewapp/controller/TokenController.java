package ro.rentea.lidldigitalinterviewapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.rentea.lidldigitalinterviewapp.entity.Customer;
import ro.rentea.lidldigitalinterviewapp.service.TokenService;

@RestController
@RequestMapping(path = "/tokens")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<Integer> getTokenByEmailAddressAndPassword(@RequestBody Customer customer) {
        return ResponseEntity.ok().body(tokenService.getTokenByEmailAddress(customer));
    }

}
