package com.striveonger.app.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.striveonger.app.config.VlogHotConfig;
import com.striveonger.common.core.Jackson;
import com.striveonger.common.core.constant.ResultStatus;
import com.striveonger.common.core.exception.OwnException;
import com.striveonger.common.third.cache.reids.RedisKit;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频池服务
 *
 * @author Mr.Lee
 * @since 2026-05-01 11:21
 */
public abstract class VlogPoolService {
    private final Logger log = LoggerFactory.getLogger(VlogPoolService.class);

    /**
     * 视频池时间线
     * 视频按上传时间排序, 最新视频在顶部
     */
    protected static final String VLOG_POOL_TIMELINE = "vlog:pool:timeline";

    /**
     * 视频池热门
     * 视频按热度排序, 最热门视频在顶部
     */
    protected static final String VLOG_POOL_HOT = "vlog:pool:hot";

    /**
     * 视频池新手
     * 视频按上传时间排序, 最新视频在顶部
     */
    protected static final String VLOG_POOL_NEW = "vlog:pool:new";

    /**
     * 新视频时间阈值
     * 视频上传时间在该时间范围内, 被认为是新视频
     */
    private static final long NEW_VLOG_HOURS = 24L;

    /**
     * 默认分页大小
     * 用于获取用户没看过的视频ID列表时, 默认返回10个视频
     */
    protected static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 热度计算配置
     */
    @Resource
    protected VlogHotConfig vlogHotConfig;

    /**
     * 加载视频池数据
     */
    public void loadVlogPoolData() {
        log.info("load vlog pool data");
        List<Vlog> vlogs = getVlogPoolData();
        log.info("vlog pool data size: {}", vlogs.size());
        vlogs.forEach(this::updateVlogScore);
        log.info("vlog pool data loaded");
    }

    /**
     * 更新视频分数
     * 根据视频的各项指标计算热度分数, 并同步到时间线池和热门池
     * 如果是24小时内的新视频, 还会加入新手池
     *
     * @param vlog 视频对象
     */
    public void updateVlogScore(Vlog vlog) {
        // log.info("update vlog source: {}", vlog.getId());
        double score = calculateScore(vlog);
        // 毫秒级
        long uploadTimestamp = vlog.getUploadTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
        RedisKit.ZSet.add(VLOG_POOL_TIMELINE, vlog.getId(), uploadTimestamp);
        if (score > vlogHotConfig.getThreshold()) {
            RedisKit.ZSet.add(VLOG_POOL_HOT, vlog.getId(), score);
        } else {
            RedisKit.ZSet.remove(VLOG_POOL_HOT, vlog.getId());
        }

        if (isNewVlog(vlog.getUploadTime())) {
            RedisKit.ZSet.add(VLOG_POOL_NEW, vlog.getId(), uploadTimestamp);
        } else {
            RedisKit.ZSet.remove(VLOG_POOL_NEW, vlog.getId());
        }
    }

    /**
     * 从视频池中获取用户没看过的视频ID列表
     *
     * @param userId 用户ID
     * @return 视频ID列表
     */
    public List<String> list(String userId) {
        return list(userId, DEFAULT_PAGE_SIZE);
    }

    public abstract List<String> list(String userId, int count);

    /**
     * 获取视频池数据
     * 模拟从数据库获取视频列表, 真实场景下应分页查询
     *
     * @return 视频列表
     */
    private List<Vlog> getVlogPoolData() {
        // 真实情况下, 要分页加载的, 这里为了演示, 我就直接模拟数据了.
        List<Vlog> list = new ArrayList<>();
        // 读取classpath:data/vlogs.json文件内容
        ClassPathResource resource = new ClassPathResource("data/vlogs.json");
        try {
            ArrayNode nodes = Jackson.toArrayNode(resource.getInputStream());
            nodes.forEach(node -> list.add(Jackson.convert(node, VlogPoolService.Vlog.class)));
        } catch (IOException e) {
            log.error("load vlog pool data error...", e);
            throw new OwnException(ResultStatus.ACCIDENT, "load vlog pool data error...");
        }
        return list;
    }

    /**
     * 计算视频得分
     *
     * @param vlog 视频
     * @return 得分
     */
    private double calculateScore(Vlog vlog) {
        VlogHotConfig.WeightsConfig w = vlogHotConfig.getWeights();
        VlogHotConfig.DecayConfig d = vlogHotConfig.getDecay();
        VlogHotConfig.NewVideoConfig n = vlogHotConfig.getNewVideo();

        // 1. 计算基础分
        double baseScore = 0;
        baseScore += vlog.getViewedCnt() * w.getViewedCnt();
        baseScore += vlog.getLikeCnt() * w.getLikeCnt();
        baseScore += vlog.getCommentCnt() * w.getCommentCnt();
        baseScore += vlog.getAuthorFansCnt() * w.getAuthorFansCnt();
        baseScore += vlog.getCompleteRate() * w.getCompleteRate();
        baseScore += vlog.getReplayRate() * w.getReplayRate();

        // 2. 计算时间加权
        double timeWeight = calculateTimeWeight(vlog.getUploadTime(), d, n);

        // 3. 最终得分
        return Math.floor(baseScore * timeWeight);
    }

    /**
     * 计算时间加权系数
     * - 7天内新视频: 1.3x boost
     * - 7天以上: 指数衰减, 最低 0.3
     */
    private double calculateTimeWeight(LocalDateTime uploadTime, VlogHotConfig.DecayConfig decay, VlogHotConfig.NewVideoConfig newVideo) {
        long ageInMillis = Duration.between(uploadTime, LocalDateTime.now()).toMillis();
        double ageInDays = ageInMillis / (1000.0 * 60 * 60 * 24);

        if (ageInDays <= newVideo.getDaysThreshold()) {
            // 新视频加权
            return newVideo.getBoost();
        } else {
            // 指数衰减
            double decayFactor = Math.pow(decay.getBase(), ageInDays / decay.getHalfLifeDays());
            return Math.max(decay.getMinFactor(), decayFactor);
        }
    }

    /**
     * 判断视频是否为新视频(上传时间在24小时内)
     *
     * @param uploadTime 视频上传时间
     * @return true表示是新视频
     */
    private boolean isNewVlog(LocalDateTime uploadTime) {
        return uploadTime.isAfter(LocalDateTime.now().minusHours(NEW_VLOG_HOURS));
    }

    /**
     * 视频实体
     */
    public static class Vlog {
        private String id;
        /**
         * 视频浏览数
         */
        private Long viewedCnt;
        /**
         * 视频获赞数
         */
        private Long likeCnt;
        /**
         * 视频评论数
         */
        private Long commentCnt;
        /**
         * 视频作者粉丝数
         */
        private Long authorFansCnt;
        /**
         * 视频上传时间
         */
        private LocalDateTime uploadTime;
        /**
         * 视频完播率
         */
        private Double completeRate;
        /**
         * 视频复播率
         */
        private Double replayRate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getViewedCnt() {
            return viewedCnt;
        }

        public void setViewedCnt(Long viewedCnt) {
            this.viewedCnt = viewedCnt;
        }

        public Long getLikeCnt() {
            return likeCnt;
        }

        public void setLikeCnt(Long likeCnt) {
            this.likeCnt = likeCnt;
        }

        public Long getCommentCnt() {
            return commentCnt;
        }

        public void setCommentCnt(Long commentCnt) {
            this.commentCnt = commentCnt;
        }

        public Long getAuthorFansCnt() {
            return authorFansCnt;
        }

        public void setAuthorFansCnt(Long authorFansCnt) {
            this.authorFansCnt = authorFansCnt;
        }

        public LocalDateTime getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(LocalDateTime uploadTime) {
            this.uploadTime = uploadTime;
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
