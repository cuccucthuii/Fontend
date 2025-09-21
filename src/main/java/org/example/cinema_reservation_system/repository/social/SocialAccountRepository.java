package org.example.cinema_reservation_system.repository.social;

import org.example.cinema_reservation_system.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Integer> {

    // Tìm social account theo provider và provider user id
    Optional<SocialAccount> findByProviderAndProviderUserId(String provider, String providerUserId);

    // Tìm social account theo user id và provider
    @Query("SELECT sa FROM SocialAccount sa WHERE sa.userAccount.idTaiKhoan = :userId AND sa.provider = :provider AND sa.isActive = true")
    Optional<SocialAccount> findByUserAccountIdAndProvider(@Param("userId") Integer userId, @Param("provider") String provider);

    // Lấy tất cả social account của một user
    @Query("SELECT sa FROM SocialAccount sa WHERE sa.userAccount.idTaiKhoan = :userId AND sa.isActive = true")
    List<SocialAccount> findByUserAccountId(@Param("userId") Integer userId);

    // Kiểm tra xem user đã có social account với provider này chưa
    @Query("SELECT COUNT(sa) > 0 FROM SocialAccount sa WHERE sa.userAccount.idTaiKhoan = :userId AND sa.provider = :provider AND sa.isActive = true")
    boolean existsByUserAccountIdAndProvider(@Param("userId") Integer userId, @Param("provider") String provider);

    // Tìm social account theo email
    @Query("SELECT sa FROM SocialAccount sa WHERE sa.providerUserEmail = :email AND sa.isActive = true")
    List<SocialAccount> findByEmail(@Param("email") String email);

    // Xóa social account (soft delete)
    @Query("UPDATE SocialAccount sa SET sa.isActive = false WHERE sa.idSocial = :id")
    void deactivateSocialAccount(@Param("id") Integer id);
}
