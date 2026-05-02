package com.striveonger.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 视频热度计算配置
 *
 * @author Mr.Lee
 * @since 2026-05-01
 */
@Component
@ConfigurationProperties(prefix = "own.vlog.hot")
public class VlogHotConfig {

    /**
     * 热门视频分数阈值
     */
    private Long threshold = 1800L;

    /**
     * 衰减配置
     */
    private DecayConfig decay = new DecayConfig();

    /**
     * 新视频加权配置
     */
    private NewVideoConfig newVideo = new NewVideoConfig();

    /**
     * 各指标权重配置
     */
    private WeightsConfig weights = new WeightsConfig();

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public DecayConfig getDecay() {
        return decay;
    }

    public void setDecay(DecayConfig decay) {
        this.decay = decay;
    }

    public NewVideoConfig getNewVideo() {
        return newVideo;
    }

    public void setNewVideo(NewVideoConfig newVideo) {
        this.newVideo = newVideo;
    }

    public WeightsConfig getWeights() {
        return weights;
    }

    public void setWeights(WeightsConfig weights) {
        this.weights = weights;
    }

    /**
     * 衰减配置
     */
    public static class DecayConfig {
        /**
         * 衰减底数
         */
        private Double base = 0.5;

        /**
         * 半衰期（天）
         */
        private Double halfLifeDays = 7.0;

        /**
         * 最小衰减系数
         */
        private Double minFactor = 0.3;

        public Double getBase() {
            return base;
        }

        public void setBase(Double base) {
            this.base = base;
        }

        public Double getHalfLifeDays() {
            return halfLifeDays;
        }

        public void setHalfLifeDays(Double halfLifeDays) {
            this.halfLifeDays = halfLifeDays;
        }

        public Double getMinFactor() {
            return minFactor;
        }

        public void setMinFactor(Double minFactor) {
            this.minFactor = minFactor;
        }
    }

    /**
     * 新视频加权配置
     */
    public static class NewVideoConfig {
        /**
         * 新视频判定天数
         */
        private Integer daysThreshold = 7;

        /**
         * 加权系数
         */
        private Double boost = 1.3;

        public Integer getDaysThreshold() {
            return daysThreshold;
        }

        public void setDaysThreshold(Integer daysThreshold) {
            this.daysThreshold = daysThreshold;
        }

        public Double getBoost() {
            return boost;
        }

        public void setBoost(Double boost) {
            this.boost = boost;
        }
    }

    /**
     * 指标权重配置
     */
    public static class WeightsConfig {
        private Double viewedCnt = 2.0;
        private Double likeCnt = 5.0;
        private Double commentCnt = 3.0;
        private Double authorFansCnt = 2.0;
        private Double completeRate = 4.0;
        private Double replayRate = 3.0;

        public Double getViewedCnt() {
            return viewedCnt;
        }

        public void setViewedCnt(Double viewedCnt) {
            this.viewedCnt = viewedCnt;
        }

        public Double getLikeCnt() {
            return likeCnt;
        }

        public void setLikeCnt(Double likeCnt) {
            this.likeCnt = likeCnt;
        }

        public Double getCommentCnt() {
            return commentCnt;
        }

        public void setCommentCnt(Double commentCnt) {
            this.commentCnt = commentCnt;
        }

        public Double getAuthorFansCnt() {
            return authorFansCnt;
        }

        public void setAuthorFansCnt(Double authorFansCnt) {
            this.authorFansCnt = authorFansCnt;
        }

        public Double getCompleteRate() {
            return completeRate;
        }

        public void setCompleteRate(Double completeRate) {
            this.completeRate = completeRate;
        }

        public Double getReplayRate() {
            return replayRate;
        }

        public void setReplayRate(Double replayRate) {
            this.replayRate = replayRate;
        }
    }
}