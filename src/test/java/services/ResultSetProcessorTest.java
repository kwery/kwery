package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import services.scheduler.ResultSetProcessor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResultSetProcessorTest {
    private String[] labels = {"label0", "label1"};
    private String[][] result = {{"foo", "bar"}, {"goo", "cho"}};
    @Mock
    private ResultSet resultSet;
    @Mock
    private ResultSetMetaData resultSetMetaData;
    private int currentRow = -1;

    @Before
    public void setUpResultSetProcessorTest() throws SQLException {
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(labels.length);

        doAnswer(new Answer<String>(){
            int counter = 0;
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return labels[counter++];
            }
        }).when(resultSetMetaData).getColumnLabel(anyInt());

        doAnswer(new Answer<Boolean>() {
            int counter = 0;
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                boolean ret = counter < result.length;
                counter = counter + 1;
                currentRow = currentRow + 1;
                return ret;
            }
        }).when(resultSet).next();

        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return result[currentRow][(Integer)args[0] - 1];
            }
        }).when(resultSet).getString(anyInt());
    }

    @Test
    public void test() throws SQLException, JsonProcessingException {
        ResultSetProcessor processor = new ResultSetProcessor(resultSet);
        String expected = new ObjectMapper().writeValueAsString(ImmutableList.of(
                Arrays.asList(labels), Arrays.asList(result[0]), Arrays.asList(result[1])
        ));
        assertThat(processor.process(), is(expected));
    }
}
