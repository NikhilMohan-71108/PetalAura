package com.petalaura.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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
    private boolean isAccept;
    private String paymentStatus;
    private String returnMessage;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id",referencedColumnName = "address_id")
    private Address shippingAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @JsonIgnore
   private List<OrderDetails> orderDetailList;
}

