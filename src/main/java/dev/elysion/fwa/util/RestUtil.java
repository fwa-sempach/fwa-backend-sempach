package dev.elysion.fwa.util;

public class RestUtil {

	public static final int DEFAULT_PAGE = 1;
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final String DEFAULT_ORDER = "asc";

	public static final String PAGE_PARAMETER = "_page";
	public static final String SIZE_PARAMETER = "_size";
	public static final String SORT_PARAMETER = "_sort";
	public static final String ORDER_PARAMETER = "_order";

	private RestUtil() {
		//private constructor to hide the public implicit one
	}

	/**
	 * @param sortOrder
	 * @return true if "asc" (or any other value), false if "desc"
	 */
	public static boolean determineSortOrder(String sortOrder) {
		return sortOrder.equals("desc") ? false : true;
	}

	/**
	 * This is used to convert from 1 based pages (REST) to 0 based pages (JPA)
	 *
	 * @param page
	 * @return page -1, minimum 0.
	 */
	public static int sanitizePageNumber(int page) {
		return Math.max(page - 1, 0);
	}
}
