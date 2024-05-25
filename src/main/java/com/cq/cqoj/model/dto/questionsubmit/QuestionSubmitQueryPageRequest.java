package com.cq.cqoj.model.dto.questionsubmit;


import com.cq.cqoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryPageRequest extends PageRequest {
    private String title;
    private String language;
}
