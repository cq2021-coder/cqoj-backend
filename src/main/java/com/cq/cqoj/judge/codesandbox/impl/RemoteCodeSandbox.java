package com.cq.cqoj.judge.codesandbox.impl;

import com.cq.cqoj.judge.codesandbox.CodeSandbox;
import com.cq.cqoj.judge.model.ExecuteCodeRequest;
import com.cq.cqoj.judge.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 远程代码沙箱
 *
 * @author 程崎
 * @since 2023/08/15
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("远程代码沙箱");
        return null;
    }
}
