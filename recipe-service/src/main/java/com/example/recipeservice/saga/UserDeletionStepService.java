package com.example.recipeservice.saga;

import com.example.recipeservice.recipe.entity.Recipe;
import com.example.recipeservice.recipe.repository.recipe.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeletionStepService {

    private final RecipeRepository recipeRepository;
    // TODO: LikeRepository 또는 QueryDSL 기반 bulk update/delete 추가

    @Transactional
    public void softDelete(String username) {
        List<Recipe> recipes = recipeRepository.findByUsername(username);
        for (Recipe recipe : recipes) {
            recipe.softDelete();
        }
    }

    @Transactional
    public void compensate(String username) {
        // TODO: soft delete 복구
        List<Recipe> recipes = recipeRepository.findByUsername(username);
        for (Recipe recipe : recipes) {
            recipe.softDeleteRollBack();
        }

    }

    @Transactional
    public void hardDelete(String username) {
        // TODO: 완전 삭제 (completed 이후)
        List<Recipe> recipes = recipeRepository.findByUsername(username);
        for (Recipe recipe : recipes) {
            if(recipe.isDeleted()) {
                recipeRepository.delete(recipe);
            }
        }


    }

}
