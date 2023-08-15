package com.cq.cqoj.judge.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cq.cqoj.common.ResultCodeEnum;
import com.cq.cqoj.exception.BusinessException;
import com.cq.cqoj.judge.JudgeService;
import com.cq.cqoj.judge.codesandbox.CodeSandbox;
import com.cq.cqoj.judge.codesandbox.factory.CodeSandboxFactory;
import com.cq.cqoj.judge.codesandbox.proxy.CodeSandboxProxy;
import com.cq.cqoj.judge.model.ExecuteCodeRequest;
import com.cq.cqoj.judge.model.ExecuteCodeResponse;
import com.cq.cqoj.judge.strategy.JudgeContext;
import com.cq.cqoj.judge.strategy.JudgeManager;
import com.cq.cqoj.mapper.QuestionSubmitMapper;
import com.cq.cqoj.model.dto.question.JudgeCase;
import com.cq.cqoj.model.dto.questionsubmit.JudgeInfo;
import com.cq.cqoj.model.entity.Question;
import com.cq.cqoj.model.entity.QuestionSubmit;
import com.cq.cqoj.model.enums.QuestionSubmitLanguageEnum;
import com.cq.cqoj.model.enums.QuestionSubmitStatusEnum;
import com.cq.cqoj.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题服务impl
 *
 * @author 程崎
 * @since 2023/08/15
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    @Resource
    private CodeSandboxFactory codeSandboxFactory;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(QuestionSubmit questionSubmit) {
        if (ObjectUtils.isEmpty(questionSubmit)) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getId, questionId).select(Question::getJudgeCase, Question::getJudgeConfig)
        );
        if (ObjectUtils.isEmpty(question)) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR, "题目不存在");
        }
        Long questionSubmitId = questionSubmit.getId();
        String code = questionSubmit.getCode();
        QuestionSubmitLanguageEnum languageType = QuestionSubmitLanguageEnum.getEnumByValue(questionSubmit.getLanguage());
        List<JudgeCase> judgeCases = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCases.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String> outputList = judgeCases.stream().map(JudgeCase::getOutput).collect(Collectors.toList());


        //region 更新状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        questionSubmitMapper.updateById(questionSubmitUpdate);
        //endregion

        //region 获取代码沙箱执行结果
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance();
        ExecuteCodeRequest request = ExecuteCodeRequest
                .builder()
                .code(code)
                .inputList(inputList)
                .language(languageType)
                .build();
        ExecuteCodeResponse executeCodeResponse = new CodeSandboxProxy(codeSandbox).executeCode(request);
        //endregion

        List<String> outputListResult = executeCodeResponse.getOutputList();
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setOutputList(outputList);
        judgeContext.setOutputListResult(outputListResult);
        judgeContext.setQuestion(question);

        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        questionSubmitMapper.updateById(questionSubmitUpdate);
        return questionSubmitMapper.selectById(questionSubmit.getId());
    }
}
