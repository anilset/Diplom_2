package site.nomoreparties.stellarburgers.pojo;

import java.util.Date;
import java.util.List;

public class Order {
    public List<Ingredient> ingredients;
    //public List<String> ingredients;
    public String _id;
    public Owner owner;
    public String status;
    public String name;
    public Date createdAt;
    public Date updatedAt;
    public Integer number;
    public Integer price;

    public Order() {
    }

    public Integer getNumber() {
        return number;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String get_id() {
        return _id;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Integer getPrice() {
        return price;
    }
}
