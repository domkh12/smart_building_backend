package edu.npic.smartBuilding.features.user;

import edu.npic.smartBuilding.features.user.dto.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    UserDetailResponse findUserById(@PathVariable Integer id) {
        return userService.findUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/full-names")
    List<FullNameResponse> findAllFullName (){
        return userService.findAllFullName();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PatchMapping("/{id}/status")
    IsOnlineResponse connectedUsers (@PathVariable Integer id, @Valid @RequestBody IsOnlineRequest isOnlineRequest){
       return userService.connectedUsers(id, isOnlineRequest);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/filters")
    GetAllUserResponse filterUser( @RequestParam(required = false, defaultValue = "") String keywords,
                                     @RequestParam(required = false, defaultValue = "") List<Integer> roleId,
                                     @RequestParam(required = false, defaultValue = "") String status,
                                     @RequestParam(required = false, defaultValue = "") List<Integer> signupMethodId,
                                     @RequestParam(required = false, defaultValue = "1") int pageNo,
                                     @RequestParam(required = false, defaultValue = "20") int pageSize){
        return userService.filterUser(keywords, roleId, status, signupMethodId, pageNo, pageSize);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deleteByUuid(@PathVariable Integer id){
        userService.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    UserDetailResponse updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateUserRequest updateUserRequest) throws IOException, MessagingException {
        return userService.updateUser(id, updateUserRequest);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create( @Valid @RequestBody CreateUser createUser) throws MessagingException {
        userService.create(createUser);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registers")
    void register(@Valid @RequestBody CreateUserRegister createUserRegister){
        userService.register(createUserRegister);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    GetAllUserResponse findAll (
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
            ){
        return userService.findAll(pageNo, pageSize);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/2fa-status")
    ResponseEntity<?> find2faStatus(){
        return userService.find2faStatus();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/2fa-secret-code")
    ResponseEntity<?> find2faSecretCode(){
        return userService.find2faSecretCode();
    }
}
