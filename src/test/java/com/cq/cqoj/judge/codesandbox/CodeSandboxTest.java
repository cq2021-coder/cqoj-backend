package com.cq.cqoj.judge.codesandbox;

import com.cq.cqoj.judge.codesandbox.factory.CodeSandboxFactory;
import com.cq.cqoj.judge.codesandbox.proxy.CodeSandboxProxy;
import com.cq.cqoj.judge.model.ExecuteCodeRequest;
import com.cq.cqoj.judge.model.ExecuteCodeResponse;
import com.cq.cqoj.model.enums.QuestionSubmitLanguageEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
@SpringBootTest
@Slf4j
class CodeSandboxTest {

    @Resource
    private CodeSandboxFactory codeSandboxFactory;
    @Test
    void executeCode() {
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance();
        String code = "public void main(String[] args){}";
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest request = ExecuteCodeRequest
                .builder()
                .code(code)
                .inputList(inputList)
                .language(QuestionSubmitLanguageEnum.JAVA)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(request);
        log.info("executeResponse: {}", executeCodeResponse);
    }

    @Test
    void executeCodeByProxy() {
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance();
        String code = "public void main(String[] args){}";
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest request = ExecuteCodeRequest
                .builder()
                .code(code)
                .inputList(inputList)
                .language(QuestionSubmitLanguageEnum.JAVA)
                .build();
        new CodeSandboxProxy(codeSandbox).executeCode(request);
    }
}
