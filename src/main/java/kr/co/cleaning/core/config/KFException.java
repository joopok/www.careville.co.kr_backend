package kr.co.cleaning.core.config;

public class KFException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private final int code;

	public KFException() {
		super("정의되지 않은 오류입니다.");
		this.code = 999;
	}

	public KFException(Throwable cause) {
		super(cause.getMessage());
		this.code = 999;
	}

	public KFException(String message) {
		super(message);
		this.code = 999;
	}

	public KFException(Throwable cause, int code) {
		super(cause.getMessage());
		this.code = code;
	}

	public KFException(String message, int code) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
