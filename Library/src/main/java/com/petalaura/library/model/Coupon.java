package com.petalaura.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Coupon {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "coupon_id")
private Long id;
    @NotBlank(message = "Coupon code is required")
    private String couponcode;
    @NotBlank(message = "Coupon description is required")
    private String couponDescription;
    @NotNull(message = "Offer percentage is required")
    @Min(value = 1, message = "Offer percentage must be at least 1")
    @Max(value = 100, message = "Offer percentage cannot exceed 51")
    private String offerPercentage;
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @NotNull(message = "Expire date is required")
    private LocalDate expireDate;
    private Double minimumOrderAmount;
    private Double maximumOfferAmount;
    @NotNull(message = "Coupon count is required")
   @Min(value = 0, message = "Coupon count must be at least 1")
    private int count;

    private boolean enabled;
    public boolean isExpired(){
        return ( this.expireDate.isBefore(LocalDate.now()) || this.startDate.isAfter(LocalDate.now()));
    }
}
