package com.cq.cqoj.judge.codesandbox.impl;

import com.cq.cqoj.judge.codesandbox.CodeSandbox;
import com.cq.cqoj.judge.model.ExecuteCodeRequest;
import com.cq.cqoj.judge.model.ExecuteCodeResponse;
import com.cq.cqoj.model.dto.questionsubmit.JudgeInfo;
import com.cq.cqoj.model.enums.JudgeInfoMessageEnum;
import com.cq.cqoj.model.enums.QuestionSubmitLanguageEnum;
import com.cq.cqoj.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * 示例代码沙箱
 *
 * @author 程崎
 * @since 2023/08/15
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        QuestionSubmitLanguageEnum language = executeCodeRequest.getLanguage();
        log.info("处理代码中，语言类型为{}...{}", language.getText(), code);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        return ExecuteCodeResponse
                .builder()
                .message("测试执行成功")
                .status(QuestionSubmitStatusEnum.SUCCEED.getValue())
                .outputList(inputList)
                .judgeInfo(judgeInfo)
                .build();
    }
}
