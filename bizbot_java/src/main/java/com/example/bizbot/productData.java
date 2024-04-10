package com.example.bizbot;

import java.util.Date;

public class productData {
    private Integer id;
    private String product_id;
    private String product_name;
    private Double price;
    private String status;
    private String image;
    private Integer stock;
    private Integer quantity;
    private String sizes;
    private String size;
    private String colors;
    public  productData(Integer id, String product_id, String product_name,
                        Double price, String status, String image, Integer stock,String sizes, String colors){
        this.id = id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.price = price;
        this.status = status;
        this.image = image;
        this.stock = stock;
        this.sizes = sizes;
        this.colors = colors;
    }
    public productData(Integer id, String product_id, String product_name,Integer quantity, Double price, String image){
        this.id = id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.price = price;
        this.image = image;
    }
    public productData(Integer id, String product_id, String product_name, Integer quantity, String size,Double price){
        this.id = id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
    }
    public Integer getId(){
        return id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public Double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
    public String getSizes(){
        return  sizes;
    }
    public String getSize(){
        return  size;
    }
    public String getColors(){
        return colors;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
