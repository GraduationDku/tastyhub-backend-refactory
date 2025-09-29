package com.example.recipeservice.recipe.dtos;

import com.example.recipeservice.recipe.entity.CookStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CookStepDto {

    private Long stepNumber;
    private String timeLine;
    private String stepImgUrl;
    private String content;

    public CookStepDto(CookStep cookStep) {
        this.stepNumber = cookStep.getStepNumber();
        this.timeLine = cookStep.getTimeline();
        this.stepImgUrl = cookStep.getStepImgUrl();
        this.content = cookStep.getContent();
    }
}
