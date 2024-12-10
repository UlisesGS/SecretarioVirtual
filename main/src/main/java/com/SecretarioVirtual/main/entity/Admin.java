package com.SecretarioVirtual.main.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Email
    private String email;

    private String password;

    private Long phone;

    @OneToMany
    private List<ScheduleRange> scheduleRangeList;
}
