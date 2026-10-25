#include <iostream>
#include <queue>
#include <chrono>
#include <mutex>

using namespace std;

class SlidingWindowRateLimiter {
private:
    queue<long long> timestamps;      // request timestamps (ms)
    int maxRequests;
    long long windowSizeMs;
    mutex mtx;

    long long currentTimeMillis() {
        return chrono::duration_cast<chrono::milliseconds>(
                   chrono::system_clock::now().time_since_epoch())
            .count();
    }

public:
    SlidingWindowRateLimiter(int limit, long long windowMs)
        : maxRequests(limit), windowSizeMs(windowMs) {}

    bool allowRequest() {
        lock_guard<mutex> lock(mtx);

        long long now = currentTimeMillis();

        // Remove expired requests
        while (!timestamps.empty() &&
               timestamps.front() <= now - windowSizeMs) {
            timestamps.pop();
        }

        // Check rate limit
        if (timestamps.size() >= maxRequests)
            return false;

        timestamps.push(now);
        return true;
    }
};
