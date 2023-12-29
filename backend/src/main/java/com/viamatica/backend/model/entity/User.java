package com.viamatica.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viamatica.backend.util.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;
    private String username;
    private String password;

    @Column(name = "access_id")
    private String accessId;

    private String email;
    @Column(name = "nombre_usuario")
    private String name;
    @Column(name = "apellidos_usuario")
    private String lastName;
    @Column(name = "rol", length = 512)
    @ColumnDefault("'USER'")
    @Enumerated(EnumType.STRING)
    private Role role;

    // relación con las sesiones
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // previene la validación innecesaria del Content-Type
    @JsonBackReference
    private List<Session> sessions;

    @Column(name = "account_non_locked")
    @ColumnDefault("TRUE")
    private boolean accountNonLocked = true;
    @Column(name = "account_non_expired")
    @ColumnDefault("TRUE")
    private boolean accountNonExpired = true;
    @Column(name = "credentials_non_expired")
    @ColumnDefault("TRUE")
    private boolean credentialsNonExpired = true;
    @Column(name = "enabled")
    @ColumnDefault("TRUE")
    private boolean enabled = true;
    @Column(name = "failed_attempts")
    @ColumnDefault("0")
    private int failedAttempts = 0;
    @Column(name = "lock_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ColumnDefault("NULL")
    private Date lockTime = null;

}
