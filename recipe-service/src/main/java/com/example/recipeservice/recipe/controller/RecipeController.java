package com.example.recipeservice.recipe.controller;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeCreateDto;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.dtos.RecipeUpdateDto;
import com.example.recipeservice.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.example.headers.SetHttpHeaders;
import org.example.headers.StatusResponse;
import org.example.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.example.headers.HttpResponseEntity.*;

@RestController
@RequiredArgsConstructor
public class RecipeController {

    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);
    private final JwtUtils jwtUtils;

    private final RecipeService recipeService;

    private final SetHttpHeaders setHttpHeaders;

    @GetMapping("/popular")
    public ResponseEntity<Page<PagingRecipeResponse>> getPopuralRecipes(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().headers(setHttpHeaders.setHeadersTypeJson())
                .body(recipeService.getPopularRecipes(pageable));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<PagingRecipeResponse>> getAllRecipes(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().headers(setHttpHeaders.setHeadersTypeJson())
                .body(recipeService.getAllRecipes(pageable));
    }

    @GetMapping("/mylist")
    public ResponseEntity<Page<PagingRecipeResponse>> getMyRecipes(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtils.extractUsername(authHeader);
        return ResponseEntity.ok().headers(setHttpHeaders.setHeadersTypeJson())
//                .body(null);
         .body(recipeService.getMyRecipes(pageable, username));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<Page<PagingRecipeResponse>> getSearchedRecipes(
            @PathVariable String keyword,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("CTL : " + keyword);
        return ResponseEntity.ok().headers(setHttpHeaders.setHeadersTypeJson()).body(
                recipeService.getSearchedRecipes(keyword, pageable));
    }

    /**
     * writer : skyriv213 method : 레시피 생성하기
     */
    @PostMapping("/create")
    public ResponseEntity<StatusResponse> createRecipe(
            @RequestPart("recipeImg") MultipartFile recipeImg,
            @RequestPart("cookStepImgs") List<MultipartFile> cookStepImgs,
            @RequestPart("data")
            RecipeCreateDto recipeCreateDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String username = jwtUtils.extractUsername(authHeader);
            recipeService.createRecipe(recipeCreateDto, recipeImg, cookStepImgs, username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RESPONSE_CREATED;
    }

    /**
     * writer : skyriv213 method : 단일레시피 조회하기
     */
    @GetMapping("/detail/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipe(@PathVariable Long recipeId) {
        RecipeDto recipeDto = recipeService.getRecipe(recipeId);
        return ResponseEntity.ok().headers(setHttpHeaders.setHeadersTypeJson()).body(recipeDto);
    }

    /**
     * writer : skyriv213 method : 레시피 수정하기
     *
     * @throws IOException
     */
    @PatchMapping(value = "/modify/{recipeId}")
    public ResponseEntity<StatusResponse> updateRecipe(@PathVariable Long recipeId,
                                                       @RequestPart("img") MultipartFile img,
                                                       @RequestPart("data") RecipeUpdateDto recipeUpdateDto,
                                                       @RequestHeader("Authorization") String authHeader
                                                       ) throws IOException {
//
        String username = jwtUtils.extractUsername(authHeader);
        recipeService.updateRecipe(recipeId, img, username, recipeUpdateDto);
        return RESPONSE_OK;
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<StatusResponse> deleteRecipe(@PathVariable Long recipeId
            , @RequestHeader("Authorization") String authHeader) throws IOException {
        String username = jwtUtils.extractUsername(authHeader);
        recipeService.deleteRecipe(recipeId, username);

        return DELETE_SUCCESS;
    }
}