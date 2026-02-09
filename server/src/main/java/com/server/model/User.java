package com.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * Model class representing an application user.
 *
 * Contains information about the user - their id, username, address information,
 * password (hashed) and authorities (user roles).
 */
public class User {
    // Adding field for profile image persistence

   private int id;
   private String name;
   private String displayName;
   private String email;
   @JsonIgnore
   private String hashedPassword;
   private String role;
   private String profileImage;


   public User() {
   }

   public User(int id, String name, String displayName, String email, String hashedPassword, String role, String profileImage) {
      this.id = id;
      this.name = name;
      this.displayName = displayName;
      this.email = email;
      this.hashedPassword = hashedPassword;
      this.role = role;
      this.profileImage = profileImage;
   }

   public User(String name, String displayName, String email, String hashedPassword, String role) {
      this.name = name;
      this.displayName = displayName;
      this.email = email;
      this.hashedPassword = hashedPassword;
      this.role = role;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDisplayName() {
      return displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getHashedPassword() {
      return hashedPassword;
   }

   public void setHashedPassword(String hashedPassword) {
      this.hashedPassword = hashedPassword;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public String getProfileImage() {
      return profileImage;
   }

   public void setProfileImage(String profileImage) {
      this.profileImage = profileImage;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return id == user.id && Objects.equals(name, user.name) && Objects.equals(displayName, user.displayName) && Objects.equals(email, user.email) && Objects.equals(hashedPassword, user.hashedPassword) && Objects.equals(role, user.role);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, name, displayName, email, hashedPassword, role);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", displayName='" + displayName + '\'' +
              ", email='" + email + '\'' +
              ", hashedPassword='" + hashedPassword + '\'' +
              ", role='" + role + '\'' +
              '}';
   }
}
