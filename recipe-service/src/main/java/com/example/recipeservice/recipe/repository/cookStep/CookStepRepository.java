package com.example.recipeservice.recipe.repository.cookStep;

import com.example.recipeservice.recipe.entity.CookStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookStepRepository extends JpaRepository<CookStep, Long> {
}
