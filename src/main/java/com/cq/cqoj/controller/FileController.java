package com.cq.cqoj.controller;

import com.cq.cqoj.common.CommonResponse;
import com.cq.cqoj.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 文件控制器
 *
 * @author 程崎
 * @since 2023/08/03
 */
@RestController
@Api(tags = "file")
@RequestMapping("/user/file")
public class FileController {

    @Resource
    private FileService fileService;


    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public CommonResponse<String> upload(@RequestPart MultipartFile file) {
        return CommonResponse.success(fileService.fileUpload(file));
    }

    @PostMapping("/tmp")
    @ApiOperation("获取临时文件访问链接")
    public CommonResponse<String> getTempAccess(String key) {
        return CommonResponse.success(fileService.getTmpAccess(key));
    }
}
