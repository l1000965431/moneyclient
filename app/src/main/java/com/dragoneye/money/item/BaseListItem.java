package com.dragoneye.money.item;

/**
 * Created by happysky on 15-4-21.
 * list view 数据项
 */
public class BaseListItem {
    private int itemType;
    private Object itemContent;

    public BaseListItem(int itemType, Object itemContent) {
        this.itemType = itemType;
        this.itemContent = itemContent;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Object getItemContent() {
        return itemContent;
    }

    public void setItemContent(Object itemContent) {
        this.itemContent = itemContent;
    }
}
