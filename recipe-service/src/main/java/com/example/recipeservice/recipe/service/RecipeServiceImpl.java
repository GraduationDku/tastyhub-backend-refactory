package com.example.recipeservice.recipe.service;

import com.example.recipeservice.recipe.dtos.*;
import com.example.recipeservice.recipe.entity.CookStep;
import com.example.recipeservice.recipe.entity.FoodInformation;
import com.example.recipeservice.recipe.entity.Ingredient;
import com.example.recipeservice.recipe.entity.Recipe;
import com.example.recipeservice.recipe.repository.recipe.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Override
    public Page<PagingRecipeResponse> getPopularRecipes(Pageable pageable) {
        return recipeRepository.getPopularRecipes(pageable);
    }

    @Override
    public Page<PagingRecipeResponse> getAllRecipes(Pageable pageable) {
        return recipeRepository.getAllRecipes(pageable);
    }

    @Override
    public Page<PagingRecipeResponse> getSearchedRecipes(String keyword, Pageable pageable) {
        return recipeRepository.getSearchedRecipes(keyword, pageable);
    }

    @Override
    public RecipeDto getRecipe(Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(EntityNotFoundException::new);
        FoodInformationDto foodInformationDto = recipe.getFoodInformation().makeFoodInformationDto();
        List<IngredientDto> ingredients = recipe.getIngredients().stream().map(Ingredient :: makeIngredientDto).toList();
        List<CookStepDto> cookStepDtos = recipe.getCookSteps().stream().map(CookStep :: makeCookStepDto).toList();


        return RecipeDto.builder()
                .foodId(recipeId)
                .foodName(recipe.getFoodName())
                .recipeType(recipe.getRecipeType())
                .foodInformation(foodInformationDto)
                .foodImgUrl(recipe.getRecipeImgUrl())

                .ingredients(ingredients)
                .cookSteps(cookStepDtos)
                .build();
    }

    @Override
    public Page<PagingRecipeResponse> getMyRecipes(Pageable pageable, String username) {
        return recipeRepository.getMyRecipes(pageable, username);
    }

    @Override
    @Transactional
    public void createRecipe(RecipeCreateDto recipeCreateDto, MultipartFile recipeImg, List<MultipartFile> cookStepImgs, String username) {
        String imgUrl = new String();

        FoodInformation foodInformation = new FoodInformation(recipeCreateDto.getFoodInformation());

        List<Ingredient> ingredients = createIngredients(recipeCreateDto.getIngredients());
        List<CookStep> cookSteps = makeCookStep(recipeCreateDto.getCookSteps(), cookStepImgs);

        try {
            // imgUrl = s3Uploader.upload(recipeImg, "image/recipeImg");

            Recipe recipe = Recipe.createRecipe(recipeCreateDto, username, imgUrl, foodInformation,
                    ingredients,
                    cookSteps);

            foodInformation.relationRecipe(recipe);
            ingredients.forEach(ingredient -> ingredient.relationRecipe(recipe));
            cookSteps.forEach(cookStep -> cookStep.relationRecipe(recipe));

            recipeRepository.save(recipe);
        } catch (Exception e) {
            // deleteImgInS3(imgUrl);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateRecipe(Long recipeId, MultipartFile img, String username,
                             RecipeUpdateDto recipeUpdateDto) throws AccessDeniedException {
        // 1. 레시피 조회 및 수정 권한 확인
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("레시피를 찾을 수 없습니다. ID: " + recipeId));

        if (!checkRecipeAndUser(recipe, username)) {
            throw new AccessDeniedException("해당 레시피를 수정할 권한이 없습니다.");
        }

        String originalImgUrl = recipe.getRecipeImgUrl();
        String newImgUrl = originalImgUrl;

        // 2. 이미지 파일 처리
//        if (img != null && !img.isEmpty()) {
//            // newImgUrl = s3Uploader.upload(img, "image/recipeImg");
//        }

        // 3. 연관 엔티티 직접 업데이트
        // 3-1. FoodInformation 업데이트 (FoodInformation 엔티티에 update 메소드가 있다고 가정)
        recipe.getFoodInformation().update(recipeUpdateDto.getFoodInformation());

        // 3-2. DTO로부터 새로운 자식 엔티티 리스트 생성
        List<Ingredient> newIngredients = createIngredientsByUpdateDto(recipeUpdateDto.getIngredients());
        List<CookStep> newCookSteps = createCookStepsFromDtos(recipeUpdateDto.getCookSteps());

        // 4. Recipe 엔티티의 update 메소드 호출
        recipe.update(
                recipeUpdateDto.getFoodName(),
                newImgUrl,
                newIngredients,
                newCookSteps
        );

//        // 5. 기존 이미지 삭제
//        if (newImgUrl != null && !newImgUrl.equals(originalImgUrl)) {
//            // s3Uploader.delete(originalImgUrl);
//        }
    }

    private List<Ingredient> createIngredientsByUpdateDto
            (List<IngredientDto> ingredients) {
        if (ingredients == null) return new ArrayList<>();
        return ingredients.stream().map(IngredientDto::toEntity).collect(Collectors.toList());
    }

    @Override
    public void deleteRecipe(Long recipeId, String username) throws AccessDeniedException {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        if (recipe.getUsername().equals(username)) {
            recipeRepository.deleteById(recipeId);
        } else {
            throw new AccessDeniedException("Access denied");
        }
    }

    private boolean checkRecipeAndUser(Recipe recipe, String username) {
        return recipe.getUsername().equals(username);
    }

    private List<Ingredient> createIngredients(List<IngredientCreateDto> ingredients) {
        if (ingredients == null) return new ArrayList<>();
        return ingredients.stream().map(IngredientCreateDto::toEntity).collect(Collectors.toList());
    }

    // CookStep DTO 리스트를 엔티티 리스트로 변환하는 헬퍼 메소드
    private List<CookStep> createCookStepsFromDtos(List<CookStepDto> cookStepDtos) {
        if (cookStepDtos == null) return new ArrayList<>();
        return cookStepDtos.stream().map(CookStepDto::toEntity).collect(Collectors.toList());
    }

    private List<CookStep> makeCookStep(List<CookStepCreateRequest> cookSteps, List<MultipartFile> cookStepImgs) {
        ArrayList<String> urlList = new ArrayList<>(); // s3 로직 혹은 업로드 서버 설정 예정

        ArrayList<CookStep> cookStepList = new ArrayList<>();
        for (int i = 0; i < cookSteps.size(); i++) {
            cookStepList.add(CookStep.makeCookStep(cookSteps.get(i), urlList.get(i)));
        }
        return cookStepList;
    }
}
