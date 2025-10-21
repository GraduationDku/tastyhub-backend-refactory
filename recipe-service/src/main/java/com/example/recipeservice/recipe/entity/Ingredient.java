package com.example.recipeservice.recipe.entity;

import com.example.recipeservice.recipe.dtos.IngredientCreateDto;
import com.example.recipeservice.recipe.dtos.IngredientDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.timeStamp.TimeStamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "ingredient")
public class Ingredient extends TimeStamp {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "tasty_hub_sequence", sequenceName = "thesq", allocationSize = 10)
    @Column(name = "ingredient_id")
    private Long id;
    private String ingredientName;
    private String amount;

//    @Enumerated(EnumType.STRING)
//    private IngredientType ingredientType;
//    public enum IngredientType{
//        seasoning, Source, Vegetable, Meat, Fish, Etc
//    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, targetEntity = Recipe.class)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    public Ingredient(String ingredientName, String amount) {
        this.ingredientName = ingredientName;
        this.amount = amount;
    }

    public Ingredient(IngredientDto ingredientDto) {
        this.id = ingredientDto.getIngredientId();
        this.ingredientName = ingredientDto.getIngredientName();
        this.amount = ingredientDto.getAmount();
    }


    public static Ingredient makeIngredient(IngredientCreateDto ingredientCreateDto) {
        return Ingredient.builder()
                .ingredientName(ingredientCreateDto.getIngredientName())
                .amount(ingredientCreateDto.getAmount())
                .build();
    }

    public void updateByUpdateDto(IngredientDto ingredientDto) {
        this.ingredientName = ingredientDto.getIngredientName();
        this.amount = ingredientDto.getAmount();

    }

    public void relationRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public IngredientDto makeIngredientDto() {
        return IngredientDto.builder().ingredientName(this.ingredientName).amount(this.amount).build();
    }

}