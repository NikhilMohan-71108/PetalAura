package com.petalaura.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDto {
 private Long id;
 @NotBlank(message = "Address cannot be Blank")
private String addressLine1;
 @NotBlank(message = "Address cannot be Blank")
private String addressLine2;
 @NotBlank(message = "City cannot be Blank")
private String city;
 @NotBlank(message = "district cannot be Blank")
 private  String district;
 @NotBlank(message = "state cannot be Blank")
private String state;
 @NotBlank(message = "country cannot be Blank")
private String country;
// @Min(value = 100000,message = "Min 6 letters")
// @Max(value = 999999,message = "Only 6 letters")
@NotBlank(message = "pincode cannot be Blank")
private String pinCode;
private boolean is_default;

}
