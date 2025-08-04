package live.streaming.framework.web.starter.error;

public class LiveErrorException extends RuntimeException{

    private int errorCode;

    private String errorMsg;

    public LiveErrorException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public LiveErrorException(LiveBaseError liveBaseError) {
        this.errorCode = liveBaseError.getErrorCode();
        this.errorMsg = liveBaseError.getErrorMsg();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
