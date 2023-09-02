package com.cq.cqoj.judge.codesandbox.impl;

import com.cq.cqoj.judge.codesandbox.CodeSandbox;
import com.cq.cqoj.judge.model.ExecuteCodeRequest;
import com.cq.cqoj.judge.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("第三方代码沙箱");
        return null;
    }
}
