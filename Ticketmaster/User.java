public class User {
    private final String id;
    private final String userName;
    private final String email;
    private final Integer mobileNumber;

    User(String id,String userName,String email,Integer mobileNumber){
        this.id= id;
        this.userName= userName;
        this.email=email;
        this.mobileNumber=mobileNumber;
    }

    public Integer getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
}
