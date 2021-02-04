package com.union.app.common.dao;

import com.union.app.dao.jpa.JpaTransactionUtil;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.PageLimitFilter;
import com.union.app.plateform.log4j2.AppLoggerFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

@Component
public class AppDaoService
{

    private static final Logger logger = AppLoggerFactory.getJPQLLogger();

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JpaTransactionUtil jpaTransactionUtil;


    @Autowired
    PkCacheService pkCacheService;

    /**
     * 查询单个实体
     * @param tClass
     * @param entityFilterChain
     * @param <T>
     * @return
     */
    public <T> T querySingleEntity(Class<T> tClass,EntityFilterChain entityFilterChain){




        List<T> entities;
        if(entityFilterChain == null){
            entities = entityManager.createQuery("select p from " + tClass.getSimpleName() + " p",tClass).getResultList();
        }
        else
        {
            String sqlUniqued = entityFilterChain.getSqlUniqued();
            logger.info("sqlUniqued|{}|ACTION|querySingleEntity" ,sqlUniqued);
            TypedQuery<T> query = entityManager.createQuery(entityFilterChain.getQuery(),tClass);
            Map<String,Object> paramMaps = entityFilterChain.getParamMap();
            for(Map.Entry<String,Object> entry:paramMaps.entrySet())
            {
                logger.debug("sqlUniqued|{}|ACTION|SET-PARAM|paramName={}|paramValue={}" ,sqlUniqued,entry.getKey(),entry.getValue());
                query.setParameter(entry.getKey(),entry.getValue());

            }
            entities = query.getResultList();
        }

        if(entities == null || entities.isEmpty()){return null;}
        T t = entities.get(0);


        return t;
    }

    /**
     * 查询实体
     * @param tClass
     * @param entityFilterChain
     * @param <T>
     * @return
     */
    public <T> List<T> queryEntities(Class<T> tClass, EntityFilterChain entityFilterChain){
        if(entityFilterChain == null){
            return entityManager.createQuery("select p from " + tClass.getSimpleName() + " p",tClass).getResultList();
        }
        String sqlUniqued = entityFilterChain.getSqlUniqued();
        logger.info("sqlUniqued|{}|ACTION|queryEntities" ,sqlUniqued);

        TypedQuery<T> query = entityManager.createQuery(entityFilterChain.getQuery(),tClass);
        Map<String,Object> paramMaps = entityFilterChain.getParamMap();
        for(Map.Entry<String,Object> entry:paramMaps.entrySet())
        {
            logger.debug("sqlUniqued|{}|ACTION|SET-PARAM|paramName={}|paramValue={}" ,sqlUniqued,entry.getKey(),entry.getValue());
            query.setParameter(entry.getKey(),entry.getValue());
        }
        PageLimitFilter pageLimitFilter = entityFilterChain.getPageLimitFilter();
        if(pageLimitFilter == null)
        {
            return query.getResultList();
        }
        else
        {
            return query.setFirstResult((pageLimitFilter.getPage()-1) * pageLimitFilter.getLimit()).setMaxResults(pageLimitFilter.getLimit()).getResultList();
        }

    }

    /**
     * 插入实体
     * @param t
     * @param <T>
     */
    public <T> void insertEntity(T t){
        entityManager.persist(t);
    }

    /**
     * 修改实体
     * @param t
     * @param <T>
     */
    public <T> void updateEntity(T t){

        entityManager.merge(t);

    }

    /**
     * 删除实体
     * @param t
     * @param <T>
     */
    public <T> void deleteEntity(T t){

        entityManager.remove(t);

    }

    /**
     * 创建会话
     * @return
     */
    public TransactionStatus newTransaction(){return jpaTransactionUtil.newTransaction();}

    /**
     * 提交会话
     * @param transactionStatus
     */
    public void commit(TransactionStatus transactionStatus){ jpaTransactionUtil.commit(transactionStatus);}

    /**
     * 回滚会话
     * @param transactionStatus
     */
    public void rollback(TransactionStatus transactionStatus){jpaTransactionUtil.rollback(transactionStatus);}


    public <T> void updateColumById(Class<T> tClass, String idName,Object idValue, Map<String,Object> columValues) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("update ");
        stringBuffer.append(tClass.getSimpleName());
        stringBuffer.append(" p set ");
        for(Map.Entry<String,Object> entry:columValues.entrySet())
        {
            stringBuffer.append("p."+entry.getKey()+"=:"+entry.getKey()+"columValue,");
        }
        stringBuffer = new StringBuffer(stringBuffer.substring(0,stringBuffer.toString().length()-1));

        stringBuffer.append(" where p." + idName);
        stringBuffer.append("=:idValue");
        Query query = entityManager.createQuery(stringBuffer.toString());
        for(Map.Entry<String,Object> entry:columValues.entrySet())
        {
            query.setParameter(entry.getKey()+"columValue", entry.getValue());
        }

        query.setParameter("idValue", idValue);
        query.executeUpdate();
    }

}
