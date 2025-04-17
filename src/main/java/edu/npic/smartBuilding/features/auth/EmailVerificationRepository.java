package edu.npic.smartBuilding.features.auth;

import edu.npic.smartBuilding.domain.EmailVerification;
import edu.npic.smartBuilding.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {
    Optional<EmailVerification> findByUser(User user);

    Boolean existsByUser(User user);

}
