package site.nomoreparties.stellarburgers.pojo;

import java.util.List;

public class Ingredients {
    public List<String> ingredients;

    public Ingredients() {
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> addIngredients(String ingredient) {
        ingredients.add(ingredient);
        return ingredients;
    }

    public Ingredients setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}
