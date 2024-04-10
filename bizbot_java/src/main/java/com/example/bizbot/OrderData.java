package com.example.bizbot;

import java.util.Date;

public class OrderData {
    private Integer id;
    private Integer orderNumber;
    private Date orderDate;
    private String phone;
    private String orderStatus;
    private String size;
    private String Colors;
    private Integer productID;
    private Integer quantity;
    public OrderData(Integer id, Integer orderNumber,
                        Date orderDate, String phone, String orderStatus, Integer productID, Integer quantity,
                     String size, String Colors){
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.phone = phone;
        this.orderStatus = orderStatus;
        this.productID = productID;
        this.size = size;
        this.quantity = quantity;
        this.Colors = Colors;
    }

    public Integer getId() {
        return id;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public String getPhone() {
        return phone;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
    public  String getSize(){return size;}
    public String getColors(){return Colors;}
    public Integer getQuantity(){return quantity;}
    public Integer getProductID(){return productID;}
}
