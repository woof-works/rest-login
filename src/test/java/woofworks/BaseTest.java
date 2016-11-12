package woofworks;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;
import woofworks.dto.LoginRequestDTO;
import woofworks.dto.LoginResponseDTO;
import woofworks.model.User;
import woofworks.service.UserService;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Objects.equal;

public abstract class BaseTest {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired
	protected BCryptPasswordEncoder encoder;

	@Autowired
	protected UserService service;

	protected String login = "user";

	protected String password = "password";

	protected User user;

	/**
	 * Creates and saves a default user
	 * 
	 * @return
	 */
	protected User saveUser() {
		user = new User();
		user.setLogin(login);
		user.setPassword(encoder.encode(password));

		service.save(user);

		return user;
	}

	/**
	 * Logs in as a default user
	 * 
	 * @return
	 */
	protected LoginResponseDTO logIn() {
		return logIn(this.password);
	}

	/**
	 * Logs in as the default user with the specified password
	 * 
	 * @param password
	 * @return
	 */
	protected LoginResponseDTO logIn(String password) {
		LoginRequestDTO request = new LoginRequestDTO();
		request.username = this.login;
		request.password = password;

		return service.logUserIn(request);
	}

	/**
	 * Checks the collection size against the expected value
	 * 
	 * @param collection
	 * @param expected
	 */
	protected void assertCollectionSize(Collection<?> collection, int expected) {
		Assert.isTrue(collection.size() == expected, "Expected " + expected + " item, but got "
				+ collection.size());
	}

	/**
	 * Checks the expected value against the compare value
	 * 
	 * @param expected
	 * @param compare
	 */
	protected void assertIsEqual(Object expected, Object compare) {
		Assert.isTrue(equal(expected, compare), "Expected value " + expected + ", but got value "
				+ compare);
	}

	/**
	 * Cleans up the in-memory database
	 * 
	 * @throws SQLException
	 */
	@After
	@Transactional
	public void cleanup() throws SQLException {
		String driverName = dataSource.getConnection().getMetaData().getDriverName();

		Assert.isTrue(driverName.equals("H2 JDBC Driver"),
				"This can only be used on the in-memory database via JUnit. Current DB name: "
						+ dataSource.getConnection().getMetaData().getDriverName());

		// Create session
		Session session = sessionFactory.openSession();

		try {
			session.createSQLQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

			// Find all involved tables
			for (final ClassMetadata metadata : sessionFactory.getAllClassMetadata().values()) {
				final String tableName = ((AbstractEntityPersister) metadata).getTableName();
				if (tableName != null) {
					session.createSQLQuery("TRUNCATE TABLE " + tableName).executeUpdate();
				}
			}

			final Map<String, CollectionMetadata> allCollectionMetadata = sessionFactory
					.getAllCollectionMetadata();
			for (final CollectionMetadata metadata : allCollectionMetadata.values()) {
				final String tableName = ((AbstractCollectionPersister) metadata).getTableName();
				if (tableName != null) {
					session.createSQLQuery("TRUNCATE TABLE " + tableName).executeUpdate();
				}
			}

			session.createSQLQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

		} finally {
			session.close();
		}
	}
}
