package site.nomoreparties.stellarburgers.pojo;

import java.util.List;

public class Ingredient {
    public List<String> ingredients;

    public Ingredient() {
    }

    public Ingredient(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> addIngredients(String ingredient) {
        ingredients.add(ingredient);
        return ingredients;
    }

    public Ingredient setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}
