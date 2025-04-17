package edu.npic.smartBuilding.features.auth;

import edu.npic.smartBuilding.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<EmailVerification, Integer> {

}
