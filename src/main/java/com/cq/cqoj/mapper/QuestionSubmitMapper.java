package com.cq.cqoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.cqoj.model.entity.QuestionSubmit;
import com.cq.cqoj.model.vo.QuestionSubmitViewVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 程崎
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2023-08-08 21:24:55
* @Entity com.cq.cqoj.model.entity.QuestionSubmit
*/
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {

    /**
     * 查询数量
     *
     * @param title    标题
     * @param language 语言
     * @return long
     */
    long countQuestionSubmit(@Param("title") String title, @Param("language") String language);

    /**
     * 根据问题标题和语言搜索记录
     *
     * @param title     标题
     * @param language  语言
     * @param pageIndex 页面索引
     * @param size      size
     * @return {@link List }<{@link QuestionSubmitViewVO }>
     */
    List<QuestionSubmitViewVO> selectQuestionSubmit(@Param("title") String title, @Param("language") String language, @Param("pageIndex") long pageIndex, @Param("size") long size);

    /**
     * 根据问题标题和语言搜索记录
     *
     * @param title     标题
     * @param language  语言
     * @param pageIndex 页面索引
     * @param size      size
     * @param userId    用户id
     * @return {@link List }<{@link QuestionSubmitViewVO }>
     */
    List<QuestionSubmitViewVO> selectQuestionSubmitByUserId(@Param("title") String title, @Param("language") String language, @Param("pageIndex") long pageIndex, @Param("size") long size, @Param("userId") Long userId);
}




