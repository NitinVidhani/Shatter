package application.example.shatter.model;

public class User {

    private String phoneNo;
    private String userName;
    private String userAbout;
    private String imageUrl;
    private String countryCode;
    private String uid;

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public User(String phoneNo, String userName, String userAbout, String imageUrl, String countryCode, String uid, String status) {
        this.phoneNo = phoneNo;
        this.countryCode = countryCode;
        this.userName = userName;
        this.userAbout = userAbout;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.status = status;
    }

    public User() {
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAbout() {
        return userAbout;
    }

    public void setUserAbout(String userAbout) {
        this.userAbout = userAbout;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "User{" +
                "phoneNo='" + phoneNo + '\'' +
                ", userName='" + userName + '\'' +
                ", userAbout='" + userAbout + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }
}
