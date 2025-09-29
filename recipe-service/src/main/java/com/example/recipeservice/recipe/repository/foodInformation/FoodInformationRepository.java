package com.example.recipeservice.recipe.repository.foodInformation;

import com.example.recipeservice.recipe.entity.FoodInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodInformationRepository extends JpaRepository<FoodInformation, Long> {
}
