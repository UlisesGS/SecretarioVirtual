package com.SecretarioVirtual.main.entities;


import com.SecretarioVirtual.main.entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String lastName;

    private String email;

    private Long phone;

    private LocalDate dateOfBirth;

    private String password;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;

    private boolean credentialsNonExpired;

    @ManyToMany
    private List<TermsConditions> acceptedTermsConditions;

    @OneToMany
    private List<Appointment> appointmentList;

    private Boolean enabled;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany
    private List<ScheduleRange> scheduleRangeList;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @PrePersist
    public void onPrePersist(){
        this.scheduleRangeList=new ArrayList<>();
    }
}
