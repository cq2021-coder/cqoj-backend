package com.cq.cqoj.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cqoj.common.CommonResponse;
import com.cq.cqoj.common.ResultCodeEnum;
import com.cq.cqoj.exception.BusinessException;
import com.cq.cqoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cq.cqoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cq.cqoj.model.entity.QuestionSubmit;
import com.cq.cqoj.model.entity.User;
import com.cq.cqoj.model.vo.QuestionSubmitVO;
import com.cq.cqoj.service.QuestionSubmitService;
import com.cq.cqoj.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 题目提交接口
 *
 * @author 程崎
 * @since 2023/08/08
 */
@RestController
@RequestMapping("/question-submit")
@Slf4j
@Api(tags = "questionSubmit")
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 题目提交添加请求
     * @param session                  会话
     * @return 提交记录的 id
     */
    @PostMapping("/")
    public CommonResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpSession session) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        final User loginUser = userService.getLoginUser(session);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return CommonResponse.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest 题目提交查询请求
     * @param session                    会话
     * @return {@link CommonResponse}<{@link Page}<{@link QuestionSubmitVO}>>
     */
    @PostMapping("/list/page")
    public CommonResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpSession session) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(session);
        // 返回脱敏信息
        return CommonResponse.success(questionSubmitService.getQuestionSubmitVoPage(questionSubmitPage, loginUser));
    }


}
