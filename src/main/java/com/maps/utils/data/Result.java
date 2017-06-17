package com.maps.utils.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 更新结果类：service 或者 action 返回值
 *
 * @author sunchuanbao
 */
public class Result<T extends Object> implements Serializable {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private boolean rlt=true; //成功-true  失败-false
    private String msg;//返回消息
    private String code;//状态码
    private String time =  sf.format(new Date()); //服务器返回的时间戳
    private T data;//返回附加信息 可以是String、List、JSON、Map 类型
    
    static String succCode = "000000";
    static String errorCode = "100000";
    
    public interface Command<D> {
        D execute();
    }

    public Result() {
        super();
    }

    public Result(boolean success) {
        if (success) {
            this.setMsg("操作成功");//操作成功
            this.setCode(succCode);
        } else {
            this.setMsg("error");//操作失败
            this.setCode(errorCode);
        }
    }

    public Result(boolean success, String msg) {
        super();
        this.rlt = success;
        this.msg = msg;
    }
    
    public Result(boolean success, String code, String msg) {
        super();
        this.code = code;
        this.rlt = success;
        this.msg = msg;
    }
    
    public <D> Result(boolean success, String code, String msg, T data){
        
        super();
        this.code = code;
        this.rlt = success;
        this.msg = msg;
        this.data = data;
    }

    public static <D> Result<D> build(Command<D> cmd) {
        D data = null;
        Result<D> result = new Result<D>();
        try {
            data = cmd.execute();
            result.setMsg("操作成功");
            result.setCode(succCode);
            result.setData(data);
        } catch (Exception e) {
            //e.printStackTrace();
            //logger.error(e.getMessage(), e);
           /* if(e.getCode() == null){
                logger.error(e.getMessage(), e);
            }else{
                logger.error(e.getCode().code+":"+e.getMessage());
            }*/
            result.setCode(errorCode);
            result.setMsg(e.getMessage());
            result.setRlt(false);
        } catch (Throwable e) {
            e.printStackTrace();
            result.setCode(errorCode);
            result.setRlt(false);
            result.setMsg("unknow exception");
        }
        return result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean getRlt() {
		return rlt;
	}

	public void setRlt(boolean rlt) {
		this.rlt = rlt;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}

