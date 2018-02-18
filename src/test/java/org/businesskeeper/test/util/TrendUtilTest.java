package org.businesskeeper.test.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TrendUtilTest {
	public static double[] ASCENDING_TREND = {0.1, 0.2, 0.3, 0.4, 0.5};
	public static double[] ASCENDING_TREND_WITH_EQUAL_VALUES = {0.1, 0.1, 0.3, 0.4, 0.5};
	public static double[] DESCENDING_TREND = {0.5, 0.4, 0.3, 0.2, 0.1};
	public static double[] DESCENDING_TREND_WITH_EQUAL_VALUES = {0.5, 0.4, 0.4, 0.2, 0.1};
	public static double[] CONSTANT_TREND = {0.1, 0.1, 0.1, 0.1, 0.1};
	public static double[] UNDEFINED_TREND = {0.1, 0.2, 0.1, 0.5, 0.4};
	
	@Test
	public void testIsAscendingTrend() {
		assertTrue(TrendUtil.isAscendingTrend(ASCENDING_TREND));
	}
	
	@Test
	public void testIsDescendingTrend() {
		assertTrue(TrendUtil.isDescendingTrend(DESCENDING_TREND));
	}
	
	@Test
	public void testIsConstantTrend() {
		assertTrue(TrendUtil.isConstantTrend(CONSTANT_TREND));
	}
	
	@Test
	public void testIsUndefinedTrend() {
		assertFalse(TrendUtil.isAscendingTrend(UNDEFINED_TREND));
		assertFalse(TrendUtil.isDescendingTrend(UNDEFINED_TREND));
		assertFalse(TrendUtil.isConstantTrend(UNDEFINED_TREND));
	}
	
	@Test
	public void testIsUndefinedTrendWithEqualValues() {
		assertFalse(TrendUtil.isAscendingTrend(ASCENDING_TREND_WITH_EQUAL_VALUES));
		assertFalse(TrendUtil.isDescendingTrend(ASCENDING_TREND_WITH_EQUAL_VALUES));
		assertFalse(TrendUtil.isConstantTrend(ASCENDING_TREND_WITH_EQUAL_VALUES));
		
		assertFalse(TrendUtil.isAscendingTrend(DESCENDING_TREND_WITH_EQUAL_VALUES));
		assertFalse(TrendUtil.isDescendingTrend(DESCENDING_TREND_WITH_EQUAL_VALUES));
		assertFalse(TrendUtil.isConstantTrend(DESCENDING_TREND_WITH_EQUAL_VALUES));
	}
}
