package com.example.recipeservice.recipe.controller;

import com.example.recipeservice.recipe.dtos.CookStepCreateRequest;
import com.example.recipeservice.recipe.dtos.CookStepDto;
import com.example.recipeservice.recipe.dtos.FoodInformationCreateDto;
import com.example.recipeservice.recipe.dtos.FoodInformationDto;
import com.example.recipeservice.recipe.dtos.IngredientCreateDto;
import com.example.recipeservice.recipe.dtos.IngredientDto;
import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeCreateDto;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.dtos.RecipeUpdateDto;
import com.example.recipeservice.recipe.entity.Recipe;
import com.example.recipeservice.recipe.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.headers.SetHttpHeaders;
import org.example.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class RecipeControllerRestDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private SetHttpHeaders setHttpHeaders;

    @MockBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private PagingRecipeResponse pagingRecipeResponse;
    private RecipeDto recipeDto;

    @BeforeEach
    void setUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        given(setHttpHeaders.setHeadersTypeJson()).willReturn(headers);
        given(jwtUtils.extractUsername(anyString())).willReturn("tester");

        FoodInformationDto foodInfo = FoodInformationDto.builder()
                .foodInformationId(10L)
                .content("info")
                .cookingTime(30L)
                .serving("2")
                .build();

        pagingRecipeResponse = PagingRecipeResponse.builder()
                .foodId(1L)
                .recipeType(Recipe.RecipeType.Image)
                .foodName("Pasta")
                .foodImgUrl("https://cdn.example/recipe.png")
                .likeCount(3L)
                .foodInformationDto(foodInfo)
                .build();

        IngredientDto ingredient = IngredientDto.builder()
                .ingredientId(20L)
                .ingredientName("Salt")
                .amount("1t")
                .build();
        CookStepDto cookStep = CookStepDto.builder()
                .stepNumber(1L)
                .timeLine("00:10")
                .stepImgUrl("https://cdn.example/step.png")
                .content("Mix ingredients")
                .build();

        recipeDto = RecipeDto.builder()
                .foodId(1L)
                .recipeType(Recipe.RecipeType.Image)
                .foodName("Pasta")
                .foodImgUrl("https://cdn.example/recipe.png")
                .foodVideoUrl("https://cdn.example/recipe.mp4")
                .isLiked(true)
                .isScraped(false)
                .foodInformation(foodInfo)
                .ingredients(List.of(ingredient))
                .cookSteps(List.of(cookStep))
                .build();
    }

    @Test
    void getPopularRecipesDocs() throws Exception {
        Page<PagingRecipeResponse> page = new PageImpl<>(
                List.of(pagingRecipeResponse), PageRequest.of(0, 10), 1
        );
        given(recipeService.getPopularRecipes(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/recipes/popular")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("recipes-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page size").optional(),
                                parameterWithName("sort").description("Sort order").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[].foodId").type(JsonFieldType.NUMBER).description("Recipe ID"),
                                fieldWithPath("content[].recipeType").type(JsonFieldType.STRING).description("Recipe type"),
                                fieldWithPath("content[].foodName").type(JsonFieldType.STRING).description("Recipe name"),
                                fieldWithPath("content[].foodImgUrl").type(JsonFieldType.STRING).description("Image URL"),
                                fieldWithPath("content[].likeCount").type(JsonFieldType.NUMBER).description("Like count"),
                                fieldWithPath("content[].foodInformationDto").type(JsonFieldType.OBJECT)
                                        .description("Food information summary"),
                                fieldWithPath("content[].foodInformationDto.foodInformationId").type(JsonFieldType.NUMBER)
                                        .description("Food information ID"),
                                fieldWithPath("content[].foodInformationDto.content").type(JsonFieldType.STRING)
                                        .description("Food information content"),
                                fieldWithPath("content[].foodInformationDto.cookingTime").type(JsonFieldType.NUMBER)
                                        .description("Cooking time"),
                                fieldWithPath("content[].foodInformationDto.serving").type(JsonFieldType.STRING)
                                        .description("Serving size"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("Total elements"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("Total pages"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("Page size"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("Page number")
                        )
                ));
    }

    @Test
    void getAllRecipesDocs() throws Exception {
        Page<PagingRecipeResponse> page = new PageImpl<>(
                List.of(pagingRecipeResponse), PageRequest.of(0, 10), 1
        );
        given(recipeService.getAllRecipes(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/recipes/list")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("recipes-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page size").optional(),
                                parameterWithName("sort").description("Sort order").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[].foodId").type(JsonFieldType.NUMBER).description("Recipe ID"),
                                fieldWithPath("content[].recipeType").type(JsonFieldType.STRING).description("Recipe type"),
                                fieldWithPath("content[].foodName").type(JsonFieldType.STRING).description("Recipe name"),
                                fieldWithPath("content[].foodImgUrl").type(JsonFieldType.STRING).description("Image URL"),
                                fieldWithPath("content[].likeCount").type(JsonFieldType.NUMBER).description("Like count"),
                                fieldWithPath("content[].foodInformationDto").type(JsonFieldType.OBJECT)
                                        .description("Food information summary"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("Total elements"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("Total pages"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("Page size"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("Page number")
                        )
                ));
    }

    @Test
    void getMyRecipesDocs() throws Exception {
        Page<PagingRecipeResponse> page = new PageImpl<>(
                List.of(pagingRecipeResponse), PageRequest.of(0, 10), 1
        );
        given(recipeService.getMyRecipes(any(Pageable.class), eq("tester"))).willReturn(page);

        mockMvc.perform(get("/recipes/mylist")
                        .header("Authorization", "Bearer access-token")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("recipes-mylist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        queryParameters(
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page size").optional(),
                                parameterWithName("sort").description("Sort order").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[].foodId").type(JsonFieldType.NUMBER).description("Recipe ID"),
                                fieldWithPath("content[].recipeType").type(JsonFieldType.STRING).description("Recipe type"),
                                fieldWithPath("content[].foodName").type(JsonFieldType.STRING).description("Recipe name"),
                                fieldWithPath("content[].foodImgUrl").type(JsonFieldType.STRING).description("Image URL"),
                                fieldWithPath("content[].likeCount").type(JsonFieldType.NUMBER).description("Like count"),
                                fieldWithPath("content[].foodInformationDto").type(JsonFieldType.OBJECT)
                                        .description("Food information summary"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("Total elements"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("Total pages"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("Page size"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("Page number")
                        )
                ));
    }

    @Test
    void getSearchedRecipesDocs() throws Exception {
        Page<PagingRecipeResponse> page = new PageImpl<>(
                List.of(pagingRecipeResponse), PageRequest.of(0, 10), 1
        );
        given(recipeService.getSearchedRecipes(eq("pasta"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/recipes/search/{keyword}", "pasta")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("recipes-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("keyword").description("Search keyword")
                        ),
                        queryParameters(
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page size").optional(),
                                parameterWithName("sort").description("Sort order").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[].foodId").type(JsonFieldType.NUMBER).description("Recipe ID"),
                                fieldWithPath("content[].recipeType").type(JsonFieldType.STRING).description("Recipe type"),
                                fieldWithPath("content[].foodName").type(JsonFieldType.STRING).description("Recipe name"),
                                fieldWithPath("content[].foodImgUrl").type(JsonFieldType.STRING).description("Image URL"),
                                fieldWithPath("content[].likeCount").type(JsonFieldType.NUMBER).description("Like count"),
                                fieldWithPath("content[].foodInformationDto").type(JsonFieldType.OBJECT)
                                        .description("Food information summary"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("Total elements"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("Total pages"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("Page size"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("Page number")
                        )
                ));
    }

    @Test
    void createRecipeDocs() throws Exception {
        RecipeCreateDto createDto = RecipeCreateDto.builder()
                .recipeType(Recipe.RecipeType.Image)
                .foodName("Pasta")
                .foodVideoUrl("https://cdn.example/recipe.mp4")
                .foodInformation(FoodInformationCreateDto.builder()
                        .content("info")
                        .cookingTime(30L)
                        .serving("2")
                        .build())
                .ingredients(List.of(IngredientCreateDto.builder()
                        .ingredientName("Salt")
                        .amount("1t")
                        .build()))
                .cookSteps(List.of(CookStepCreateRequest.builder()
                        .stepNumber(1L)
                        .timeLine("00:10")
                        .stepImg("https://cdn.example/step.png")
                        .content("Mix ingredients")
                        .build()))
                .build();

        MockMultipartFile recipeImg = new MockMultipartFile(
                "recipeImg", "recipe.png", "image/png", "fake".getBytes()
        );
        MockMultipartFile cookStepImg = new MockMultipartFile(
                "cookStepImgs", "step.png", "image/png", "fake".getBytes()
        );
        MockMultipartFile data = new MockMultipartFile(
                "data", "", "application/json",
                objectMapper.writeValueAsString(createDto).getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(recipeService).createRecipe(any(), any(), any(), anyString());

        mockMvc.perform(multipart("/recipes/create")
                        .file(recipeImg)
                        .file(cookStepImg)
                        .file(data)
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isCreated())
                .andDo(document("recipes-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        requestParts(
                                partWithName("recipeImg").description("Recipe image"),
                                partWithName("cookStepImgs").description("Cook step images"),
                                partWithName("data").description("Recipe create payload")
                        ),
                        relaxedRequestPartFields("data",
                                fieldWithPath("recipeType").type(JsonFieldType.STRING).description("Recipe type"),
                                fieldWithPath("foodName").type(JsonFieldType.STRING).description("Recipe name"),
                                fieldWithPath("foodVideoUrl").type(JsonFieldType.STRING).optional()
                                        .description("Video URL"),
                                fieldWithPath("foodInformation").type(JsonFieldType.OBJECT)
                                        .description("Food information"),
                                fieldWithPath("foodInformation.content").type(JsonFieldType.STRING)
                                        .description("Food info content"),
                                fieldWithPath("foodInformation.cookingTime").type(JsonFieldType.NUMBER)
                                        .description("Cooking time"),
                                fieldWithPath("foodInformation.serving").type(JsonFieldType.STRING)
                                        .description("Serving size"),
                                fieldWithPath("ingredients").type(JsonFieldType.ARRAY).description("Ingredients"),
                                fieldWithPath("ingredients[].ingredientName").type(JsonFieldType.STRING)
                                        .description("Ingredient name"),
                                fieldWithPath("ingredients[].amount").type(JsonFieldType.STRING)
                                        .description("Ingredient amount"),
                                fieldWithPath("cookSteps").type(JsonFieldType.ARRAY).description("Cook steps"),
                                fieldWithPath("cookSteps[].stepNumber").type(JsonFieldType.NUMBER)
                                        .description("Step number"),
                                fieldWithPath("cookSteps[].timeLine").type(JsonFieldType.STRING)
                                        .description("Step timeline"),
                                fieldWithPath("cookSteps[].stepImg").type(JsonFieldType.STRING)
                                        .description("Step image URL"),
                                fieldWithPath("cookSteps[].content").type(JsonFieldType.STRING)
                                        .description("Step content")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("Result message")
                        )
                ));
    }

    @Test
    void getRecipeDocs() throws Exception {
        given(recipeService.getRecipe(1L)).willReturn(recipeDto);

        mockMvc.perform(get("/recipes/detail/{recipeId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("recipes-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("recipeId").description("Recipe ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("foodId").type(JsonFieldType.NUMBER).description("Recipe ID"),
                                fieldWithPath("recipeType").type(JsonFieldType.STRING).description("Recipe type"),
                                fieldWithPath("foodName").type(JsonFieldType.STRING).description("Recipe name"),
                                fieldWithPath("foodImgUrl").type(JsonFieldType.STRING).description("Image URL"),
                                fieldWithPath("foodVideoUrl").type(JsonFieldType.STRING).description("Video URL"),
                                fieldWithPath("liked").type(JsonFieldType.BOOLEAN).description("Liked by user"),
                                fieldWithPath("scraped").type(JsonFieldType.BOOLEAN).description("Scraped by user"),
                                fieldWithPath("foodInformation").type(JsonFieldType.OBJECT).description("Food information"),
                                fieldWithPath("foodInformation.foodInformationId").type(JsonFieldType.NUMBER)
                                        .description("Food info ID"),
                                fieldWithPath("foodInformation.content").type(JsonFieldType.STRING)
                                        .description("Food info content"),
                                fieldWithPath("foodInformation.cookingTime").type(JsonFieldType.NUMBER)
                                        .description("Cooking time"),
                                fieldWithPath("foodInformation.serving").type(JsonFieldType.STRING)
                                        .description("Serving size"),
                                fieldWithPath("ingredients").type(JsonFieldType.ARRAY).description("Ingredients"),
                                fieldWithPath("ingredients[].ingredientId").type(JsonFieldType.NUMBER)
                                        .description("Ingredient ID"),
                                fieldWithPath("ingredients[].ingredientName").type(JsonFieldType.STRING)
                                        .description("Ingredient name"),
                                fieldWithPath("ingredients[].amount").type(JsonFieldType.STRING)
                                        .description("Ingredient amount"),
                                fieldWithPath("cookSteps").type(JsonFieldType.ARRAY).description("Cook steps"),
                                fieldWithPath("cookSteps[].stepNumber").type(JsonFieldType.NUMBER)
                                        .description("Step number"),
                                fieldWithPath("cookSteps[].timeLine").type(JsonFieldType.STRING)
                                        .description("Step timeline"),
                                fieldWithPath("cookSteps[].stepImgUrl").type(JsonFieldType.STRING)
                                        .description("Step image URL"),
                                fieldWithPath("cookSteps[].content").type(JsonFieldType.STRING)
                                        .description("Step content")
                        )
                ));
    }

    @Test
    void updateRecipeDocs() throws Exception {
        RecipeUpdateDto updateDto = RecipeUpdateDto.builder()
                .foodName("Updated Pasta")
                .foodVideoUrl("https://cdn.example/new.mp4")
                .foodInformation(FoodInformationDto.builder()
                        .foodInformationId(10L)
                        .content("new info")
                        .cookingTime(40L)
                        .serving("3")
                        .build())
                .ingredients(List.of(IngredientDto.builder()
                        .ingredientId(20L)
                        .ingredientName("Salt")
                        .amount("2t")
                        .build()))
                .cookSteps(List.of(CookStepDto.builder()
                        .stepNumber(1L)
                        .timeLine("00:20")
                        .stepImgUrl("https://cdn.example/step.png")
                        .content("Mix more")
                        .build()))
                .build();

        MockMultipartFile img = new MockMultipartFile(
                "img", "recipe.png", "image/png", "fake".getBytes()
        );
        MockMultipartFile data = new MockMultipartFile(
                "data", "", "application/json",
                objectMapper.writeValueAsString(updateDto).getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(recipeService).updateRecipe(eq(1L), any(), anyString(), any());

        mockMvc.perform(multipart("/recipes/modify/{recipeId}", 1L)
                        .file(img)
                        .file(data)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(document("recipes-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("recipeId").description("Recipe ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        requestParts(
                                partWithName("img").description("Recipe image"),
                                partWithName("data").description("Recipe update payload")
                        ),
                        relaxedRequestPartFields("data",
                                fieldWithPath("foodName").type(JsonFieldType.STRING).description("Recipe name"),
                                fieldWithPath("foodVideoUrl").type(JsonFieldType.STRING).optional()
                                        .description("Video URL"),
                                fieldWithPath("foodInformation").type(JsonFieldType.OBJECT).description("Food info"),
                                fieldWithPath("foodInformation.foodInformationId").type(JsonFieldType.NUMBER)
                                        .description("Food info ID"),
                                fieldWithPath("foodInformation.content").type(JsonFieldType.STRING)
                                        .description("Food info content"),
                                fieldWithPath("foodInformation.cookingTime").type(JsonFieldType.NUMBER)
                                        .description("Cooking time"),
                                fieldWithPath("foodInformation.serving").type(JsonFieldType.STRING)
                                        .description("Serving size"),
                                fieldWithPath("ingredients").type(JsonFieldType.ARRAY).description("Ingredients"),
                                fieldWithPath("ingredients[].ingredientId").type(JsonFieldType.NUMBER)
                                        .description("Ingredient ID"),
                                fieldWithPath("ingredients[].ingredientName").type(JsonFieldType.STRING)
                                        .description("Ingredient name"),
                                fieldWithPath("ingredients[].amount").type(JsonFieldType.STRING)
                                        .description("Ingredient amount"),
                                fieldWithPath("cookSteps").type(JsonFieldType.ARRAY).description("Cook steps"),
                                fieldWithPath("cookSteps[].stepNumber").type(JsonFieldType.NUMBER)
                                        .description("Step number"),
                                fieldWithPath("cookSteps[].timeLine").type(JsonFieldType.STRING)
                                        .description("Step timeline"),
                                fieldWithPath("cookSteps[].stepImgUrl").type(JsonFieldType.STRING)
                                        .description("Step image URL"),
                                fieldWithPath("cookSteps[].content").type(JsonFieldType.STRING)
                                        .description("Step content")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("Result message")
                        )
                ));
    }

    @Test
    void deleteRecipeDocs() throws Exception {
        doNothing().when(recipeService).deleteRecipe(eq(1L), anyString());

        mockMvc.perform(delete("/recipes/{recipeId}", 1L)
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isNoContent())
                .andDo(document("recipes-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("recipeId").description("Recipe ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        )
                ));
    }

}
