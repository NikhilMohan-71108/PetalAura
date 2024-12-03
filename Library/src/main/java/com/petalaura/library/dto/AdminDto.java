package com.petalaura.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminDto {
@Size(min = 3, max = 10, message="Enter at-least 3 letters")
@NotBlank
private String firstName;
@NotBlank
private String lastName;
@NotBlank
private String username;
@Size(min=5,max=10,message = "password contains 5-10 characters")
private String password;
private String repeatPassword;

}
