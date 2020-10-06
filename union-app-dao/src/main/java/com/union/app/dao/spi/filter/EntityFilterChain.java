package com.union.app.dao.spi.filter;

import com.union.app.plateform.log4j2.AppLoggerFactory;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表查询拦截器链
 * @author root
 */
public class EntityFilterChain
{
    private static final Logger logger = AppLoggerFactory.getJPQLLogger();


    private List<EntityFilter> entityFilterList = new ArrayList<>();

    private Map<String,Object> paramMaps = new ConcurrentHashMap<>();

    private Class<?> tClass;

    private PageLimitFilter pageLimitFilter;

    private OrderByFilter orderByFilter;

    private OrderByRandomFilter orderByRandomFilter;

    private String sqlUniqued;


    private EntityFilterChain(Class<?> tClass){
        sqlUniqued = UUID.randomUUID().toString();
        logger.debug("sqlUniqued|{}|ACTION|new Filter Chain" ,sqlUniqued);
        this.tClass = tClass;
    }

    public static <T> EntityFilterChain newFilterChain(Class<T> tClass)
    {
        return new EntityFilterChain(tClass);
    }

    public EntityFilterChain orFilter(){
        logger.debug("sqlUniqued|{}|ACTION|add OrFilter" ,sqlUniqued);
        this.entityFilterList.add(new OrFilter());return this;}

    public EntityFilterChain andFilter(){
        logger.debug("sqlUniqued|{}|add AndFilter" ,sqlUniqued);
        this.entityFilterList.add(new AndFilter());return this;}

    public EntityFilterChain compareFilter(String colum,CompareTag compareTag,Object value)
    {
        logger.debug("sqlUniqued|{}|ACTION|add CompareFilter|colum={}|symbol={}|value={}" ,sqlUniqued,colum,compareTag.getSymbol(),value);
        String paramName = getParamName(colum);
        paramMaps.put(paramName,value);
        this.entityFilterList.add(new CompareFilter(colum,compareTag,paramName));
        return this;
    }

    public EntityFilterChain nullFilter(String colum,boolean isNull)
    {
        logger.debug("sqlUniqued|{}|ACTION|add NullFilter|colum={}|isNull={}" ,sqlUniqued,colum,isNull);
        this.entityFilterList.add(new NullFilter(colum,isNull));
        return this;
    }

    public EntityFilterChain orderByFilter(String colum,OrderTag orderTag){
        logger.debug("sqlUniqued|{}|ACTION|add OrderByFilter|colum={}|orderby={}" ,sqlUniqued,colum,orderTag);
        this.orderByFilter = new OrderByFilter(colum,orderTag);
        return this;
    }

    public EntityFilterChain orderByRandomFilter(){
        logger.debug("sqlUniqued|{}|ACTION|add OrderByFilter|colum={}|orderby={}" ,sqlUniqued);
        this.orderByRandomFilter = new OrderByRandomFilter();
        return this;
    }

    /**
     * 页数从1开始
     * @param page
     * @param limit
     * @return
     */
    public EntityFilterChain pageLimitFilter(int page,int limit)
    {
        logger.debug("sqlUniqued|{}|ACTION|add PageLimitFilter|page={}|limit={}" ,sqlUniqued,page,limit);
        this.pageLimitFilter = new PageLimitFilter(page,limit);
        return this;
    }

    public EntityFilterChain inFilter(String colum,List<Object> values){
        logger.debug("sqlUniqued|{}|ACTION|add InFilter|colum={}|values={}" ,sqlUniqued,colum,values);
        String paramName = getParamName(colum);
        paramMaps.put(paramName,values);
        this.entityFilterList.add(new InFilter(colum,paramName));
        return this;
    }

    public EntityFilterChain betweenFilter(String colum,Object lowyer,Object higher){
        logger.debug("sqlUniqued|{}|ACTION|add BetweenFilter|colum={}|lowyer={}|higher={}" ,sqlUniqued,colum,lowyer,higher);
        String paramName1 =  getParamName(colum);
        String paramName2 =  getParamName(colum);
        paramMaps.put(paramName1,lowyer);
        paramMaps.put(paramName2,higher);
        this.entityFilterList.add(new BetweenFilter(colum,paramName1,paramName2));
        return this;
    }

    public EntityFilterChain likeFilter(String colum,String value){
        logger.debug("sqlUniqued|{}|ACTION|add BetweenFilter|colum={}|value={}" ,sqlUniqued,colum,value);
        String paramName = getParamName(colum);
        paramMaps.put(paramName,value);
        this.entityFilterList.add(new LikeFilter(colum,paramName));
        return this;
    }

    public PageLimitFilter getPageLimitFilter() {
        return pageLimitFilter;
    }



    public String getQuery()
    {

        StringBuffer stringBuffer = new StringBuffer();
        String entityName = this.tClass.getSimpleName();
        stringBuffer.append("select p from ");
        stringBuffer.append(entityName);
        stringBuffer.append(" p ");
        if(!this.entityFilterList.isEmpty()) {
            stringBuffer.append(" where ");
            for (EntityFilter entityFilter : this.entityFilterList) {
                stringBuffer.append(entityFilter.getSql());
            }
        }
        if(this.orderByFilter != null){stringBuffer.append(orderByFilter.getSql());}
        if(this.orderByRandomFilter != null){stringBuffer.append(orderByRandomFilter.getSql());}
        logger.debug("sqlUniqued|{}|ACTION|get JPQL|JPQL={}" ,sqlUniqued,stringBuffer.toString());
        return stringBuffer.toString();

    }

    public Map<String,Object> getParamMap() {
        return this.paramMaps;
    }

    public String getSqlUniqued() {
        return sqlUniqued;
    }

    private String getParamName(String colum)
    {
        return (colum + UUID.randomUUID().getMostSignificantBits()).replace("-","A");
    }




}
