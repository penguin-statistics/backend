package io.penguinstats.constant;

public class Constant {

	public static final Long[] ADD_TIME_POINTS =
			new Long[] {0L, 1558989300000L, 1560045456000L, 1577145600000L, 1581105600000L};

	public static final String API_V2 = "/api/v2";

	public static class LastUpdateMapKeyName {
		public static final String MATRIX_RESULT = "matrix_result";
		public static final String TREND_RESULT = "trend_result";
		public static final String ITEM_LIST = "item_list";
		public static final String ZONE_LIST = "zone_list";
		public static final String STAGE_LIST = "stage_list";
		public static final String DROP_INFO_LIST = "drop_info_list";
		public static final String NOTICE_LIST = "notice_list";
	}

	public static class CustomHeader {
		public static final String X_PENGUIN_UPGRAGE = "X-Penguin-Upgrade";
	}

	public static class SystemPropertyKey {
		public static final String ADVANCED_QUERY_REQUEST_NUM_MAX = "advanced_query_request_num_max";
		public static final String DEFAULT_GLOBAL_TREND_INTERVAL = "default_global_trend_interval";
		public static final String DEFAULT_GLOBAL_TREND_RANGE = "default_global_trend_range";
		public static final String MAX_SECTION_NUM = "max_section_num";
		public static final String GLOBAL_MATRIX_QUERY_TIMEOUT = "global_matrix_query_timeout";
		public static final String GLOBAL_TREND_QUERY_TIMEOUT = "global_trend_query_timeout";
		public static final String ADVANCED_QUERY_TIMEOUT = "global_advanced_query_timeout";
	}

}
