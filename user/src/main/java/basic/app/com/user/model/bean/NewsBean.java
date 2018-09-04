package basic.app.com.user.model.bean;


/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 重要新闻bean
 */
public class NewsBean {
    public final static String IMPORTANT_NEWS_ASSET_ID = "important";
    private String artid;
    private String title;
    private long timestamp;
    private String url;
    private String assetId = IMPORTANT_NEWS_ASSET_ID;
    private String logo_url;
    private String site; //平台枚举值

    public String getArtid() {
        return artid;
    }

    public void setArtid(String artid) {
        this.artid = artid;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
