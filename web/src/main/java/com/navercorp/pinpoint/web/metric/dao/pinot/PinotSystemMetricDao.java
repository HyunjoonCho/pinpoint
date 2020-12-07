package com.navercorp.pinpoint.web.metric.dao.pinot;

import com.navercorp.pinpoint.common.server.metric.bo.SystemMetricBo;
import com.navercorp.pinpoint.common.server.metric.bo.TagBo;
import com.navercorp.pinpoint.web.metric.dao.SystemMetricDao;
import com.navercorp.pinpoint.web.metric.util.QueryStatementWriter;
import com.navercorp.pinpoint.web.metric.util.pinot.PinotResultSetProcessor;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.Request;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class PinotSystemMetricDao implements SystemMetricDao {
    private final String zkURL;
    private final String pinotClusterName;
    private final Connection pinotConnection;
    private final QueryStatementWriter queryStatementWriter;
    private final PinotResultSetProcessor pinotResultSetProcessor;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PinotSystemMetricDao(String zkURL,
                                String pinotClusterName,
                                QueryStatementWriter queryStatementWriter,
                                PinotResultSetProcessor pinotResultSetProcessor) {
        this.zkURL = Objects.requireNonNull(zkURL);
        this.pinotClusterName = Objects.requireNonNull(pinotClusterName);
        pinotConnection = ConnectionFactory.fromZookeeper(zkURL + "/" + pinotClusterName);
        this.queryStatementWriter = Objects.requireNonNull(queryStatementWriter, "queryStatementWriter");
        this.pinotResultSetProcessor = Objects.requireNonNull(pinotResultSetProcessor, "pinotResultSetProcessor");
    }

    @Override
    public List<String> getMetricNameList(String applicationName) {
        String query = queryStatementWriter.queryForMetricNameList(applicationName);
        ResultSet resultSet = queryAndGetResultSet(query);
        return pinotResultSetProcessor.processStringList(resultSet);
    }

    @Override
    public List<String> getFieldNameList(String applicationName, String metricName) {
        String query = queryStatementWriter.queryForFieldNameList(applicationName, metricName);
        ResultSet resultSet = queryAndGetResultSet(query);
        return pinotResultSetProcessor.processStringList(resultSet);
    }

    @Override
    public List<TagBo> getTagBoList(String applicationName, String metricName, String fieldName) {
        ResultSet timestampResultSet = queryAndGetResultSet(queryStatementWriter.queryTimestampForField(applicationName, metricName, fieldName));
        long timestamp = timestampResultSet.getLong(0, 0);
        ResultSet resultSet = queryAndGetResultSet(queryStatementWriter.queryForTagBoList(applicationName, metricName, fieldName, timestamp));
        return pinotResultSetProcessor.processTagBoList(resultSet);
    }

    @Override
    public List<SystemMetricBo> getSystemMetricBoList(String applicationName, String metricName, String fieldName, List<TagBo> tags, Range range) {
        String query = queryStatementWriter.queryForSystemMetricBoList(applicationName, metricName, fieldName, tags, range);
//         logger.info(query);
        ResultSet resultSet = queryAndGetResultSet(query);
//        logger.info("rows: {} columns: {}", resultSet.getRowCount(), resultSet.getColumnCount());

        return pinotResultSetProcessor.processSystemMetricBoList(resultSet);
    }

    private ResultSet queryAndGetResultSet(String query) {
        Request request = new Request("sql", query);
        ResultSetGroup resultSetGroup = pinotConnection.execute(request);
        return resultSetGroup.getResultSet(0);
    }
}
