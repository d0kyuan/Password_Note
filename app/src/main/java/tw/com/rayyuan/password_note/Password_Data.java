package tw.com.rayyuan.password_note;

public class Password_Data {
    public Password_Data(String documetid,String account,String password,String appid,long dataId,long type,boolean is_Sectioned) {
        this.account = account;
        this.password = password;
        this.appid = appid;
        this.dataId = dataId;
        this.type = type;
        this.documetid = documetid;
        this.is_Sectioned = is_Sectioned;
    }
    String documetid = "";
    String account = "";
    String password = "";
    String appid = "";
    long dataId=0;
    long type = 0;
    boolean is_Sectioned = false;

}
