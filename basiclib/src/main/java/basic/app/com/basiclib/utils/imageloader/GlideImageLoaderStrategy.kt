package basic.app.com.basiclib.utils.imageloader

import android.annotation.DrawableRes
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
import android.os.Looper
import android.text.TextUtils
import android.widget.ImageView
import basic.app.com.basiclib.utils.FileUtil
import basic.app.com.basiclib.utils.imageloader.listener.ImageSaveListener
import basic.app.com.basiclib.utils.imageloader.listener.ImgLoaderListener
import basic.app.com.basiclib.utils.imageloader.transformation.GlideCircleTransform
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * author : user_zf
 * date : 2018/8/30
 * desc : Glide实现的加载策略
 * fixme 下面有些调用的使用方式是不合理的，特别在于对释放资源的使用上，详见：https://muyangmin.github.io/glide-docs-cn/doc/resourcereuse.html
 */
class GlideImageLoaderStrategy : BaseImageLoaderStrategy {

    override fun loadImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView) {
        loadNormal(imageView.context, url, placeholder, imageView)
    }

    override fun loadImage(context: Context, url: String?, @DrawableRes placeholder: Int, imageView: ImageView) {
        loadNormal(context, url, placeholder, imageView)
    }

    /**
     * 无holder的gif加载
     *
     * @param url
     * @param imageView
     */
    override fun loadImage(url: String?, imageView: ImageView) {
        val myOptions: RequestOptions = RequestOptions()
                .placeholder(imageView.drawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .transition(withCrossFade())
                .into(imageView)
    }

    override fun loadImage(url: String?, imageView: ImageView, useCache: Boolean) {
        val myOptions: RequestOptions = RequestOptions().placeholder(imageView.drawable)
                .diskCacheStrategy(if (useCache) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)


        Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .transition(withCrossFade())
                .into(imageView)
    }

    override fun loadImage(context: Context, url: String?, width: Int, height: Int, listener: ImgLoaderListener) {
        val myOptions = RequestOptions()
        myOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
        val request = Glide.with(context).asBitmap().load(url)
        if (width > 0 && height > 0) {
            myOptions.override(width, height)
        }
        request.apply(myOptions)
                .into(object : SimpleTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        if (resource != null) {
                            listener.onLoadComplete(resource)
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        listener.onLoadFailed(null)
                    }

                })

    }

    override fun loadCircleImage(url: String?, imageView: ImageView) {
        val myOptions: RequestOptions = RequestOptions().dontAnimate()
                .transform(GlideCircleTransform(imageView.context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .into(imageView)
    }

    override fun loadCircleImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView) {
        val myOptions: RequestOptions = RequestOptions().placeholder(placeholder)
                .dontAnimate()
                .transform(GlideCircleTransform(imageView.context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .into(imageView)
    }

    override fun loadCircleImage(url: String?, placeholder: Drawable, imageView: ImageView) {
        val myOptions: RequestOptions = RequestOptions().placeholder(placeholder)
                .dontAnimate()
                .transform(GlideCircleTransform(imageView.context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .into(imageView)
    }


    override fun loadCircleBorderImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView, borderWidth: Float, borderColor: Int) {
        val myOptions: RequestOptions = RequestOptions().placeholder(placeholder)
                .dontAnimate()
                .transform(GlideCircleTransform(imageView.context, borderWidth, borderColor))
                .diskCacheStrategy(DiskCacheStrategy.ALL)


        Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .into(imageView)
    }

    override fun loadCircleBorderImage(url: String?, @DrawableRes placeholder: Int, imageView: ImageView, borderWidth: Float, borderColor: Int, heightPx: Int, widthPx: Int) {
        val myOptions: RequestOptions = RequestOptions().placeholder(placeholder)
                .dontAnimate()
                .transform(GlideCircleTransform(imageView.context, borderWidth, borderColor, heightPx, widthPx))
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .into(imageView)
    }


    override fun loadImageWithAppCxt(url: String?, imageView: ImageView) {
        val myOptions: RequestOptions = RequestOptions()
                .placeholder(imageView.drawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(imageView.context.applicationContext)
                .load(url)
                .apply(myOptions)
                .transition(withCrossFade())
                .into(imageView)
    }

//    override fun loadGifImage(url: String?, placeholder: Int, imageView: ImageView) {
//        loadGif(imageView.context, url, placeholder, imageView)
//    }

    override fun clearImageDiskCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread { Glide.get(context.applicationContext).clearDiskCache() }.start()
            } else {
                Glide.get(context.applicationContext).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun clearImageMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context.applicationContext).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun trimMemory(context: Context, level: Int) {
        Glide.get(context).trimMemory(level)
    }

    override fun getCacheSize(context: Context): String {
        try {
            return FileUtil.getFormatSize(FileUtil.getFolderSize(Glide.getPhotoCacheDir(context.applicationContext)).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun saveImage(context: Context, url: String?, savePath: String, saveFileName: String, listener: ImageSaveListener) {
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState() || TextUtils.isEmpty(url)) {
            listener.onSaveFail()
            return
        }

        val cacheFile = Glide
                .with(context)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get()
        if (cacheFile == null || !cacheFile.exists()) {
            listener.onSaveFail()
            return
        }

        val dir = File(savePath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, saveFileName + FileUtil.getPicType(cacheFile.absolutePath))

        val fromStream = FileInputStream(cacheFile)
        val toStream = FileOutputStream(file)
        fromStream.use {
            while (true) {
                val bytes = it.readBytes()
                if (bytes.isEmpty()) break
                toStream.use {
                    it.write(bytes)
                }
            }
            listener.onSaveSuccess()
        }
    }

    /**
     * load image with Glide
     */
    private fun loadNormal(ctx: Context, url: String?, placeholder: Int, imageView: ImageView) {
        /**
         * 为其添加缓存策略,其中缓存策略可以为:Source及None,None及为不缓存,Source缓存原型.如果为ALL和Result就不行.然后几个issue的连接:
         * https://github.com/bumptech/glide/issues/513
         * https://github.com/bumptech/glide/issues/281
         * https://github.com/bumptech/glide/issues/600
         * modified by xuqiang
         */

        val myOptions: RequestOptions = RequestOptions()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)


        //去掉动画 解决与CircleImageView冲突的问题 这个只是其中的一个解决方案
        //使用SOURCE 图片load结束再显示而不是先显示缩略图再显示最终的图片（导致图片大小不一致变化）
        Glide.with(ctx)
                .load(url)
                .apply(myOptions)
                .transition(withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .into(imageView)
    }
//
//    /**
//     * load gif with Glide
//     */
//    private fun loadGif(ctx: Context, url: String?, placeholder: Int, imageView: ImageView) {
//
//        val myOptions: RequestOptions =  RequestOptions()
//                .placeholder(placeholder)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.DATA)
//
//
//        Glide.with(ctx)
//                .load(url)
//                .apply(myOptions)
//                .apply(RequestOptions.decodeTypeOf(GifDrawable::class.java))
//                .listener(object : RequestListener<String?, GifDrawable?> {
//                    override fun onException(e: Exception, model: String?, target: Target<GifDrawable?>, isFirstResource: Boolean): Boolean = false
//
//                    override fun onResourceReady(resource: GifDrawable?, model: String?, target: Target<GifDrawable?>,
//                                                 isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean = false
//                })
//                .into(imageView)
//    }

}
