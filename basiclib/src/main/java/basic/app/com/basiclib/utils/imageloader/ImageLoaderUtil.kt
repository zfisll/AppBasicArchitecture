package basic.app.com.basiclib.utils.imageloader

import android.annotation.DrawableRes
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import basic.app.com.basiclib.utils.imageloader.listener.ImageSaveListener
import basic.app.com.basiclib.utils.imageloader.listener.ImgLoaderListener

/**
 * author : user_zf
 * date : 2018/8/30
 * desc : 图片加载工具，对外提供服务，可提换图片三方库，类似于外观模式
 */
object ImageLoaderUtil {
    //本应该使用策略模式，用基类声明，但是因为Glide特殊问题
    //持续优化更新
    private var mStrategy: BaseImageLoaderStrategy = GlideImageLoaderStrategy()

    fun loadImage(url: String?, imageView: ImageView) {
        mStrategy.loadImage(url, imageView)
    }

    fun loadImage(url: String?, imageView: ImageView, useCache: Boolean) {
        mStrategy.loadImage(url, imageView, useCache)
    }

    fun loadImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView) {
        mStrategy.loadImage(imageView.context, url, placeholder, imageView)
    }

    fun loadImage(context: Context, url: String?, width: Int, height: Int, listener: ImgLoaderListener) {
        mStrategy.loadImage(context, url, width, height, listener)
    }

//    fun loadGifImage(url: String?, placeholder: Int, imageView: ImageView) {
//        mStrategy.loadGifImage(url, placeholder, imageView)
//    }

    fun loadCircleImage(url: String?, imageView: ImageView) {
        mStrategy.loadCircleImage(url, imageView)
    }

    fun loadCircleImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView) {
        mStrategy.loadCircleImage(url, placeholder, imageView)
    }

    fun loadCircleImage(url: String?, placeholder: Drawable, imageView: ImageView) {
        mStrategy.loadCircleImage(url, placeholder, imageView)
    }

    fun loadCircleBorderImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView, borderWidth: Float, borderColor: Int) {
        mStrategy.loadCircleBorderImage(url, placeholder, imageView, borderWidth, borderColor)
    }

    fun loadCircleBorderImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView, borderWidth: Float, borderColor: Int, heightPX: Int, widthPX: Int) {
        mStrategy.loadCircleBorderImage(url, placeholder, imageView, borderWidth, borderColor, heightPX, widthPX)
    }

    fun loadImageWithAppCxt(url: String?, imageView: ImageView) {
        mStrategy.loadImageWithAppCxt(url, imageView)
    }


    /**
     * 策略模式的注入操作
     *
     * @param strategy
     */
    fun setLoadImgStrategy(strategy: BaseImageLoaderStrategy) {
        mStrategy = strategy
    }

    /**
     * 清除图片磁盘缓存
     */
    fun clearImageDiskCache(context: Context) {
        mStrategy.clearImageDiskCache(context)
    }

    /**
     * 清除图片内存缓存
     */
    fun clearImageMemoryCache(context: Context) {
        mStrategy.clearImageMemoryCache(context)
    }

    /**
     * 根据不同的内存状态，来响应不同的内存释放策略
     *
     * @param context
     * @param level
     */
    fun trimMemory(context: Context, level: Int) {
        mStrategy.trimMemory(context, level)
    }

    /**
     * 清除图片所有缓存
     */
    fun clearImageAllCache(context: Context) {
        clearImageDiskCache(context.applicationContext)
        clearImageMemoryCache(context.applicationContext)
    }

    /**
     * 获取缓存大小
     *
     * @return CacheSize
     */
    fun getCacheSize(context: Context): String {
        return mStrategy.getCacheSize(context)
    }

    fun saveImage(context: Context, url: String?, savePath: String, saveFileName: String, listener: ImageSaveListener) {
        mStrategy.saveImage(context, url, savePath, saveFileName, listener)
    }

}