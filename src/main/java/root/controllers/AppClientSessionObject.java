package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import root.app.AppConfig;

import java.util.LinkedHashMap;
import java.util.Map;

class AppClientSessionObject {
    Map<String, Boolean> reviewLikeMap = new LinkedHashMap<>();

    private AppClientSessionObject(HttpServletRequest req) {
        var session = req.getSession();
        if (session.getAttribute(AppConfig.SESSION_ROOT_KEY) == null) {
            session.setAttribute(AppConfig.SESSION_ROOT_KEY, this);
        }
    }

    public static AppClientSessionObject getOrCreateClientSessionObject(HttpServletRequest req) {
        var session = req.getSession();
        AppClientSessionObject cso = (AppClientSessionObject) session.getAttribute(AppConfig.SESSION_ROOT_KEY);
        if (cso == null) {
            cso = new AppClientSessionObject(req);
            session.setAttribute(AppConfig.SESSION_ROOT_KEY, cso);
        }
        return cso;
    }

    public Map<String, Boolean> getReviewLikeMap() {
        return reviewLikeMap;
    }
/*
    public boolean hasLikedReview(long reviewId) {
        return reviewLikeMap.getOrDefault("like_" + reviewId, false);
    }
    public boolean hasDislikedReview(long reviewId) {
        return reviewLikeMap.getOrDefault("dislike_" + reviewId, false);
    }

    public void markReviewLiked(long reviewId) {
        reviewLikeMap.put("like_" + reviewId, true);
    }
    public void markReviewDisliked(long reviewId) {
        reviewLikeMap.put("dislike_" + reviewId, true);
    }

 */
}
