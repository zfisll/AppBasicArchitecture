package basic.app.com.basicres.basicclass;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.compress.CompressImage;
import org.devio.takephoto.compress.CompressImageImpl;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.TContextWrap;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.util.ArrayList;

import basic.app.com.basiclib.utils.CollectionUtil;
import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.utils.FileUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basiclib.utils.ToastUtil;
import basic.app.com.basiclib.utils.UriUtil;
import basic.app.com.basiclib.widget.AdvancedWebView;
import basic.app.com.basicres.R;
import basic.app.com.basicres.R2;
import basic.app.com.basicres.widget.DialogBuilderProxy;
import basic.app.com.basicres.widget.MyAdvancedWebView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * author : user_zf
 * date : 2018/9/4
 * desc : 加强版 WebActivity,使用第三方控件 AdvancedWebView ,支持上传文件等多种功能
 * 详情见 https://github.com/delight-im/Android-AdvancedWebView
 * todo 该Fragment实现了很多功能，用的过程有待调试
 */
public class AdvanceWebFragment extends BaseFragment implements AdvancedWebView.Listener, TakePhoto.TakeResultListener, InvokeListener {

    private OnFragmentInteractionListener mListener;

    private static final String ARG_URL = "ARG_URL";
    private static final String PHOTO_URL = "photoUrl";
    public static final int REQUEST_FILE_CODE = 1024;
    public static final int REQUEST_CAMERA = 1111;

    @BindView(R2.id.webView)
    MyAdvancedWebView webView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.flRoot)
    FrameLayout flRoot;

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private Uri mCameraFileUri;

    private boolean isNewWayChoosePhoto = false; //是否新的选择图片的方式

    public static AdvanceWebFragment newInstance(String url) {
        AdvanceWebFragment fragment = new AdvanceWebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_web_view;
    }

    @Override
    public boolean isRefreshEnable() {
        return false;
    }

    @Override
    public void onLoadData() {
        super.onLoadData();
        if (!TextUtils.isEmpty(webView.getUrl())) {
            webView.reload();
        } else {
            setState(ViewState.EMPTY);
        }
    }

    @Override
    public void initLayout(View view) {
        super.initLayout(view);
        ButterKnife.bind(this, view);
        initView(); //初始化组件
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getTakePhoto() != null) {
            getTakePhoto().onCreate(savedInstanceState);
        }
    }

    private void initView() {
        //webView设置WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                resetRefreshStatus();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                resetRefreshStatus();
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                // TODO: 2018/9/4 此处需要判断是否特殊的url，如tel:、或者自定义的协议
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 加载url结束后，重置下拉刷新状态
                resetRefreshStatus();

                if (mListener != null) {
                    mListener.onWebLoadedFinish(url);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                if (view.getUrl().contains("bluestonehk.com")) { // TODO: 2018/9/4 此处的域名改成公司的域名
                    handler.proceed();
                } else {
                    new DialogBuilderProxy(getActivity())
                            .setMessage(Html.fromHtml(ResourceUtil.getString(R.string.ssl_error_for_other)))
                            .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.cancel();
                                }
                            })
                            .setPositiveButton(R.string.ok_to_visit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.proceed();
                                }
                            })
                            .show();
                }
            }
        });
        //webView设置WebChromeClient
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mListener != null) {
                    mListener.onReceivedTitle(view.getUrl(), title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                    if (newProgress >= 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomView = view;
                flRoot.addView(mCustomView);
                mCustomViewCallback = callback;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                if (mCustomView == null) {
                    return;
                }
                flRoot.removeView(mCustomView);
                mCustomViewCallback.onCustomViewHidden();
                mCustomView = null;
                mCustomViewCallback = null;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                super.onHideCustomView();
            }
        });

        // 加载第一个URL
        webView.setListener(getActivity(), this, REQUEST_FILE_CODE);
        String url = getArguments().getString(ARG_URL);
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        } else {
            setState(ViewState.EMPTY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                if (isNewWayChoosePhoto) { //新的方式，上传图片
                    //对指定图片进行压缩
                    ArrayList images = new ArrayList();
                    images.add(TImage.of(mCameraFileUri, TImage.FromType.CAMERA));
                    CompressImageImpl.of(getActivity(), getCompressConfig(), images, new CompressImage.CompressListener() {
                        @Override
                        public void onCompressSuccess(ArrayList<TImage> images) {
                            //图片压缩成功，上传压缩图片
                            if (!CollectionUtil.isEmpty(images)) {
                                uploadPhoto(images.get(0).getCompressPath());
                            }
                        }

                        @Override
                        public void onCompressFailed(ArrayList<TImage> images, String msg) {
                            //图片压缩失败，上传原图
                            uploadPhoto(mCameraFileUri.getPath());
                        }
                    }).compress();
                } else {// 旧的方式，把Uri回调给webView
                    Intent data = new Intent();
                    data.setData(mCameraFileUri);
                    webView.onActivityResult(REQUEST_FILE_CODE, RESULT_OK, data);
                }
            } else {
                takeCancel();
            }
        } else {
            webView.onActivityResult(requestCode, resultCode, intent);
            if (getTakePhoto() != null) {
                getTakePhoto().onActivityResult(requestCode, resultCode, intent);
            }
        }
    }

    /**
     * webview加载新的url
     *
     * @param url 新的链接
     */
    public void onLoadNewUrl(String url) {
        if (!TextUtils.isEmpty(url) && webView != null) {
            webView.loadUrl(url);
        }
    }

    /**
     * 如果该fragment消费了返回键true,否则false
     */
    public boolean onBackPressed() {
        // 消费不是第一个URL的返回点击事件
        if (webView.canGoBack() && !TextUtils.equals(webView.getUrl(), getArguments().getString(ARG_URL))) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    @Override
    public void onFileUploadRequested(String fileType) {
        //此处是老的选择文件处理，但部分Android系统不一定能走到此处，之后的就用新的方式处理
        //实现方式：h5都需要选择文件的地方，发一个input类型的事件，webview处理事件，走到这个方法，弹出文件选择框，用户选择文件之后，直接把文件Uri传给h5，h5自己做上传七牛获取url操作
        isNewWayChoosePhoto = false;
        boolean chooseImage = !TextUtils.isEmpty(fileType) && fileType.contains("image");
        dealChooseFile(chooseImage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getTakePhoto() != null) {
            getTakePhoto().onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(getActivity(), type, invokeParam, this);
    }

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(getActivity(), this));
        }
        takePhoto.onEnableCompress(getCompressConfig(), true); //必须每次都设置,因为压缩成功后会清空
        return takePhoto;
    }

    /**
     * 获取图片压缩配置
     */
    public CompressConfig getCompressConfig() {
        //此处为了尽量保证图片的清晰度，所以压缩比率并不大
        return new CompressConfig.Builder()
                .setMaxSize(1024 * 1024) //最大1M
                .setMaxPixel(1000) //最大尺寸1000px
                .create();
    }

    @Override
    public void takeSuccess(TResult result) {
        //获取压缩之后的图片
        String imagePath = result.getImage().getCompressPath();
        if (isNewWayChoosePhoto) {//新的选取图片方式：Native把图片上传到七牛，把url返给h5
            uploadPhoto(imagePath);
        } else { // 老的选取图片方式：获取成功后把uri回传给webview去处理
            Uri uri = Uri.fromFile(new File(imagePath));
            Intent intent = new Intent();
            intent.setData(uri);
            webView.onActivityResult(REQUEST_FILE_CODE, RESULT_OK, intent);
        }
    }

    /**
     * 上传图片
     *
     * @param imagePath
     */
    private void uploadPhoto(String imagePath) {
        // TODO: 2018/9/4 上传图片，直接用H5页面上传在不同的手机上可能会出现各种问题。现在的方案是，通过Native上传之后，回传一个图片链接个H5页面。
        // TODO: 2018/9/4 图片可以上传到自己的服务器，也可以上传到七牛云存储
    }

    /**
     * 获取失败或取消要回传操作结果给webView，否则会导致无法再操作上传
     */
    @Override
    public void takeFail(TResult result, String msg) {
        if (isNewWayChoosePhoto) {
            ToastUtil.showToast(msg);
        } else {
            webView.onActivityResult(REQUEST_FILE_CODE, RESULT_CANCELED, null);
        }
    }

    @Override
    public void takeCancel() {
        webView.onActivityResult(REQUEST_FILE_CODE, RESULT_CANCELED, null);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(getActivity()), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    //处理选择文件请求
    private void dealChooseFile(boolean isPhoto) {
        if (isDetached()) {
            takeCancel();
            return;
        }
        final RxPermissions rxPermissions = new RxPermissions(AdvanceWebFragment.this);
        //弹出对话框
        new AlertDialog.Builder(getActivity())
                .setTitle(isPhoto ? R.string.image_source_selection : R.string.file_source_selection)
                .setItems(isPhoto ? R.array.choose_image : R.array.choose_file,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: //拍照，拍照后处理图片也需要sd卡权限，否则获取不到文件也会导致后续的上传失败
                                        rxPermissions.requestEachCombined(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                                .subscribe(new Consumer<Permission>() {
                                                    @Override
                                                    public void accept(Permission permission) throws Exception {
                                                        if (permission.granted) { //授权成功
                                                            goCamera();
                                                        } else if (permission.shouldShowRequestPermissionRationale) { //授权失败，不能再次请求，只能到系统设置页面去修改权限
                                                            takeCancel();
                                                        } else { //授权失败
                                                            takeCancel();
                                                            showGoSettingDialog(permission);
                                                        }
                                                    }

                                                });
                                        break;
                                    case 1: //手机相册
                                        rxPermissions.requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                .subscribe(new Consumer<Permission>() {
                                                    @Override
                                                    public void accept(Permission permission) throws Exception {
                                                        if (permission.granted) { //授权成功
                                                            pickFromAlbum();
                                                        } else if (permission.shouldShowRequestPermissionRationale) { //授权失败，不能再次请求，只能到系统设置页面去修改权限
                                                            takeCancel();
                                                        } else { //授权失败
                                                            takeCancel();
                                                            showGoSettingDialog(permission);
                                                        }
                                                    }

                                                });
                                        break;
                                    case 2: // 其它
                                        rxPermissions.requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                .subscribe(new Consumer<Permission>() {
                                                    @Override
                                                    public void accept(Permission permission) throws Exception {
                                                        if (permission.granted) { //授权成功
                                                            pickFromFile();
                                                        } else if (permission.shouldShowRequestPermissionRationale) { //授权失败，不能再次请求，只能到系统设置页面去修改权限
                                                            takeCancel();
                                                        } else { //授权失败
                                                            takeCancel();
                                                            showGoSettingDialog(permission);
                                                        }
                                                    }

                                                });
                                }
                            }
                        }
                )
                .

                        setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                takeCancel();
                            }
                        })
                .

                        show();
    }

    private void pickFromAlbum() {
        getTakePhoto().onPickFromGallery();
    }

    /**
     * 弹出对话框提示进入设置页面，打开权限
     */
    private void showGoSettingDialog(Permission permission) {
        new DialogBuilderProxy(getActivity())
                .setMessage(ResourceUtil.getString(R.string.lib_need_permission, permission.name))
                .setNegativeButton(R.string.lib_cancel, null)
                .setPositiveButton(R.string.lib_go_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        getActivity().startActivity(intent);
                    }
                })
                .show();
    }

    private void pickFromFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(i, REQUEST_FILE_CODE);
    }

    /**
     * 跳转相机拍照
     */
    private void goCamera() {
        mCameraFileUri = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机应用
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new File(FileUtil.getImagePath(System.currentTimeMillis() + ".jpg"));
                if (!photoFile.getParentFile().exists()) {
                    photoFile.getParentFile().mkdirs();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                // 记住原始的uri，用于回调webView去上传
                mCameraFileUri = Uri.fromFile(photoFile);
                // 判断系统版本，兼容7.0的文件权限
                Uri photoURI = UriUtil.getUri(getActivity(), photoFile, DeviceUtil.getPackageName());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }


    public String getCurrentWebUrl() {
        return webView.getUrl();
    }

    public interface OnFragmentInteractionListener {
        void onIsShare(String url, boolean isShare);

        void onWebLoadedFinish(String url);

        void onReceivedTitle(String url, String title);
    }
}
