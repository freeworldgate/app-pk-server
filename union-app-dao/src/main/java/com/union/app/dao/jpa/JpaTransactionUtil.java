package com.union.app.dao.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * @author root
 */
@Component
public class JpaTransactionUtil {


    @Autowired
    PlatformTransactionManager transactionManager;



    public TransactionStatus newTransaction(){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        return status ;
    }

    public void commit(TransactionStatus status) {
        if ( status != null) {
            transactionManager.commit(status);
        }
    }

    public void rollback(TransactionStatus status) {
        if( status != null) {
            transactionManager.rollback(status);
        }
    }



    @Bean
    public JpaTransactionManager transactionManager()
    {
        return new JpaTransactionManager();
    }



}