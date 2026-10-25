import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo {
    CompletableFuture<UserProfile> profileFuture =
    CompletableFuture.supplyAsync(() -> userService.getProfile(userId), executor);

CompletableFuture<List<Order>> ordersFuture =
    CompletableFuture.supplyAsync(() -> orderService.getOrders(userId), executor);

CompletableFuture<UserDashboard> dashboard = profileFuture
    .thenCombine(ordersFuture, (profile, orders) -> new UserDashboard(profile, orders))
    .exceptionally(ex -> {
        log.error("Dashboard build failed", ex);
        return UserDashboard.fallback();
    });

UserDashboard result = dashboard.get(2, TimeUnit.SECONDS); // bounded wait at the edge
    
}
