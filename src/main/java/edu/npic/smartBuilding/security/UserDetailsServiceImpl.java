package edu.npic.smartBuilding.security;

import edu.npic.smartBuilding.domain.User;
import edu.npic.smartBuilding.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username is " + username);
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );
        System.out.println(user.getEmail());
        System.out.println(user.getFullName());
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUser(user);
        return customUserDetails;
    }
}
