package com.cq.cqoj.judge.strategy;

import com.cq.cqoj.judge.strategy.impl.DefaultJudgeStrategy;
import com.cq.cqoj.judge.strategy.impl.JavaJudgeStrategy;
import com.cq.cqoj.model.dto.questionsubmit.JudgeInfo;
import com.cq.cqoj.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

/**
 * 判题管理
 *
 * @author 程崎
 * @since 2023/08/16
 */
@Service
public class JudgeManager {

    public JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmitLanguageEnum languageType = judgeContext.getLanguageType();
        JudgeStrategy judgeStrategy;
        if (QuestionSubmitLanguageEnum.JAVA.equals(languageType)) {
            judgeStrategy = new JavaJudgeStrategy();
        }else {
            judgeStrategy = new DefaultJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
