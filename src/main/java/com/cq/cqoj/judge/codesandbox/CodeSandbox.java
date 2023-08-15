package com.cq.cqoj.judge.codesandbox;

import com.cq.cqoj.judge.model.ExecuteCodeRequest;
import com.cq.cqoj.judge.model.ExecuteCodeResponse;

/**
 * 代码沙箱
 *
 * @author 程崎
 * @since 2023/08/15
 */
public interface CodeSandbox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return {@link ExecuteCodeResponse}
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
