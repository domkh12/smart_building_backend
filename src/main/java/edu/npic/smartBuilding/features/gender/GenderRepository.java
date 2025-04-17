package edu.npic.smartBuilding.features.gender;

import edu.npic.smartBuilding.domain.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Integer> {
   Optional<Gender> findByUuid(String uuid);
}
