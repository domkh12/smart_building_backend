package edu.npic.smartBuilding.features.signUpMethod;

import edu.npic.smartBuilding.features.signUpMethod.dto.SignUpMethodResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sign-up-methods")
@RequiredArgsConstructor
public class SignUpMethodController {

    private final SignUpMethodService signUpMethodService;

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<SignUpMethodResponse> findAll(){
        return signUpMethodService.findAll();
    }

}
