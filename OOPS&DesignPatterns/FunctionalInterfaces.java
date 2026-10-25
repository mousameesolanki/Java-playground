import java.util.stream.IntStream;
import java.util.stream.Stream;

interface Predicate<T> {
boolean test(T t);
}
interface Consumer<T> {
    void accept(T t);
}
interface Supplier<T>{
    T get();
}

interface Runnable{
    void run();
}
interface Callable<V>{
    V call() throws Exception;
}
//functional interfaces has single abstract method
//enables lambda in java 8
//labdas are code snippets represeint anonymus inner class ,single functionality ,algo and fit with funnctional interface
//lambdas are shorthand for FR
class User{
int id;
User(int id){
    this.id=id;
}
public int  getId(){
    return id;
}
}
Comparator<User> comparator = new Comparator<User>() {
    @Override
    public int compare(User u1,User u2){
        return Integer.compare(u1.getId(), u2.getId());
    }
};
Comparator<User> comp = (u1, u2) -> Integer.compare(u1.getId(), u2.getId());

interface Comparator<T>{
    int compare(T o1,T o2);
}
interface Function<T,R>{
    R apply(T t);
}
//functional interfaces in streams and collections
public class FunctionalInterfaces {
    public static void main(String[] args) {
        IntStream.Builder builder = IntStream.builder();
        builder.add(1).add(2).add(3);
        IntStream stream = builder.build();
        stream.forEach(System.out::println);
    }
}
