public class UserContext {
    class User {
        String userId;
     public User(){
        
     }
    
    }

    public static java.lang.ThreadLocal<User> holder = new java.lang.ThreadLocal<>();
    class ServiceOne{
        public void process(){
       User user=getUser();
       UserContext.holder.set(user);
    }
}
class Service2{
    public void process(){
        User user=UserContext.holder.get();//get user for this thread;
    }
}
}
