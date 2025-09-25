package com.example.recipeservice.recipe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "recipes")
@Entity
@DynamicUpdate
public class Recipe {

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





}
