package basic.app.com.basiclib.utils.imageloader.listener;

import android.graphics.Bitmap;

/**
 * author : user_zf
 * date : 2018/8/30
 * desc : 图片加载箭筒
 */
public interface ImgLoaderListener {
    void onLoadComplete(Bitmap bitmap);

    void onLoadFailed(Throwable throwable);
}