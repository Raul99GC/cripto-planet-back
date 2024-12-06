package com.rcgraul.cripto_planet.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rcgraul.cripto_planet.enums.OauthClientId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @UuidGenerator
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  private String firstName;

  private String lastName;

  @Column(nullable = true, unique = true)
  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private boolean accountNonLocked = true;
  private boolean accountNonExpired = true;
  private boolean credentialsNonExpired = true;
  private boolean enabled = true;

  private LocalDate credentialsExpiryDate;
  private LocalDate accountExpiryDate;

  @Embedded
  private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

  // ? seccion de OAuth y de SignUpMethod
  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isOAuth;
  @Enumerated(EnumType.STRING)
  @Column(nullable = true)
  private OauthClientId signUpMethod;

  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinColumn(name = "role_id", referencedColumnName = "id")
  @JsonBackReference
  @ToString.Exclude
  private Role role;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public User(String userName, String email, String password) {
    this.username = userName;
    this.email = email;
    this.password = password;
  }

  public User(String userName, String email) {
    this.username = userName;
    this.email = email;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    User other = (User) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

}
