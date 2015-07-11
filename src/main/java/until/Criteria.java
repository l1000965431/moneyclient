package until;

import java.util.List;

/**
 * 条件查询类
 */
public interface Criteria {
    List<String> getFields();

    void setFields(List<String> fields);

    List<Object> getCondition();

    void setCondition(List<Object> condition);

    int getStart();

    void setStart(int start);

    int getFetchSize();

    void setFetchSize(int fetchSize);

    List<String> getOrder();

    void setOrder(List<String> order);

    int getPageNo();

    void setPageNo(int pageNo);

    String toString();

    String getFieldClause();

    String getOrderClause();

    String getWhereClause();

    /**
     * 添加条件
     *
     * @param position  添加的位置
     * @param condition
     */
    void addCondition(int position, Condition condition);

    void addOrder(int index, String order);
}
