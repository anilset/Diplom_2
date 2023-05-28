package site.nomoreparties.stellarburgers.pojo;

import java.util.List;

public class OrderResponse {
    public String name;
    public Order order;
    public Boolean success;

    public List<Data> data;

    public List<Order> orders;
    public int total;
    public int totalToday;

    public OrderResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Boolean isSuccessful() {
        return success;
    }

    public List<Data> getData() {
        return data;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalToday() {
        return totalToday;
    }
}
