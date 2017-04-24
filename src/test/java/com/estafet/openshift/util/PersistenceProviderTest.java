package com.estafet.openshift.util;

import com.estafet.openshift.model.exception.EmptyArgumentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.estafet.openshift.util.TestConstants.*;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Delcho Delov on 24.04.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersistenceProviderTest {
		@InjectMocks
		private PersistenceProvider providerMock;
		@Mock
		private Connection connectionMock = mock(Connection.class);
		@Mock
		private PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
		@Mock
		private ResultSet resultSetMock = mock(ResultSet.class);

		@Before
		public void setUp() throws Exception {
				MockitoAnnotations.initMocks(this);
		}

		@Test(expected = EmptyArgumentException.class)
		public void writeLeaksDataEmptyConnection() throws Exception {
				providerMock.writeLeaksData(TEST_DEVICE_ID, TEST_PRESSURE_LOW, TEST_STAMP, null);
		}
		@Test(expected = EmptyArgumentException.class)
		public void writeLeaksDataEmptyArgumentException() throws Exception {
				providerMock.writeLeaksData(TEST_DEVICE_ID, null, TEST_STAMP, connectionMock);
		}
		@Test
		public void writeLeaksOK() throws EmptyArgumentException, SQLException {
				when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
				when(preparedStatementMock.executeUpdate()).thenReturn(1);
				try {
						providerMock.writeLeaksData(TEST_DEVICE_ID, TEST_PRESSURE_LOW, TEST_STAMP, connectionMock);
				}catch (EmptyArgumentException|SQLException e){
						fail();
				}
		}


}