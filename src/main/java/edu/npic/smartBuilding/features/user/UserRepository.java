package edu.npic.smartBuilding.features.user;

import edu.npic.smartBuilding.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select count(u) from User u where u.createdAt between ?1 and ?2")
    long countUserByDate(LocalDateTime createdAtStart, LocalDateTime createdAtEnd);


    @Query("""
            select u from User u inner join u.rooms rooms inner join u.roles roles
            where u.id = ?1 and rooms.id in ?2 and upper(roles.name) = upper('USER')""")
    Optional<User> findUserByIdAndRoomIdsUserRole(Integer id, Collection<Long> ids);

    @Query("""
            select u from User u inner join u.roles roles inner join u.rooms rooms
            where u.id <> ?1
            and (upper(u.fullName) like upper(concat('%', ?2, '%'))
            or upper(u.email) like upper(concat('%', ?2, '%'))
            or upper(u.phoneNumber) like upper(concat('%', ?2, '%'))
            or upper(u.address) like upper(concat('%', ?2, '%')))
            and upper(roles.name) = upper('USER')
            and (u.signUpMethod.id in ?3 or ?3 is null)
            and (upper(u.status) = upper(?4) or ?4 = '')
            and (rooms.id in ?5 or ?5 is null)
            """)
    Page<User> filterUserByAdmin(Integer id, String keywords, Collection<Integer> ids1, String status, Collection<Long> ids2, Pageable pageable);


    @Query("""
            select count(u) from User u inner join u.roles roles inner join u.rooms rooms
            where u.id <> ?1 and upper(u.status) = upper(?2) and upper(roles.name) = upper(?3) and rooms.id in ?4""")
    Integer countUserByRoomId(Integer id, String status, String name, Collection<Long> ids);

    @Query("""
            select u from User u inner join u.roles roles inner join u.rooms rooms
            where u.id <> ?1 and upper(roles.name) = upper('USER') and rooms.id in ?2""")
    Page<User> findAllByRoomIdAndRoleUser(Integer id, Collection<Long> ids, Pageable pageable);

    @Query("select count(u) from User u inner join u.rooms rooms where rooms.id = ?1")
    long countUserByRoomId(Integer id);

    @Query("""
            select u from User u inner join u.roles roles
            where u.id <> ?1
            and (upper(u.fullName) like upper(concat('%', ?2, '%'))
            or upper(u.email) like upper(concat('%', ?2, '%'))
            or upper(u.phoneNumber) like upper(concat('%', ?2, '%'))
            or upper(u.address) like upper(concat('%', ?2, '%')))
            and (roles.id in ?3 or ?3 is null)
            and (u.signUpMethod.id in ?4 or ?4 is null)
            and (upper(u.status) = upper(?5) or ?5 = '')
            """)
    Page<User> filterUser(Integer id, String keywords, Collection<Integer> ids, Collection<Integer> ids1, String status, Pageable pageable);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Page<User> findByIdNot(Integer id, Pageable pageable);

    @Query("select count(u) from User u where upper(u.status) = upper('Pending')")
    Integer countPendingUser();

    @Query("select count(u) from User u where upper(u.status) = upper('Banned')")
    Integer countBannedUser();

    @Query("select count(u) from User u where u.id <> ?1 and upper(u.status) = upper('Active')")
    Integer countActiveUser(Integer id);

}
