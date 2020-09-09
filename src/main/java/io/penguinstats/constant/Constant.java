package io.penguinstats.constant;

public class Constant {

	public static final Long[] ADD_TIME_POINTS =
			new Long[] {0L, 1558989300000L, 1560045456000L, 1577145600000L, 1581105600000L};

	public static final String API_V2 = "/api/v2";

	public static class LastUpdateMapKeyName {
		public static final String PAST_MATRIX_RESULT = "past_matrix_result";
		public static final String CURRENT_MATRIX_RESULT = "current_matrix_result";
		public static final String TREND_RESULT = "trend_result";
		public static final String ITEM_LIST = "item_list";
		public static final String ZONE_LIST = "zone_list";
		public static final String STAGE_LIST = "stage_list";
		public static final String DROP_INFO_LIST = "drop_info_list";
		public static final String NOTICE_LIST = "notice_list";
		public static final String EVENT_PERIOD_LIST = "event_period_list";
		public static final String TOTAL_STAGE_TIMES_MAP = "total_stage_times_map";
		public static final String TOTAL_ITEM_QUANTITIES_MAP = "total_item_quantities_map";
	}

	public static class CustomHeader {
		public static final String X_PENGUIN_UPGRAGE = "X-Penguin-Upgrade";
	}

	public static class SystemPropertyKey {
		public static final String ADVANCED_QUERY_REQUEST_NUM_MAX = "advanced_query_request_num_max";
		public static final String DEFAULT_GLOBAL_TREND_INTERVAL = "default_global_trend_interval";
		public static final String DEFAULT_GLOBAL_TREND_RANGE = "default_global_trend_range";
		public static final String MAX_SECTION_NUM = "max_section_num";
		public static final String PAST_GLOBAL_MATRIX_QUERY_TIMEOUT = "past_global_matrix_query_timeout";
		public static final String CURRENT_GLOBAL_MATRIX_QUERY_TIMEOUT = "current_global_matrix_query_timeout";
		public static final String GLOBAL_TREND_QUERY_TIMEOUT = "global_trend_query_timeout";
		public static final String ADVANCED_QUERY_TIMEOUT = "global_advanced_query_timeout";
	}

	public static class CacheName {
		public static final String NO_EXPIRY_MAP = "no-expiry-map";
	}

	public static class CacheKeyPrefix {
		public static final String TOTAL_STAGE_TIMES = "total-stage-times";
		public static final String TOTAL_ITEM_QUANTITIES = "total-item-quantities";
	}

}
