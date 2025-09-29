package com.example.recipeservice.recipe.entity;


import com.example.recipeservice.recipe.dtos.FoodInformationDto;
import com.example.recipeservice.recipe.dtos.RecipeCreateDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
@Table(name = "food_information")
public class FoodInformation extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_information_id")
    private Long id;

    @Size(max = 1024)
    private String content;

    private Long cookingTime;

    // 몇 인분
    private String serving;

    //연관 관계
    @OneToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;


    public void updateByFoodInformationDto(FoodInformationDto foodInformationDto) {
        this.content = foodInformationDto.getContent();
        this.cookingTime = foodInformationDto.getCookingTime();
        this.serving = foodInformationDto.getServing();
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void updateRelation(Recipe recipe) {
        this.recipe = recipe;
    }

    public static FoodInformation createFoodInformation(RecipeCreateDto recipeCreateDto) {
        return FoodInformation.builder()
                .content(recipeCreateDto.getFoodInformation().getContent())
                .serving(recipeCreateDto.getFoodInformation().getServing())
                .cookingTime(recipeCreateDto.getFoodInformation().getCookingTime())
                .build();
    }


}