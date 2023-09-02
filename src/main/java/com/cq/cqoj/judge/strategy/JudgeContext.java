package com.cq.cqoj.judge.strategy;

import com.cq.cqoj.model.dto.questionsubmit.JudgeInfo;
import com.cq.cqoj.model.entity.Question;
import com.cq.cqoj.model.enums.QuestionSubmitLanguageEnum;
import lombok.Data;

import java.util.List;

/**
 * 判题上下文
 *
 * @author 程崎
 * @since 2023/08/16
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> outputList;

    private List<String> outputListResult;

    private Question question;

    private QuestionSubmitLanguageEnum languageType;
}
