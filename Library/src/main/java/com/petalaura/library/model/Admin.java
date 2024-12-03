package com.petalaura.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name="admin",uniqueConstraints = @UniqueConstraint(columnNames = {"username","image"}))
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="admin_id")
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String image;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name="admin_roles",joinColumns = @JoinColumn(name="admin_id",referencedColumnName = "admin_id"),
    inverseJoinColumns = @JoinColumn(name="role_id"))
    private Collection<Role> roles;
}
