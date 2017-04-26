package com.estafet.openshift.util;

import com.estafet.openshift.model.exception.EmptyArgumentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

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

//		@Test
//		public void loadInitialStateFromTheDB(){
//				final List<String> strings = providerMock.loadInitialStateFromTheDB();
//				assertNotNull(strings);
//		}
		/**
		 * FOR TEST PURPOSES ONLY
		 * Useful when you need to use Sysytem.getenv(*) in test
		 * @param newenv
		 */
		private static void setTestEnvVariablesInMemory(Map<String, String> newenv)
		{
				try
				{
						Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
						Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
						theEnvironmentField.setAccessible(true);
						Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
						env.putAll(newenv);
						Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
						theCaseInsensitiveEnvironmentField.setAccessible(true);
						Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
						cienv.putAll(newenv);
				}
				catch (NoSuchFieldException e)
				{
						try {
								Class[] classes = Collections.class.getDeclaredClasses();
								Map<String, String> env = System.getenv();
								for(Class cl : classes) {
										if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
												Field field = cl.getDeclaredField("m");
												field.setAccessible(true);
												Object obj = field.get(env);
												Map<String, String> map = (Map<String, String>) obj;
												map.clear();
												map.putAll(newenv);
										}
								}
						} catch (Exception e2) {
								e2.printStackTrace();
						}
				} catch (Exception e1) {
						e1.printStackTrace();
				}
		}


}