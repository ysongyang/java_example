package cn.liuxiang.security.util;

/**
 * 定义统一的返回格式
 *
 * @Auther: ysongyang (zz1.com.cn)
 * @Date: 2018/11/29 0029 15:13
 * @Description:
 */
public class Result<T> {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 泛型返回对象
     */
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
