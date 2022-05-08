package com.example.resturant.models;

public class ModelProduct {
    private String Product_id,title,description,category,quantity,
            productIcon,price,discountPrice,discount,timeStamp,uid;

    public ModelProduct() {
    }

    public ModelProduct(String product, String title, String description, String category, String quantity,
                        String productIcon, String price, String discountPrice, String discount, String timeStamp, String uid) {
        Product_id = product;
        this.title = title;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.productIcon = productIcon;
        this.price = price;
        this.discountPrice = discountPrice;
        this.discount = discount;
        this.timeStamp = timeStamp;
        this.uid = uid;
    }

    public String getProduct_id() {
        return Product_id;
    }

    public void setProduct_id(String product) {
        Product_id = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
