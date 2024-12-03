package com.petalaura.library.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor

public class TotalPriceByPayment {
    @JsonProperty("payMethod")
  private String payMethod;
    @JsonProperty("amount")
  private double  amount;

  public TotalPriceByPayment(String payMethod, double  amount) {
        this.payMethod = payMethod;
       this.amount = amount;
  }
}
