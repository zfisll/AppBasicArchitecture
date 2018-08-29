package basic.app.com.user.model.bean;

public class UserBean {
    private String avatar;  //头像
    private String email;   //邮箱
    private int is_witness; //是否见证人
    private String nick_name;  //昵称
    private String phone;  //电话
    private String session; //登录态session
    private String user_id; //用户id
    private String stock_account; //客户号
    private String open_status; //开户状态(new)
    private String name;//姓名
    private String certificate_type;//证件类型
    private String id_card;//证件号码
    private int is_double_check; //是否做过双重认证（1=是 0=否）

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIs_witness() {
        return is_witness;
    }

    public void setIs_witness(int is_witness) {
        this.is_witness = is_witness;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStock_account() {
        return stock_account;
    }

    public void setStock_account(String stock_account) {
        this.stock_account = stock_account;
    }

    public String getOpen_status() {
        return open_status;
    }

    public void setOpen_status(String open_status) {
        this.open_status = open_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertificate_type() {
        return certificate_type;
    }

    public void setCertificate_type(String certificate_type) {
        this.certificate_type = certificate_type;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public int getIs_double_check() {
        return is_double_check;
    }

    public void setIs_double_check(int is_double_check) {
        this.is_double_check = is_double_check;
    }
}
