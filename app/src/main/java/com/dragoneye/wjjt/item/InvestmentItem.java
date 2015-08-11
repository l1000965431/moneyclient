package com.dragoneye.wjjt.item;

/**
 * Created by happysky on 15-6-24.
 */
public class InvestmentItem extends BaseListItem {
    public InvestmentItem(int itemType, Object itemContent) {
        super(itemType, itemContent);
    }

    public static final int TYPE_INVESTMENT_ITEM = 0;
    public static final int TYPE_INVESTMENT_LOADING = 1;
    public static final int TYPE_INVESTMENT_NOT_ANY_MORE = 2;

    public static final int TYPE_INVESTMENT_COUNT = 3;
}
