package org.jerry.frameworks.base.repository.hibernate.type;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * <p>Date : 16/5/13</p>
 * <p>Time : 上午8:41</p>
 *
 * @author jerry
 */
public class CollectionToStringUserType implements UserType, ParameterizedType, Serializable {

    /**
     * 默认分隔符','
     */
    private String separator;
    /**
     * 默认 java.lang.Long
     */
    private Class elementType;
    /**
     * 默认 ArrayList
     */
    private Class collectionType;

    @Override
    public void setParameterValues(Properties properties) {
        String separator = (String) properties.get("separator");
        if (!StringUtils.isEmpty(separator)) {
            this.separator = separator;
        } else {
            this.separator = ",";
        }

        String collectionType = (String) properties.get("collectionType");
        if (!StringUtils.isEmpty(collectionType)) {
            try {
                this.collectionType = Class.forName(collectionType);
            } catch (ClassNotFoundException e) {
                throw new HibernateException(e);
            }
        } else {
            this.collectionType = ArrayList.class;
        }

        String elementType = (String) properties.get("elementType");
        if (!StringUtils.isEmpty(elementType)) {
            try {
                this.elementType = Class.forName(elementType);
            } catch (ClassNotFoundException e) {
                throw new HibernateException(e);
            }
        } else {
            this.elementType = Long.TYPE;
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class returnedClass() {
        return collectionType;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        return o == o1 || !(o == null || o1 == null) && o.equals(o1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    /**
     * 从JDBC ResultSet读取数据,将其转换为自定义类型后返回
     *
     * @param resultSet             取数结果集
     * @param names                 包含了当前自定义类型的映射字段名称
     * @param sessionImplementor    无用
     * @param o                     无用
     * @return                      一个分隔后的自定义类型数据集合
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        String valueStr = resultSet.getString(names[0]);
        if (StringUtils.isEmpty(valueStr)) {
            return newCollection();
        }
        String[] values = StringUtils.split(valueStr, separator);

        Collection result = newCollection();

        for (String value :values) {
            if (!StringUtils.isEmpty(value)) {
                result.add(ConvertUtils.convert(value, elementType));
            }
        }
        return result;
    }

    private Collection newCollection() {
        try {
            return (Collection) collectionType.newInstance();
        } catch (Exception e) {
            throw new HibernateException(e);
        }
    }

    /**
     *
     * 本方法将在Hibernate进行数据保存时被调用.<p/>
     * 我们可以通过PreparedStateme将自定义数据写入到对应的数据库表字段.
     *
     * @param preparedStatement             ps
     * @param o                             value
     * @param i                             index
     * @param sessionImplementor            session
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        String valueStr;

        if (o == null)
            valueStr = "";
        else
            valueStr = StringUtils.join((Collection) o, separator);
        if (StringUtils.isNoneEmpty(valueStr)) {
            valueStr = valueStr + ",";
        }
        preparedStatement.setString(i, valueStr);
    }

    /**
     * 提供自定义类型的完全复制方法.<p/>本方法将用构造返回对象
     * 当nullSafeGet方法调用之后，我们获得了自定义数据对象，在向用户返回自定义数据之前，
     * deepCopy方法将被调用，它将根据自定义数据对象构造一个完全拷贝，并将此拷贝返回给用户
     * 此时我们就得到了自定义数据对象的两个版本，第一个是从数据库读出的原始版本，其二是我们通过
     * deepCopy方法构造的复制版本，原始的版本将有Hibernate维护，复制版由用户使用。原始版本用作
     * 稍后的脏数据检查依据；Hibernate将在脏数据检查过程中将两个版本的数据进行对比（通过调用
     * equals方法），如果数据发生了变化（equals方法返回false），则执行对应的持久化操作.
     *
     * @param o     要复制的实体对象
     * @return      复制版本的实体对象
     * @throws HibernateException
     */
    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null) return null;
        Collection copyCollection = newCollection();
        copyCollection.addAll(newCollection());
        return copyCollection;
    }

    /**
     * 本类型实例是否可变
     *
     * @return 是|否
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * 序列化
     *
     * @param o     对象jo
     * @return      实体po
     * @throws HibernateException
     */
    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return ((Serializable) o);
    }

    /**
     * 反序列化
     *
     * @param serializable  cache
     * @param o             po
     * @return              实体反序列化
     * @throws HibernateException
     */
    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return serializable;
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return o;
    }
}
