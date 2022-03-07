package com.example.demo.controller;


import com.example.demo.common.RestResponse;
import com.example.demo.service.TouchUserDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/open/touch/user")
@Api(tags = "客户触达")
public class TouchUserController {

    @Autowired
    private TouchUserDataService touchUserDataService;

    @PostMapping("/add")
    @ApiOperation("新增用户触达任务")
    public RestResponse<String> add(){
        String flowNo = touchUserDataService.start();
        return RestResponse.success(flowNo);
    }

    @PostMapping("/query/{flow_no}")
    @ApiOperation(value = "查询触发任务执行结果", notes = "返回true时为执行成功，返回false时为执行失败")
    public RestResponse<Boolean> query(@PathVariable("flow_no") @ApiParam("流水号") String flowNo){
        return RestResponse.success(touchUserDataService.queryTask(flowNo));
    }

    @PostMapping(value = "/testFile")
    @ApiOperation(value = "测试文件接收")
    public void testFile(@ApiParam(value = "触达文件", required = true) @RequestParam(name = "files")MultipartFile files,
                         @ApiParam(value = "订单ID", required = true) @RequestParam(name = "orderId") String orderId,
                         @ApiParam("工号") @RequestParam(name = "empNo") String empNo){
        System.out.println(files.getSize());
        System.out.println(files.getName());
        System.out.println(orderId.concat("-").concat(empNo));
    }

}
