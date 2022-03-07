package com.example.demo.exception;



import lombok.Data;

@Data
public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = -7700586742426745129L;
	private String errorCode;
	private String clientMsg;
	private String serverMsg;
	private String data;

	public ServiceException(ResultCodeEnum resultCodeEnum, String clientMsg) {
		super(String.format("%s[%s]", resultCodeEnum.code, clientMsg));
		this.errorCode = resultCodeEnum.code;
		this.clientMsg = clientMsg;
		this.serverMsg = clientMsg;
	}

	public ServiceException(ResultCodeEnum resultCodeEnum) {
		super(String.format("%s[%s]", resultCodeEnum.code, resultCodeEnum.serverMsg));
		this.errorCode = resultCodeEnum.code;
		this.clientMsg = resultCodeEnum.clientMsg;
		this.serverMsg = resultCodeEnum.serverMsg;
	}

	public ServiceException(String errorCode, String errorMsg, String data) {
		super(String.format("%s", errorMsg));
		this.errorCode = errorCode;
		this.clientMsg = errorMsg;
		this.serverMsg = errorMsg;
		this.data = data;
	}

	public ServiceException(String errorCode, String errorMsg) {
		super(String.format("%s[%s]", errorCode, errorMsg));
		this.errorCode = errorCode;
		this.clientMsg = errorMsg;
		this.serverMsg = errorMsg;
	}

	public ServiceException(String errorMsg) {
		super(String.format("%s[%s]", "FAIL", errorMsg));
		this.errorCode = "FAIL";
		this.clientMsg = errorMsg;
		this.serverMsg = errorMsg;
	}

	private String generateMsg(String code, String msg) {
		return String.format("%s[%s]", msg, code);
	}

	public String generateServerMsg() {
		return String.format("%s[%s]", serverMsg, errorCode);
	}

	public String generateClientMsg() {
		return String.format("%s[%s]", clientMsg, errorCode);
	}

	public static ServiceException error(ResultCodeEnum resultCodeEnum, String clientMsg) {
		return new ServiceException(resultCodeEnum, clientMsg);
	}
}
