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
        public static final String X_PENGUIN_SET_PENGUIN_ID = "X-Penguin-Set-PenguinID";
        public static final String X_PENGUIN_VARIANT = "X-Penguin-Variant";
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
        public static final String AES_IV = "aes_iv";
        public static final String RECOGNITION_BATCH_MAX = "recognition_batch_max";
        public static final String OUTLIER_IMG_BUCKET = "outlier_img_bucket";
        public static final String OUTLIER_IMG_SAVE_KEY = "outlier_img_save_key";
        public static final String OUTLIER_IMG_USERNAME = "outlier_img_username";
        public static final String OUTLIER_IMG_PASSWORD = "outlier_img_password";
        public static final String OUTLIER_IMG_EXPIRATION = "outlier_img_expiration";
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

    public static class DefaultValue {
        public static final int ADVANCED_QUERY_REQUEST_NUM_MAX = 5;
        public static final long DEFAULT_GLOBAL_TREND_INTERVAL = 86400000L;
        public static final long DEFAULT_GLOBAL_TREND_RANGE = 5184000000L;
        public static final int MAX_SECTION_NUM = 180;
        public static final int PAST_GLOBAL_MATRIX_QUERY_TIMEOUT = 3;
        public static final int CURRENT_GLOBAL_MATRIX_QUERY_TIMEOUT = 3;
        public static final int GLOBAL_TREND_QUERY_TIMEOUT = 1;
        public static final int GLOBAL_PATTERN_QUERY_TIMEOUT = 3;
        public static final int ADVANCED_QUERY_TIMEOUT = 2;
        public static final int RECOGNITION_BATCH_MAX = 50;
        public static final int OUTLIER_IMG_EXPIRATION = 30;
        public static final long SCREENSHOT_REPORT_TIMESTAMP_THRESHOLD = 1800000L;
        public static final int USER_ID_COOKIE_EXPIRY = 60 * 60 * 24 * 365 * 10;
    }

    public static class OutlierUpYunSignature {
        public static final String METHOD_POST = "POST";
    }

    public static class UserTag {
        public static final String TESTER = "tester";
        public static final String BANNED = "banned";
    }

    public static class CacheValue {
        public static final String USERS = "users";
        public static final String DROP_MATRIX = "drop_matrix";
        public static final String SEGMENTED_DROP_MATRIX = "segmented_drop_matrix";
        public static final String PATTERN_MATRIX = "pattern_matrix";
        public static final String DROP_INFO_LIST = "drop_info_list";
        public static final String DROP_SET = "drop_set";
        public static final String LATEST_DROP_INFO_MAP = "latest_drop_info_map";
        public static final String LATEST_MAX_ACCUMULATABLE_TIME_RANGE_MAP = "latest_max_accumulatable_time_range_map";
        public static final String LATEST_TIME_RANGE_MAP = "latest_time_range_map";
        public static final String TOTAL_STAGE_TIMES_MAP = "total_stage_times_map";
        public static final String TOTAL_ITEM_QUANTITIES_MAP = "total_item_quantities_map";
        public static final String LISTS = "lists";
        public static final String MAPS = "maps";
    }

    public static class Auth {
        public static final String AUTHORIZATION_REALM_PENGUIN_ID = "PenguinID";
    }

}
