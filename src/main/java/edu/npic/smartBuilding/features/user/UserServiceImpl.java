package edu.npic.smartBuilding.features.user;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import edu.npic.smartBuilding.base.Status;
import edu.npic.smartBuilding.domain.*;
import edu.npic.smartBuilding.features.auth.EmailVerificationRepository;
import edu.npic.smartBuilding.features.gender.GenderRepository;
import edu.npic.smartBuilding.features.role.RoleRepository;
import edu.npic.smartBuilding.features.room.RoomRepository;
import edu.npic.smartBuilding.features.signUpMethod.SignUpMethodRepository;
import edu.npic.smartBuilding.features.totp.TotpService;
import edu.npic.smartBuilding.features.user.dto.*;
import edu.npic.smartBuilding.mapper.UserMapper;
import edu.npic.smartBuilding.util.AuthUtil;
import edu.npic.smartBuilding.util.RandomOtp;
import edu.npic.smartBuilding.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SimpMessagingTemplate simpMessageTemplate;
    private final TotpService totpService;
    private final AuthUtil authUtil;
    private final SignUpMethodRepository signUpMethodRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSenderImpl mailSender;
    private final GenderRepository genderRepository;
    private final TemplateEngine templateEngine;
    private final RoomRepository roomRepository;

    @Value("${file-server.server-path}")
    private String serverPath;

    @Value("${file-server.base-uri}")
    private String baseUri;

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${spring.mail.username}")
    private String adminMail;

    @Override
    public UserDetailResponse findUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        );
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        );
        return Optional.of(user);
    }

    @Override
    public void registerUser(User newUser) {
        userRepository.save(newUser);
    }

    @Override
    public GetAllUserResponse filterUser(String keywords, List<Integer> roleId, String status, List<Integer> signupMethodId, int pageNo, int pageSize) {
        boolean isAdmin = authUtil.isAdminLoggedUser();
        boolean isManager = authUtil.isManagerLoggedUser();
        Integer loggedUserId = authUtil.loggedUserId();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();

        if (pageNo < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page number");
        } else if (pageSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page size");
        }

        List<Integer> roleIds = null;
        if (!roleId.isEmpty()) {
            roleIds = roleId;
        }

        List<Integer> signUpMethodIds = null;
        if (!signupMethodId.isEmpty()) {
            signUpMethodIds = signupMethodId;
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<User> users = Page.empty();
        Map<String, Integer> statusCount = new HashMap<>();

        if (isManager){
            users = userRepository.filterUser(loggedUserId, keywords, roleIds, signUpMethodIds, status, pageRequest);

            statusCount.put("Active", userRepository.countActiveUser(loggedUserId));
            statusCount.put("Pending", userRepository.countPendingUser());
            statusCount.put("Banned", userRepository.countBannedUser());
        }else if (isAdmin){
            users = userRepository.filterUserByAdmin(loggedUserId, keywords, signUpMethodIds, status, roomIds, pageRequest);

            statusCount.put("Active", userRepository.countUserByRoomId(loggedUserId, "ACTIVE", "USER", roomIds));
            statusCount.put("Pending", userRepository.countUserByRoomId(loggedUserId, "PENDING", "USER", roomIds));
            statusCount.put("Banned", userRepository.countUserByRoomId(loggedUserId, "BANNED", "USER", roomIds));
        }

        Page<UserDetailResponse> userDetailResponses = users.map(userMapper::toUserDetailResponse);
        return GetAllUserResponse.builder()
                .allUsers(userDetailResponses)
                .statusCount(statusCount)
                .build();
    }

    @Override
    public List<FullNameResponse> findAllFullName() {
//
//        String loggedUserUuid = authUtil.loggedUserUuid();
//        boolean isManager = authUtil.isManagerLoggedUser();
//        boolean isAdmin =  authUtil.isAdminLoggedUser();
//        List<String> verifiedUuid = authUtil.loggedUserSites();
//        Sort sort = Sort.by(Sort.Direction.DESC, "id");
//        List<User> user = new ArrayList<>();
//
//        if (isManager) {
//            user = userRepository.findAll(sort);
//        }else if(isAdmin) {
//            user = userRepository.findAllFullNameBySite(loggedUserUuid ,verifiedUuid.stream().findFirst().orElseThrow());
//        }
//
//        return userMapper.toFullNameResponse(user);
        return null;
    }

    @Override
    public IsOnlineResponse connectedUsers(Integer id, IsOnlineRequest isOnlineRequest) {
        System.out.println(isOnlineRequest.isOnline());
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        user.setIsOnline(isOnlineRequest.isOnline());
        userRepository.save(user);

        userMapper.fromUpdateStatusRequest(isOnlineRequest, user);

        simpMessageTemplate.convertAndSend(
                "/topic/online-status",
                IsOnlineResponse.builder()
                        .id(id)
                        .isOnline(user.getIsOnline())
                        .build()
        );
        return IsOnlineResponse.builder()
                .id(id)
                .isOnline(user.getIsOnline())
                .build();
    }

    @Override
    public void deleteById(Integer id) {
        boolean isManager = authUtil.isManagerLoggedUser();
        boolean isAdmin = authUtil.isAdminLoggedUser();

        if (isManager) {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
            );

            if (emailVerificationRepository.existsByUser(user)) {
                EmailVerification emailVerification = emailVerificationRepository.findByUser(user).orElseThrow();
                emailVerification.setUser(null);
            }

            userRepository.delete(user);
        } else if (isAdmin) {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
            );

            boolean hasUserRole = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("USER"));

            if (!hasUserRole) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin can only delete users with 'USER' role.");
            }

            if (emailVerificationRepository.existsByUser(user)) {
                EmailVerification emailVerification = emailVerificationRepository.findByUser(user).orElseThrow();
                emailVerification.setUser(null);
            }

            userRepository.delete(user);
        }

    }

    @Override
    public UserDetailResponse updateUser(Integer id, UpdateUserRequest updateUserRequest) throws IOException, MessagingException {

        boolean isManager = authUtil.isManagerLoggedUser();
        boolean isAdmin = authUtil.isAdminLoggedUser();

        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found")
        );

        if (userRepository.existsByEmail(updateUserRequest.email()) && !user.getEmail().equals(updateUserRequest.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (userRepository.existsByPhoneNumber(updateUserRequest.phoneNumber()) && !user.getPhoneNumber().equals(updateUserRequest.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        userMapper.fromUpdateUserRequest(updateUserRequest, user);

        if (!updateUserRequest.isVerified()) {
            user.setStatus(String.valueOf(Status.Pending));

            if (emailVerificationRepository.existsByUser(user)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User not verify yet!");
            } else {
                sendEmailVerification(user, 15);
            }
        }

        if (!updateUserRequest.isDeleted()) {
            user.setStatus(String.valueOf(Status.Active));
        } else {
            user.setStatus(String.valueOf(Status.Banned));
        }

        Gender gender = genderRepository.findById(updateUserRequest.genderId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Gender not found!"
        ));

        user.setGender(gender);

        if (updateUserRequest.profileImage() != null) {
//            deleteImageFile(user.getProfileImage());
            user.setProfileImage(updateUserRequest.profileImage());
        }

        List<Room> rooms = roomRepository.findByIdIn(updateUserRequest.roomId());
        user.setRooms(rooms);

        if (isManager) {

            List<Role> roles = roleRepository.findByIdIn(updateUserRequest.roleId());
            user.setRoles(roles);

            userRepository.save(user);
            return userMapper.toUserDetailResponse(user);
        } else if (isAdmin) {

            List<Role> roles = roleRepository.findRoleUser();
            user.setRoles(roles);

            userRepository.save(user);
            return userMapper.toUserDetailResponse(user);
        }
        return null;

    }

    @Override
    public void create(CreateUser createUser) throws MessagingException {
        boolean isManager = authUtil.isManagerLoggedUser();
        boolean isAdmin = authUtil.isAdminLoggedUser();

        if (userRepository.existsByEmail(createUser.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (userRepository.existsByPhoneNumber(createUser.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        if (!createUser.password().equals(createUser.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password does not match");
        }

        if (isManager) {

            Gender gender = genderRepository.findById(createUser.genderId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gender not found")
            );

            User user = userMapper.fromCreateUser(createUser);

            List<Room> rooms = createUser.roomId().stream().map(
                    roomId -> roomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!"))
            ).toList();

            List<Role> roles = createUser.roleId().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!")))
                    .collect(Collectors.toList());

            user.setRooms(rooms);
            user.setUuid(UUID.randomUUID().toString());
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(createUser.password()));
            user.setCreatedAt(LocalDateTime.now());
            user.setIsAccountNonLocked(true);
            user.setIsAccountNonExpired(true);
            user.setIsCredentialsNonExpired(true);
            user.setStatus(String.valueOf(Status.Active));
            user.setSignUpMethod(signUpMethodRepository.findByName("CUSTOM").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sign up method not found")));
            user.setIsOnline(false);
            user.setIsTwoFactorEnabled(false);
            user.setGender(gender);
            user.setIsDeleted(!createUser.isVerified());
            userRepository.save(user);

            if (!createUser.isVerified()) {
                sendEmailVerification(user, 30);
            }

        } else if (isAdmin) {

            Gender gender = genderRepository.findById(createUser.genderId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gender not found")
            );

            List<Room> rooms = createUser.roomId().stream().map(
                    roomId -> roomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!"))
            ).toList();

            User user = userMapper.fromCreateUser(createUser);
            List<Role> roles = roleRepository.findAll().stream().filter(role -> role.getName().equalsIgnoreCase("USER")).toList();

            user.setUuid(UUID.randomUUID().toString());
            user.setRooms(rooms);
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(createUser.password()));
            user.setCreatedAt(LocalDateTime.now());
            user.setIsAccountNonLocked(true);
            user.setIsAccountNonExpired(true);
            user.setIsCredentialsNonExpired(true);
            user.setStatus(String.valueOf(Status.Active));
            user.setSignUpMethod(signUpMethodRepository.findByName("CUSTOM").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sign up method not found")));
            user.setIsOnline(false);
            user.setIsTwoFactorEnabled(false);
            user.setGender(gender);
            user.setIsDeleted(!createUser.isVerified());
            userRepository.save(user);

            if (!createUser.isVerified()) {
                sendEmailVerification(user, 30);
            }
        }
    }

    @Override
    public GetAllUserResponse findAll(int pageNo, int pageSize) {
        Integer loggedUserId = authUtil.loggedUserId();
        boolean isManager = authUtil.isManagerLoggedUser();
        boolean isAdmin = authUtil.isAdminLoggedUser();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();

        if (pageNo < 1 || pageSize < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number or page size must be greater than zero"
            );
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<User> users = Page.empty();
        Map<String, Integer> statusCount = new HashMap<>();

        if (isManager) {
            users = userRepository.findByIdNot(loggedUserId, pageRequest);
            statusCount.put("Active", userRepository.countActiveUser(loggedUserId));
            statusCount.put("Pending", userRepository.countPendingUser());
            statusCount.put("Banned", userRepository.countBannedUser());
        } else if (isAdmin) {
            users = userRepository.findAllByRoomIdAndRoleUser(loggedUserId, roomIds, pageRequest);
            statusCount.put("Active", userRepository.countUserByRoomId(loggedUserId, "ACTIVE", "USER", roomIds));
            statusCount.put("Pending", userRepository.countUserByRoomId(loggedUserId, "PENDING", "USER", roomIds));
            statusCount.put("Banned", userRepository.countUserByRoomId(loggedUserId, "BANNED", "USER", roomIds));
        }

        Page<UserDetailResponse> userDetailResponses = users.map(userMapper::toUserDetailResponse);
        return GetAllUserResponse.builder()
                .allUsers(userDetailResponses)
                .statusCount(statusCount)
                .build();
    }

    @Override
    public void register(CreateUserRegister createUserRegister) {

        if (userRepository.existsByEmail(createUserRegister.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists!"
            );
        }

        if (!createUserRegister.password().equals(createUserRegister.confirmPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passwords and confirm passwords do not match!"
            );
        }

        User user = userMapper.fromCreateUserRegister(createUserRegister);
        user.setUuid(UUID.randomUUID().toString());
        user.setIsVerified(false);
        user.setPassword(passwordEncoder.encode(createUserRegister.password()));
        user.setProfileImage("default-avatar.png");
        user.setCreatedAt(LocalDateTime.now());
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsDeleted(false);

        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("STAFF").build());
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public GoogleAuthenticatorKey generate2FASecret(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        );
        GoogleAuthenticatorKey key = totpService.generateSecret();
        user.setTwoFactorSecret(key.getKey());
        userRepository.save(user);
        return key;
    }

    @Override
    public boolean validate2FACode(Integer userId, int code) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        );
        return totpService.verifyCode(user.getTwoFactorSecret(), code);
    }

    @Override
    public void enable2FA(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        );
        user.setIsTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disable2FA(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        );
        user.setIsTwoFactorEnabled(false);
        userRepository.save(user);
    }

    @Override
    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
        );
    }

    @Override
    public ResponseEntity<?> find2faStatus() {
        User user = authUtil.loggedUser();
        if (user != null) {
            return ResponseEntity.ok().body(Map.of("is2faEnabled", user.getIsTwoFactorEnabled()));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
    }

    @Override
    public ResponseEntity<?> find2faSecretCode() {
        User user = authUtil.loggedUser();
        if (user != null) {
            return ResponseEntity.ok().body(Map.of("twoFASecretCode", user.getTwoFactorSecret()));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
    }

    private void deleteImageFile(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            Path path = Path.of(serverPath + fileName);
            Files.deleteIfExists(path);
        }
    }

    private void sendEmailVerification(User user, Integer expireMinute) throws MessagingException {
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail(user.getEmail());
        emailVerification.setExpiryTime(LocalDateTime.now().plusMinutes(expireMinute));
        emailVerification.setUser(user);
//        emailVerification.setVerificationCode(RandomOtp.generateSecurityCode());
        emailVerification.setToken(RandomUtil.randomUuidToken());
        emailVerificationRepository.save(emailVerification);

        String verifyUrl = backendUrl + "/verify?token=" + emailVerification.getToken();
        Context context = new Context();
        context.setVariable("email", emailVerification.getEmail());
        context.setVariable("expire", emailVerification.getExpiryTime());
        context.setVariable("tokenUrl", verifyUrl);
        String htmlContent = templateEngine.process("emailTemplate", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Email Verification - SPS");
        helper.setTo(user.getEmail());
        helper.setFrom(adminMail);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

}
