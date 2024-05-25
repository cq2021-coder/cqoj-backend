package com.cq.cqoj.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.cqoj.common.ResultCodeEnum;
import com.cq.cqoj.constants.CommonConstant;
import com.cq.cqoj.exception.BusinessException;
import com.cq.cqoj.judge.JudgeService;
import com.cq.cqoj.mapper.QuestionSubmitMapper;
import com.cq.cqoj.model.dto.questionsubmit.JudgeInfo;
import com.cq.cqoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cq.cqoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cq.cqoj.model.entity.Question;
import com.cq.cqoj.model.entity.QuestionSubmit;
import com.cq.cqoj.model.entity.User;
import com.cq.cqoj.model.enums.QuestionSubmitLanguageEnum;
import com.cq.cqoj.model.enums.QuestionSubmitStatusEnum;
import com.cq.cqoj.model.enums.UserRoleEnum;
import com.cq.cqoj.model.vo.QuestionSubmitVO;
import com.cq.cqoj.model.vo.QuestionSubmitViewVO;
import com.cq.cqoj.service.QuestionService;
import com.cq.cqoj.service.QuestionSubmitService;
import com.cq.cqoj.service.UserService;
import com.cq.cqoj.utils.SqlUtils;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author 程崎
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-08-08 21:24:55
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 问题提交添加请求
     * @param loginUser                登录用户
     * @return long
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "数据插入失败");
        }
        CompletableFuture.runAsync(() -> judgeService.doJudge(questionSubmit));
        return questionSubmit.getId();
    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest 问题提交查询请求
     * @return {@link QueryWrapper}<{@link QuestionSubmit}>
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        boolean isSort = SqlUtils.validSortField(sortField);
        if (isSort) {
            sortField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortField);
        }
        queryWrapper.orderBy(
                isSort,
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField
        );
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVoPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVoPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVoPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVoPage.setRecords(questionSubmitVOList);
        return questionSubmitVoPage;
    }

    @Override
    public Page<QuestionSubmitViewVO> listQuestionSubmitByPage(String title, String language, long pageIndex, long size, User loginUser) {
        Page<QuestionSubmitViewVO> page = new Page<>(pageIndex, size);
        long total = this.baseMapper.countQuestionSubmit(title, language);
        page.setTotal(total);
        if (size > 0) {
            page.setPages(total / size);
        }
        List<QuestionSubmitViewVO> records;
        if (UserRoleEnum.ADMIN.equals(loginUser.getUserRole())) {
            records = this.baseMapper.selectQuestionSubmit(title, language, pageIndex, size);
        }else {
            records = this.baseMapper.selectQuestionSubmitByUserId(title, language, pageIndex, size, loginUser.getId());
        }
        records.forEach(record -> {
            JudgeInfo judgeInfo = JSONUtil.toBean(record.getJudgeInfo(), JudgeInfo.class);
            record.setMessage(judgeInfo.getMessage());
            record.setTime(ObjectUtil.defaultIfNull(judgeInfo.getTime(), 0) + "ms");
        });
        page.setRecords(records);
        return page;
    }

}




