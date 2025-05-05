package edu.npic.smartBuilding.features.user;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import edu.npic.smartBuilding.domain.User;
import edu.npic.smartBuilding.features.user.dto.*;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    GetAllUserResponse filterUser(String keywords, List<Integer> roleId, String status, List<Integer> signupMethodId, int pageNo, int pageSize);

    List<FullNameResponse> findAllFullName();

    IsOnlineResponse connectedUsers(Integer id, IsOnlineRequest isOnlineRequest);

    void deleteById(Integer id);

    UserDetailResponse updateUser(Integer id, UpdateUserRequest updateUserRequest) throws IOException, MessagingException;

    void create(CreateUser createUser) throws MessagingException;

    GetAllUserResponse findAll(int pageNo, int pageSize);

    void register(CreateUserRegister createUserRegister);

    GoogleAuthenticatorKey generate2FASecret(Integer userId);

    boolean validate2FACode(Integer userId, int code);

    void enable2FA(Integer userId);

    void disable2FA(Integer userId);

    User getUserById(Integer userId);

    ResponseEntity<?> find2faStatus();

    ResponseEntity<?> find2faSecretCode();

    UserDetailResponse findUserById(Integer id);

    Optional<User> findByEmail(String email);

    void registerUser(User newUser);
}
