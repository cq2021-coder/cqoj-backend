package com.cq.cqoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cqoj.annotation.AuthCheck;
import com.cq.cqoj.common.CommonResponse;
import com.cq.cqoj.common.DeleteRequest;
import com.cq.cqoj.common.ResultCodeEnum;
import com.cq.cqoj.exception.BusinessException;
import com.cq.cqoj.model.dto.question.*;
import com.cq.cqoj.model.entity.Question;
import com.cq.cqoj.model.entity.User;
import com.cq.cqoj.model.enums.UserRoleEnum;
import com.cq.cqoj.model.vo.QuestionVO;
import com.cq.cqoj.service.QuestionService;
import com.cq.cqoj.service.UserService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 题目接口
 *
 * @author 程崎
 * @since 2023/08/08
 */
@RestController
@RequestMapping("/question")
@Api(tags = "question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest 题目添加请求
     * @param session            会话
     * @return {@link CommonResponse}<{@link Long}>
     */
    @PostMapping("/add")
    public CommonResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpSession session) {
        if (questionAddRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        setQuestionValue(questionAddRequest, question, true);
        User loginUser = userService.getLoginUser(session);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        if (!result) {
            throw new BusinessException(ResultCodeEnum.OPERATION_ERROR);
        }
        return CommonResponse.success(question.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求
     * @param session       会话
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    public CommonResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpSession session) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(session);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }

        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(session)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }
        return CommonResponse.success(questionService.removeById(id));
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest 题目更新请求
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        setQuestionValue(questionUpdateRequest, question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        boolean result = questionService.updateById(question);
        return CommonResponse.success(result);
    }

    /**
     * 根据 id 获取(脱敏)
     *
     * @param id id
     * @return {@link CommonResponse}<{@link QuestionVO}>
     */
    @GetMapping("/get/vo")
    public CommonResponse<QuestionVO> getQuestionVoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        return CommonResponse.success(questionService.getQuestionVO(question));
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return {@link CommonResponse}<{@link QuestionVO}>
     */
    @GetMapping("/get")
    public CommonResponse<Question> getQuestionById(long id, HttpSession session) {
        if (id <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(session);
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }
        return CommonResponse.success(question);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest 题目查询请求
     * @return {@link CommonResponse}<{@link Page}<{@link QuestionVO}>>
     */
    @PostMapping("/list/page/vo")
    public CommonResponse<Page<QuestionVO>> listQuestionVoByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return CommonResponse.success(questionService.getQuestionVoPage(questionPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest 题目查询请求
     * @param session              会话
     * @return {@link CommonResponse}<{@link Page}<{@link QuestionVO}>>
     */
    @PostMapping("/my/list/page/vo")
    public CommonResponse<Page<QuestionVO>> listMyQuestionVoByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                   HttpSession session) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(session);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return CommonResponse.success(questionService.getQuestionVoPage(questionPage));
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest 题目查询请求
     * @return {@link CommonResponse}<{@link Page}<{@link Question}>>
     */
    @PostMapping("/list/page")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return CommonResponse.success(questionPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest 题目编辑请求
     * @param session             会话
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/edit")
    public CommonResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpSession session) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        setQuestionValue(questionEditRequest, question, false);
        User loginUser = userService.getLoginUser(session);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }
        return CommonResponse.success(questionService.updateById(question));
    }



    private void setQuestionValue(QuestionBaseRequest questionBaseRequest, Question question, boolean add) {
        List<String> tags = questionBaseRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionBaseRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionBaseRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, add);
    }

}
