package site.nomoreparties.stellarburgers.pojo;

public class OrderResponse {
    public String name;
    public Order order;
    public Boolean success;

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

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
