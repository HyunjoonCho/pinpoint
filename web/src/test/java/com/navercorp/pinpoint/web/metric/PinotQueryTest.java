package com.navercorp.pinpoint.web.metric;

import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.Request;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.junit.Test;

public class PinotQueryTest {
    @Test
    public void simplePinotQueryTest() {
        String zkURL = "10.113.84.89:2191";
        String pinotClusterName = "PinotCluster";
        Connection pinotConnection = ConnectionFactory.fromZookeeper(zkURL + "/" + pinotClusterName);

        String query = "SELECT COUNT(*) FROM systemMetric WHERE metricName='cpu'";

        Request pinotClientRequest = new Request("sql", query);
        ResultSetGroup pinotResultSetGroup = pinotConnection.execute(pinotClientRequest);
        ResultSet resultTableResultSet = pinotResultSetGroup.getResultSet(0);

//        int numRows = resultTableResultSet.getRowCount();
//        int numColumns = resultTableResultSet.getColumnCount();
        String columnValue = resultTableResultSet.getString(0, 0);
        String columnName = resultTableResultSet.getColumnName(0);

        System.out.println("ColumnName: " + columnName + ", ColumnValue: " + columnValue);
    }
}
