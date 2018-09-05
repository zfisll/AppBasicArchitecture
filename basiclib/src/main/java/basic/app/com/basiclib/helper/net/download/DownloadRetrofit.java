package basic.app.com.basiclib.helper.net.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import basic.app.com.basiclib.utils.FileUtil;
import basic.app.com.basiclib.utils.logger.LogUtil;
import io.reactivex.Observable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * author : user_zf
 * date : 2018/9/4
 * desc : 专注于下载的 Retrofit 实现,支持进度监听,最终返回一个 File 对象
 */
public class DownloadRetrofit {

    private final DownloadService mService;

    public static DownloadRetrofit getInstance(ProgressListener progressListener) {
        return new DownloadRetrofit(progressListener);
    }

    public DownloadRetrofit(ProgressListener progressListener) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Integer.MAX_VALUE, TimeUnit.MILLISECONDS)   //下载超时设为无限大
                .addInterceptor(new DownloadProgressInterceptor(progressListener))
                .retryOnConnectionFailure(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://www.baidu.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mService = retrofit.create(DownloadService.class);
    }

    /**
     * @param url      完整的下载地址
     * @param filePath 下载后文件存储位置的绝对路径 格式为 Environment.getExternalStorageDirectory().getAbsolutePath()+"/basic/"+文件名(包含后缀);
     */
    public Observable<File> download(String url, final String filePath) {
        return mService.download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        //这一步将文件流写入文件
                        FileOutputStream outputStream = null;
                        try {
                            byte[] bytes = responseBody.bytes();
                            File file = new File(filePath);
                            outputStream = FileUtil.openOutputStream(file);
                            outputStream.write(bytes);
                            return file;
                        } catch (IOException e) {
                            LogUtil.e(e, e.getMessage());
                            e.printStackTrace();
                            throw Exceptions.propagate(e); //抛给 Subscriber 的 onError 处理
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    LogUtil.e(e, e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }
}
