package com.cq.cqoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.cqoj.model.dto.question.QuestionQueryRequest;
import com.cq.cqoj.model.entity.Question;
import com.cq.cqoj.model.vo.QuestionVO;

/**
* @author 程崎
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-08-08 21:24:55
*/
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param question 题目
     * @param add      添加
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest 题目查询请求
     * @return {@link QueryWrapper}<{@link Question}>
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question 题目
     * @return {@link QuestionVO}
     */
    QuestionVO getQuestionVO(Question question);

    /**
     * 分页获取题目封装
     *
     * @param questionPage 题目页面
     * @return {@link Page}<{@link QuestionVO}>
     */
    Page<QuestionVO> getQuestionVoPage(Page<Question> questionPage);

}
