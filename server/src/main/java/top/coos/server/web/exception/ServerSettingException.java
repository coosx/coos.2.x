package top.coos.server.web.exception;

import top.coos.util.StrUtil;

/**
 * 设置异常
 */
public class ServerSettingException extends RuntimeException {

	private static final long serialVersionUID = -4134588858314744501L;

	public ServerSettingException(Throwable e) {

		super(e);
	}

	public ServerSettingException(String message) {

		super(message);
	}

	public ServerSettingException(String messageTemplate, Object... params) {

		super(StrUtil.format(messageTemplate, params));
	}

	public ServerSettingException(String message, Throwable throwable) {

		super(message, throwable);
	}

	public ServerSettingException(Throwable throwable, String messageTemplate, Object... params) {

		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
