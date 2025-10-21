package com.example.recipeservice.recipe.entity;

import com.example.recipeservice.recipe.dtos.FoodInformationDto;
import com.example.recipeservice.recipe.dtos.RecipeCreateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "recipes")
@Entity
@DynamicUpdate
public class Recipe {

    public static Recipe createRecipe(RecipeCreateDto recipeCreateDto, String username, String imgUrl, FoodInformation foodInformation, List<Ingredient> ingredients, List<CookStep> cookSteps) {
        return new Recipe().builder()
                .recipeImgUrl(imgUrl)
                .username(username)
                .foodInformation(foodInformation)
                .ingredients(ingredients)
                .cookSteps(cookSteps)
                .foodName(recipeCreateDto.getFoodName())
                .recipeType(recipeCreateDto.getRecipeType())
                .build();
    }

    public void update(String foodName, String newImgUrl, List<Ingredient> newIngredients, List<CookStep> newCookSteps) {

        this.ingredients = newIngredients;
        this.cookSteps = newCookSteps;
        this.foodName = foodName;
        this.recipeImgUrl = newImgUrl;
    }

    public enum RecipeType {
        Image,
        Video,
        Youtube
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasty_hub_sequence")
//    @SequenceGenerator(name = "tasty_hub_sequence", sequenceName = "thesq", allocationSize = 10)
    @Column(name = "recipe_id")
    private Long id;

    private String foodName;


    @Column(name = "food_img", length = 1024)
    private String recipeImgUrl;

    @Column(name = "food_video", length = 1024)
    private String recipeVideoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeType recipeType;


    @Column(name = "user_name")
    private String username;

    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FoodInformation foodInformation;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    List<CookStep> cookSteps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    List<Like> likes = new ArrayList<>();
}
