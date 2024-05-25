package com.cq.cqoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.cqoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cq.cqoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cq.cqoj.model.entity.QuestionSubmit;
import com.cq.cqoj.model.entity.User;
import com.cq.cqoj.model.vo.QuestionSubmitVO;
import com.cq.cqoj.model.vo.QuestionSubmitViewVO;

/**
* @author 程崎
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-08-08 21:24:55
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser                登录用户
     * @return long
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest 问题提交查询请求
     * @return {@link QueryWrapper}<{@link QuestionSubmit}>
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit 问题提交
     * @param loginUser      登录用户
     * @return {@link QuestionSubmitVO}
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage 问题提交页面
     * @param loginUser          登录用户
     * @return {@link Page}<{@link QuestionSubmitVO}>
     */
    Page<QuestionSubmitVO> getQuestionSubmitVoPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    Page<QuestionSubmitViewVO> listQuestionSubmitByPage(String title, String language, long pageIndex, long size, User loginUser);
}
