package basic.app.com.basicres.basicclass

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import basic.app.com.basiclib.baseclass.BasePresenter
import basic.app.com.basiclib.baseclass.IStateView
import basic.app.com.basiclib.helper.net.download.DownloadRetrofit
import basic.app.com.basiclib.utils.EncryptUtil
import basic.app.com.basiclib.utils.FileUtil
import basic.app.com.basiclib.utils.MathUtil
import basic.app.com.basiclib.utils.NumberFormatUtil
import basic.app.com.basiclib.utils.logger.LogUtil
import basic.app.com.basicres.R
import basic.app.com.basicres.R2
import butterknife.BindView
import butterknife.ButterKnife
import com.github.barteksc.pdfviewer.PDFView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File

/**
 * author : user_zf
 * date : 2018/9/4
 * desc : Pdf阅读器
 */
class PdfFragment : BaseFragment<BasePresenter<*>>() {

    @BindView(R2.id.pdfView)
    lateinit var pdfView: PDFView

    private var url: String? = null

    private var pdfPath = ""

    override fun getLayoutResource(): Int = R.layout.fragment_pdf


    override fun initLayout(view: View) {
        super.initLayout(view)
        ButterKnife.bind(this, view)
        url = arguments?.getString(EXTRA_PDF_URL)
        if (TextUtils.isEmpty(url)) {
            setState(IStateView.ViewState.EMPTY)
        } else {
            //根据url生成一个名称，可以防止重复下载PDF文件
            pdfPath = FileUtil.getPdfPath(EncryptUtil.getMd5Value(url) + ".pdf")
            onLoadData()
        }
    }

    override fun onLoadData() {
        if (File(pdfPath).exists()) { //存在该pdf文件，直接展示
            showPdf(File(pdfPath))
        } else { //下载pdf文件再展示
            downloadPdf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pdfView != null) {
            pdfView.recycle()
        }
    }

    /**
     * 下载并显示pdf文件
     */
    private fun downloadPdf() {
        showWaiting("", true)
        DownloadRetrofit.getInstance { bytesRead, contentLength, done ->
            LogUtil.i("zf_tag", "当前进度：" + NumberFormatUtil.formatPercent(MathUtil.div(bytesRead.toString(), contentLength.toString(), 2)))
            //TODO user_zf create on 2018/9/4, Desc: 优化体验，可以展示下载进度条
        }
                .download(url, pdfPath)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //下载成功，展示pdf文件
                    dismissWaiting()
                    showPdf(it)
                }
    }

    private fun showPdf(pdf: File) {
        pdfView.fromFile(pdf)
                .enableSwipe(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .load()
    }

    companion object {

        const val EXTRA_PDF_URL = "EXTRA_PDF_URL"
        @JvmStatic
        fun newInstance(url: String?): PdfFragment {
            val fragment = PdfFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_PDF_URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }
}
