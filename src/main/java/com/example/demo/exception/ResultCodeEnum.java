package com.example.demo.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResultCodeEnum {
    /**
     * 通用入参错误,请用[-1]开始,且仅能用负数
     */
    SYS_COMMON_ERROR("-1", "系统繁忙,请稍后再试", "服务异常，请稍后重试"),
    /**
     * 通用入参错误,请用[-100]~[-199]定义
     */
    SYS_PARAM_ERROR("-100", "参数错误", "传入参数有误"),
    SYS_REQUEST_NULL("-101", "请求request对象为空", "请求对象为空"),
    SYS_REQUEST_PARAMS_NULL("-102", "请求参数为空", "传入参数为空"),
    SYS_REQUEST_PARAMS_HEADER_NULL("-103", "Header中的UserID为空", "用户信息为空"),

    /**
     * [1000]～[1999]
     */
    SAVE_DB_ERROR("1000", "保存数据异常", "保存数据异常"),
    QUERY_DB_ERROR("1001", "查询数据异常", "查询数据异常"),
    SERVER_ERROR("1002", "系统异常，请联系管理员", "系统异常，请联系管理员"),
    INVALID_INVOKE("1003", "非法调用", "非法调用"),

    /**
     * 通讯录异常码段
     */
    ADDRESS_BOOK_EVENT_CODE_NOT_FOUND("2000", "通讯录回调事件code不存在", "通讯录回调事件code不存在"),
    DEPARTMENT_NOT_FOUND("2001", "部门不存在", "部门不存在"),
    DEL_USER_FAIL_USER_NOT_FOUND("2002", "删除用户失败，用户不存在", "删除用户失败，用户不存在"),
    DEPARTMENT_POSITION_NULL("2003", "部门职务为空", "部门职务为空"),

    /**
     * 外部联系人异常码段
     */
    EXTERNAL_CONTACT_SYNC_FAIL("3001", "外部联系人同步错误", "外部联系人同步错误"),
    EXTERNAL_CONTACT_REMARK_FAIL("3004", "备注外部联系人异常", "备注外部联系人异常"),
    QUERY_CONCAT_ME_ERROR("3002", "获取联系我方式异常", "获取联系我方式异常"),
    CREATE_CONCAT_ME_ERROR("3003", "创建联系我方式异常", "创建联系我方式异常"),


    /**
     * 人员管理异常码
     */
    STAFF_USER_EXIST("4000", "人员已存在", "人员已存在"),
    STAFF_USER_NOT_EXIST("4001", "人员不存在", "人员不存在"),
    STAFF_OLD_PASSWORD_CHECK_ERROR("4002", "旧密码校验错误", "旧密码校验错误"),
    STAFF_STATUS_ERROR("4003", "人员状态异常", "人员状态异常"),


    /**
     * 职务人员异常码
     */
    POSITION_STAFF_EXIST("5000", "人员职位已存在", "人员职位已存在"),
    POSITION_NOT_EXIST("5001", "不存在该职务", "不存在该职务"),
    POSITION_STAFF_NULL("5002", "人员职务为空", "人员职务为空"),

    /**
     * auth相关异常码
     */
    AUTH_PERMISSION_DENIED("6000","无权操作","无权操作"),

    /**
     * OA异常码[7000]～[7999]
     */
    OA_EXISTS_PROCESSING("7000", "此客户有尚未办结的OA，建议完成审批或撤回后编辑，重新发起审批！", "此客户有尚未办结的OA，建议完成审批或撤回后编辑，重新发起审批！"),
    OA_HOUSE_MOVE_CHANGE_COUNTER("7001", "撤柜的列表中存在已撤离或待撤离的点位，不许发起审批", "撤柜的列表中存在已撤离或待撤离的点位，不许发起审批"),
    OA_REMOVE_DATE_INVALID("7002", "撤柜日期必须在法定工作日内", "撤柜日期必须在法定工作日内！"),
    OA_REMOVE_LIST_NULL("7003", "撤柜列表为空，不符合业务逻辑", "撤柜列表为空，不符合业务逻辑");


    /**
     * 错误码
     */
    public String code;

    /**
     * 服务端信息
     */
    public String serverMsg;

    /**
     * 客户端信息
     */
    public String clientMsg;

    public String getClientMsg() {
        return String.join(this.clientMsg, STRSTA, this.code, STREND);
    }

    private static final String STRSTA = "[";

    private static final String STREND = "]";


    @Override
    public String toString() {
        return String.join(this.clientMsg, STRSTA, this.serverMsg, STREND);
    }

}
