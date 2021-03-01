package io.penguinstats.constant;

public class Constant {

    public static final String API_V2 = "/api/v2";

    public static class LastUpdateMapKeyName {
        public static final String ITEM_LIST = "item_list";
        public static final String ZONE_LIST = "zone_list";
        public static final String STAGE_LIST = "stage_list";
        public static final String DROP_INFO_LIST = "drop_info_list";
        public static final String NOTICE_LIST = "notice_list";
        public static final String EVENT_PERIOD_LIST = "event_period_list";
        public static final String TOTAL_STAGE_TIMES_MAP = "total_stage_times_map";
        public static final String TOTAL_ITEM_QUANTITIES_MAP = "total_item_quantities_map";
        public static final String FRONTEND_CONFIG_MAP = "frontend_config_map";
    }

    public static class CustomHeader {
        public static final String X_PENGUIN_UPGRAGE = "X-Penguin-Upgrade";
        public static final String X_PENGUIN_COMPATIBLE = "X-Penguin-Compatible";
    }

    public static class SystemPropertyKey {
        public static final String ADVANCED_QUERY_REQUEST_NUM_MAX = "advanced_query_request_num_max";
        public static final String DEFAULT_GLOBAL_TREND_INTERVAL = "default_global_trend_interval";
        public static final String DEFAULT_GLOBAL_TREND_RANGE = "default_global_trend_range";
        public static final String MAX_SECTION_NUM = "max_section_num";
        public static final String PAST_GLOBAL_MATRIX_QUERY_TIMEOUT = "past_global_matrix_query_timeout";
        public static final String CURRENT_GLOBAL_MATRIX_QUERY_TIMEOUT = "current_global_matrix_query_timeout";
        public static final String GLOBAL_TREND_QUERY_TIMEOUT = "global_trend_query_timeout";
        public static final String GLOBAL_PATTERN_QUERY_TIMEOUT = "global_pattern_query_timeout";
        public static final String ADVANCED_QUERY_TIMEOUT = "global_advanced_query_timeout";
        public static final String RECOGNITION_PUBLIC_KEY = "recognition_public_key";
        public static final String RECOGNITION_PRIVATE_KEY = "recognition_private_key";
        public static final String RECOGNITION_BATCH_MAX = "recognition_batch_max";
    }

    public static class CacheName {
        public static final String NO_EXPIRY_MAP = "no-expiry-map";
    }

    public static class CacheKeyPrefix {
        public static final String TOTAL_STAGE_TIMES = "total-stage-times";
        public static final String TOTAL_ITEM_QUANTITIES = "total-item-quantities";
    }

    public static class SiteURL {
        public static final String PENGUIN_STATS_CN = "https://penguin-stats.cn/";
        public static final String PENGUIN_STATS_IO = "https://penguin-stats.io/";
        public static final String EXUSI_AI = "https://exusi.ai/";
    }

    public static class CompatibleVersion {
        public static final String FRONTEND_V2_3_4_0 = "frontend-v2@v3.4.0";
    }

}
