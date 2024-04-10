package com.example.bizbot;

import java.util.Date;

public class CustomerData {
    private Integer id;
    private Integer customer_id;
    private Double total;
    private Date date;
    private String em_username;
    public CustomerData(Integer id, Integer customer_id, Double total,
                        Date date, String em_username){
        this.id = id;
        this.customer_id = customer_id;
        this.total = total;
        this.date = date;
        this.em_username = em_username;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public Double getTotal() {
        return total;
    }

    public Date getDate() {
        return date;
    }

    public String getEm_username() {
        return em_username;
    }
}
