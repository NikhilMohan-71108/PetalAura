package com.petalaura.library.dto;


import com.petalaura.library.model.Category;
import com.petalaura.library.model.Product;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OfferDto {
    private Long id;
    @NotBlank(message = "Offer name is required")
    private String name;
    @NotBlank(message = " description is required")
    private String description;
    @Min(value = 0, message = "Discount must be at least 0")
    @Max(value = 100, message = "Discount cannot exceed 100")
    private  double discount;

    private String offerType;

    private Category category;

    private Product product;
    private boolean activated;

}
