package me.lolico.blog.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author lolico
 */
@Data
@Entity
@Table(name = "blog_users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, length = 64)
    private String name;

    @Column(name = "user_nick_name", length = 64)
    private String nickName;

    @JsonIgnore
    @Column(name = "user_password", nullable = false, length = 64)
    private String password;

    @Column(name = "user_email", nullable = false, length = 64)
    private String email;

    @Column(name = "user_registration_time")
    private Timestamp registrationTime;

    @Column(name = "user_last_login_time")
    private Timestamp lastLoginTime;

    @Column(name = "user_last_logout_time")
    private Timestamp lastLogoutTime;

    @Column(name = "user_registration_ip", length = 32)
    private String registrationIp;

    @Column(name = "user_last_login_ip", length = 32)
    private String lastLoginIp;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private Status status;

    @Column(name = "user_is_admin")
    private Boolean isAdmin;

    @Column(name = "user_image", length = 64)
    private String image;

    @Column(name = "user_phone", length = 11)
    private String phone;

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id")
    private Role role;

    public enum Status {
        VALID, LOCKING, WAITING_CONFIRMATION
    }

    // public enum Status {
    //     VALID("VALID"),
    //     LOCKING("LOCKING"),
    //     WAITING_CONFIRMATION("WAITING_CONFIRMATION");
    //
    //     private final String status;
    //
    //     Status(String status) {
    //         this.status = status;
    //     }
    //
    //     public static Status parseCode(String status) {
    //         for (Status s : Status.values()) {
    //             if (s.status.equalsIgnoreCase(status)) {
    //                 return s;
    //             }
    //         }
    //         return null;
    //     }
    // }

}
