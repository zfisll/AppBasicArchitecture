package basic.app.com.basiclib.helper.net.download;

/**
 * author : user_zf
 * date : 2018/9/4
 * desc : 进度监听器
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
