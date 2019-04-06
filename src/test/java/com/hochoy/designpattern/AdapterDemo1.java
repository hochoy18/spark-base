package com.hochoy.designpattern;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/4
 */
public class AdapterDemo1 {
}

//target mysql_sql
interface MySQL_SQL{
    String ddl();
    String  dml();
}
//oracle_sql
class Oracle_SQL{
    private String ddl;
    private String dml;

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public String getDml() {
        return dml;
    }

    public void setDml(String dml) {
        this.dml = dml;
    }
}
class adapter implements MySQL_SQL{
    Oracle_SQL oracleSql;

    public adapter(Oracle_SQL oracleSql) {
        this.oracleSql = oracleSql;
    }

    @Override
    public String ddl() {
        return oracleSql.getDdl().toUpperCase().replaceAll("VARCHAR2","VARCHAR");
    }

    @Override
    public String dml() {
        return oracleSql.getDml().toUpperCase();

    }
}