package basic.app.com.basiclib.utils.imageloader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import basic.app.com.basiclib.utils.imageloader.listener.ImageSaveListener
import basic.app.com.basiclib.utils.imageloader.listener.ImgLoaderListener

/**
 * author : user_zf
 * date : 2018/8/30
 * desc : 图片加载策略，通过实现该策略可以轻松替换三方库
 */
interface BaseImageLoaderStrategy {
    //无占位图
    fun loadImage(url: String?, imageView: ImageView)

    //这里的context指定为ApplicationContext
    fun loadImageWithAppCxt(url: String?, imageView: ImageView)

    fun loadImage(url: String?, imageView: ImageView, useCache: Boolean)

    fun loadImage(url: String?, placeholder: Int, imageView: ImageView)

    fun loadImage(context: Context, url: String?, placeholder: Int, imageView: ImageView)

    fun loadImage(context: Context, url: String?, width: Int, height: Int, listener: ImgLoaderListener)

    fun loadCircleImage(url: String?, imageView: ImageView)

    fun loadCircleImage(url: String?, placeholder: Int, imageView: ImageView)

    fun loadCircleImage(url: String?, placeholder: Drawable, imageView: ImageView)

    fun loadCircleBorderImage(url: String?, placeholder: Int, imageView: ImageView, borderWidth: Float, borderColor: Int)

    fun loadCircleBorderImage(url: String?, placeholder: Int, imageView: ImageView, borderWidth: Float, borderColor: Int, heightPx: Int, widthPx: Int)

//    fun loadGifImage(url: String?, placeholder: Int, imageView: ImageView)


    //清除硬盘缓存
    fun clearImageDiskCache(context: Context)

    //清除内存缓存
    fun clearImageMemoryCache(context: Context)

    //根据不同的内存状态，来响应不同的内存释放策略
    fun trimMemory(context: Context, level: Int)

    //获取缓存大小
    fun getCacheSize(context: Context): String

    fun saveImage(context: Context, url: String?, savePath: String, saveFileName: String, listener: ImageSaveListener)

}
