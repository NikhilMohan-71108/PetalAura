package com.petalaura.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    private Date orderDate;
    private Date deliveryDate;
    private String orderStatus;
    private double grandTotalPrize;
    private int quantity;
    private String paymentMethod;
    private double shippingFee;
    private String isAccept;
    private String paymentStatus;
    private String paymentId;
    private String returnMessage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    @JsonIgnore
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id",referencedColumnName = "address_id")
    @JsonIgnore
    private Address shippingAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @JsonIgnore
   private List<OrderDetails> orderDetailList;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", deliveryDate=" + deliveryDate +
                ", orderStatus='" + orderStatus + '\'' +
                ", grandTotalPrize=" + grandTotalPrize +
                ", quantity=" + quantity +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", shippingFee=" + shippingFee +
                ", isAccept=" + isAccept +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", returnMessage='" + returnMessage + '\'' +
                ", customerId=" + (customer != null ? customer.getCustomer_id() : "N/A") +
                ", shippingAddressId=" + (shippingAddress != null ? shippingAddress.getId() : "N/A") +
                '}';
    }
}

