package me.lolicom.blog.service.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author lolicom
 */
@Entity
@Table(name = "blog_users", schema = "mini_blog")
public class User {
    private Long id;
    private String name;
    private String nickName;
    private String password;
    private String email;
    private Timestamp registrationTime;
    private Timestamp lastLoginTime;
    private Timestamp lastLogoutTime;
    private String registrationIp;
    private String lastLoginIp;
    private Status status;
    private Boolean isAdmin;
    private String image;
    private String phone;
    
    public enum Status {
        VALID("VALID"),
        LOCKING("LOCKING"),
        WAITING_CONFIRMATION("WAITING_CONFIRMATION");
        
        private String status;
        
        Status(String status) {
            this.status = status;
        }
        
        
        public static Status parseCode(String status) {
            for (Status s : Status.values()) {
                if (s.status.equalsIgnoreCase(status)) {
                    return s;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return status;
        }
        
    }
    
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "user_name", nullable = false, length = 64)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Basic
    @Column(name = "user_nick_name", length = 64)
    public String getNickName() {
        return nickName;
    }
    
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
    @Basic
    @Column(name = "user_password", nullable = false, length = 64)
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Basic
    @Column(name = "user_email", nullable = false, length = 64)
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Basic
    @Column(name = "user_registration_time")
    public Timestamp getRegistrationTime() {
        return registrationTime;
    }
    
    public void setRegistrationTime(Timestamp registrationTime) {
        this.registrationTime = registrationTime;
    }
    
    @Basic
    @Column(name = "user_last_login_time")
    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    @Basic
    @Column(name = "user_last_logout_time")
    public Timestamp getLastLogoutTime() {
        return lastLogoutTime;
    }
    
    public void setLastLogoutTime(Timestamp lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }
    
    @Basic
    @Column(name = "user_registration_ip", length = 32)
    public String getRegistrationIp() {
        return registrationIp;
    }
    
    public void setRegistrationIp(String registrationIp) {
        this.registrationIp = registrationIp;
    }
    
    @Basic
    @Column(name = "user_last_login_ip", length = 32)
    public String getLastLoginIp() {
        return lastLoginIp;
    }
    
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
    
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    @Basic
    @Column(name = "user_is_admin")
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    @Basic
    @Column(name = "user_image", length = 64)
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    @Basic
    @Column(name = "user_phone", length = 11)
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(nickName, user.nickName) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(registrationTime, user.registrationTime) &&
                Objects.equals(lastLoginTime, user.lastLoginTime) &&
                Objects.equals(lastLogoutTime, user.lastLogoutTime) &&
                Objects.equals(registrationIp, user.registrationIp) &&
                Objects.equals(lastLoginIp, user.lastLoginIp) &&
                Objects.equals(status, user.status) &&
                Objects.equals(isAdmin, user.isAdmin) &&
                Objects.equals(image, user.image) &&
                Objects.equals(phone, user.phone);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, nickName, password, email, registrationTime, lastLoginTime, lastLogoutTime, registrationIp, lastLoginIp, status, isAdmin, image, phone);
    }
}
