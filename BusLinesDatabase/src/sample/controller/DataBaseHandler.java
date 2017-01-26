package sample.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import consts.BusConsts;
import consts.BusModelConsts;
import consts.CategoryConsts;
import consts.DriveConsts;
import consts.GraphicPositionConsts;
import consts.IntermediateDriveConsts;
import consts.PersonConsts;
import consts.ServiceConsts;
import consts.TimeTablePositionConsts;
import sample.model.Bus;
import sample.model.BusModel;
import sample.model.Category;
import sample.model.Course;
import sample.model.Drive;
import sample.model.GraphicPosition;
import sample.model.IntermediateDrive;
import sample.model.Person;
import sample.model.Service;
import sample.model.TimeTablePosition;

public class DataBaseHandler {
	/** Packet address for JDBC driver */
	private static String driver;
	/** URL data base address */
	private static String url;
	/** User login for data base */
	private static String login;
	/** User password for data base */
	private static String password;

	/**
	 * STATIC BLOCK - update default configuration data/download from
	 * dbconfig.properties file - start JDBC driver
	 */
	static {
		try {
			/**
			 * Sets fields driver, URL, login and password - needed to make
			 * connection with database
			 */
			Configuration();
		} catch (IOException ex) {
			System.err.println("Configuration failed.\n" + ex.getMessage());
		}

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException ex) {
			System.err.println("Registration the JDBC/ODBC driver failed.\n" + ex.getMessage());
		}
	}

	private static void Configuration() throws IOException {
		File file = new File("dbconfig.properties");
		if (!file.exists()) {
			file.createNewFile();
			String defaultData = "#Driver for JDBC\n" + "driver=oracle.jdbc.driver.OracleDriver\n"
					+ "#Data Base adress (default on your localhost on 80 port)\n"
					+ "url=jdbc:oracle:thin:@//localhost:1522/orcl\n" + "#login on your database\n" + "login=xxx\n"
					+ "#your password on database\n" + "password=xxx";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}
		try (FileReader reader = new FileReader("dbconfig.properties")) {
			Properties prop = new Properties();
			prop.load(reader);
			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			login = prop.getProperty("login");
			password = prop.getProperty("password");
		}

	}

	/** Returns connection handler through driver */
	private Connection createConnection() throws SQLException {
		return DriverManager.getConnection(url, login, password);
	}

	/** Closing connection with local data base */
	private void endConnection(Connection connection) {
		if (connection == null) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException ex) {
			System.err.println("Closing connection with local data base failed.\n" + ex.getSQLState());
		}
	}

	/**
	 * Getting all drives + price,time and distance summed within all
	 * intermediate drives in
	 */
	public List<Drive> getAllDrives() {
		String query = "select d.DRIVE_ID,d.CITY_FROM,d.CITY_TO, sum(idr.DISTANCE) as DISTANCE_SUM,"
				+ " sum(idr.PRICE) as PRICE_SUM, sum(idr.TIME) as TIME_SUM" + " from DRIVES d,INTERMEDIATE_DRIVES idr"
				+ " where  idr.INTERMEDIATE_DRIVE_ID in (SELECT INTERMEDIATE_DRIVE_ID "
				+ " from DRIVE_CONTENTS where DRIVE_ID=d.DRIVE_ID) group by d.DRIVE_ID,d.CITY_FROM,d.CITY_TO";
		List<Drive> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Drive drive = new Drive(resultSet.getInt(DriveConsts.ID), resultSet.getString(DriveConsts.CITY_FROM),
						resultSet.getString(DriveConsts.CITY_TO), resultSet.getInt("TIME_SUM"),
						resultSet.getInt("DISTANCE_SUM"), resultSet.getFloat("PRICE_SUM"),
						getAllIntermediateDrives(resultSet.getInt(DriveConsts.ID)));
				result.add(drive);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting drives with intermediate drives data failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** Getting all buses joining bus model related with each bus */
	public List<Bus> getAllBuses() {
		String query = "select * from BUSES join BUS_MODELS on BUSES.BUS_MODEL_ID = BUS_MODELS.BUS_MODEL_ID";
		List<Bus> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Bus bus = new Bus(resultSet.getInt(BusConsts.ID), resultSet.getInt(BusConsts.MODEL_ID),
						resultSet.getDate(BusConsts.BOUGHT_TIME), resultSet.getString(BusConsts.LICENSE_PLATE),
						resultSet.getString(BusConsts.SERIAL_NUMBER), resultSet.getInt(BusConsts.SEATS_NUM),
						resultSet.getInt(BusConsts.MILEAGE), resultSet.getFloat(BusConsts.CLASS_RATE),
						resultSet.getString(BusConsts.BUS_MODEL_NAME));
				result.add(bus);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting all buses failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** Getting all intermediate drives related with drive with given id */
	private List<IntermediateDrive> getAllIntermediateDrives(int driveId) {
		String query = "select * from INTERMEDIATE_DRIVES where INTERMEDIATE_DRIVE_ID in "
				+ "(select INTERMEDIATE_DRIVE_ID " + "from DRIVE_CONTENTS where DRIVE_ID=" + driveId + ")";
		List<IntermediateDrive> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				IntermediateDrive iDrive = new IntermediateDrive(resultSet.getInt(IntermediateDriveConsts.ID),
						resultSet.getString(IntermediateDriveConsts.CITY_FROM),
						resultSet.getString(IntermediateDriveConsts.CITY_TO),
						resultSet.getInt(IntermediateDriveConsts.TIME),
						resultSet.getInt(IntermediateDriveConsts.DISTANCE),
						resultSet.getFloat(IntermediateDriveConsts.PRICE));
				result.add(iDrive);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting intermediate drives of one drive failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/**
	 * Getting all data of services where mileage or age is greater than needed
	 * in plan service
	 */
	public List<Service> getServicesFromPlan(Bus bus, int ageInMonths, int mileage) {
		String query = "select distinct s.SERVICE_ID,s.OPERATION,s.MEANINGNESS,spp.MILEAGE_KM,spp.MONTHS_TO_SERVICE from SERVICES s "
				+ "inner join  POSITION_REQUIREMENTS pr on s.SERVICE_ID = pr.SERVICE_ID "
				+ "inner join SERVICE_PLAN_POSITIONS spp on spp.SERVICE_PLAN_POSITION_ID = pr.SERVICE_PLAN_POSITION_ID  "
				+ "where spp.BUS_MODEL_ID=? AND (spp.MILEAGE_KM BETWEEN ? AND ?) OR (spp.MONTHS_TO_SERVICE BETWEEN ? AND ?) ";
		List<Service> result = new ArrayList<>();
		List<Integer> historyIds = getServiceHistory(bus).stream().map(s -> s.getId()).collect(Collectors.toList());
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			statement.setInt(counter++, bus.getBusModelId());

			statement.setInt(counter++, getMileageRangeLowerValue(mileage));
			statement.setInt(counter++, mileage);
			statement.setInt(counter++, getMonthRangeLowerValue(ageInMonths));
			statement.setInt(counter++, ageInMonths);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Service service = new Service(resultSet.getInt(ServiceConsts.ID),
						resultSet.getString(ServiceConsts.OPERATION), resultSet.getString(ServiceConsts.MEANINGNESS),
						resultSet.getInt(ServiceConsts.NEEDED_MILEAGE), resultSet.getInt(ServiceConsts.NEEDED_MONTH));
				if (!historyIds.contains(service.getId()))
					result.add(service);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting services from service plan  failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	private int getMonthRangeLowerValue(int monthValue) {
		if (monthValue >= 12)
			return 12;
		else if (monthValue >= 9)
			return 9;
		else if (monthValue >= 6)
			return 6;
		else if (monthValue >= 3)
			return 3;
		else if (monthValue >= 1)
			return 1;
		else
			return 0;
	}

	private int getMileageRangeLowerValue(int mileageValue) {
		if (mileageValue >= 19200)
			return 19200;
		else if (mileageValue >= 14400)
			return 14400;
		else if (mileageValue >= 9600)
			return 9600;
		else if (mileageValue >= 4800)
			return 4800;
		else if (mileageValue >= 1600)
			return 1600;
		else
			return 0;
	}

	/** Getting all data of done services with date for given bus */
	public List<Service> getServiceHistory(Bus bus) {
		String query = "select s.SERVICE_ID, s.OPERATION, s.MEANINGNESS, sbp.SERVICE_DATE, sbp.MILEAGE_THIS_TIME"
				+ " from SERVICES_BOOK_POSITIONS sbp "
				+ "inner join SERVICE_ACCOMPLISHMENTS sa on sa.SERVICES_BOOK_POSITION_ID=sbp.SERVICES_BOOK_POSITION_ID "
				+ "inner join SERVICES s on s.SERVICE_ID=sa.SERVICE_ID where BUS_ID=" + bus.getBusId() + " and STATE=?";
		List<Service> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			statement.setString(1, "COMPLETE");
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Service service = new Service(resultSet.getInt(ServiceConsts.ID),
						resultSet.getString(ServiceConsts.OPERATION), resultSet.getString(ServiceConsts.MEANINGNESS),
						resultSet.getDate(ServiceConsts.OPTIONAL_DATE),
						resultSet.getInt(ServiceConsts.OPTIONAL_MILEAGE));
				result.add(service);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting services with dates failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** Getting all intermediate drives */
	public List<IntermediateDrive> getIntermediateDrivesData() {
		String query = "select * from INTERMEDIATE_DRIVES ORDER BY INTERMEDIATE_DRIVE_ID";
		List<IntermediateDrive> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				IntermediateDrive iDrive = new IntermediateDrive(resultSet.getInt(IntermediateDriveConsts.ID),
						resultSet.getString(IntermediateDriveConsts.CITY_FROM),
						resultSet.getString(IntermediateDriveConsts.CITY_TO),
						resultSet.getInt(IntermediateDriveConsts.TIME),
						resultSet.getInt(IntermediateDriveConsts.DISTANCE),
						resultSet.getFloat(IntermediateDriveConsts.PRICE));
				result.add(iDrive);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting intermediate drives data failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** Getting intermediate drives where starting city is given in argument */
	public List<IntermediateDrive> getIntermediateDrivesFromData(String cityFrom) {
		String query = "select * from INTERMEDIATE_DRIVES where CITY_FROM = '" + cityFrom + "'";
		List<IntermediateDrive> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				IntermediateDrive iDrive = new IntermediateDrive(resultSet.getInt(IntermediateDriveConsts.ID),
						resultSet.getString(IntermediateDriveConsts.CITY_FROM),
						resultSet.getString(IntermediateDriveConsts.CITY_TO),
						resultSet.getInt(IntermediateDriveConsts.TIME),
						resultSet.getInt(IntermediateDriveConsts.DISTANCE),
						resultSet.getFloat(IntermediateDriveConsts.PRICE));
				result.add(iDrive);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting intermediate drvies from stating city failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** Getting all services */
	public List<Service> getServices() {
		String query = "select * from SERVICES";
		List<Service> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Service service = new Service(resultSet.getInt("SERVICE_ID"), resultSet.getString("OPERATION"),
						resultSet.getString("MEANINGNESS"));
				result.add(service);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting services from data failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** Getting all bus models */
	public List<BusModel> getAllBusModels() {
		String query = "select * from BUS_MODELS";
		List<BusModel> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				BusModel busModel = new BusModel(resultSet.getInt(BusModelConsts.ID),
						resultSet.getString(BusModelConsts.MODEL_NAME));
				result.add(busModel);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting all bus models failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** INSERT UPDATE DELETE DRIVE */
	public void insertDrive(Drive drive) {
		String query = "INSERT INTO " + "DRIVES" + "(DRIVE_ID,CITY_FROM,CITY_TO) " + "VALUES(DRIVE_SEQ.NEXTVAL,?,?)";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			statement.setString(counter++, drive.getFrom());
			statement.setString(counter++, drive.getTo());
			statement.executeUpdate();
			int id = getDriveId(drive);
			for (IntermediateDrive idr : drive.getListOfIntermediateDrive()) {
				connectIntermediateDriveWithDrive(idr.getId(), id);
			}
		} catch (SQLException ex) {
			System.err.println("Inserting drive failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	/** Inserting intermediate drive id and drive id in DRIVE_CONTENTS */
	private void connectIntermediateDriveWithDrive(int intermediateId, int driveId) {
		String query = "INSERT INTO " + "DRIVE_CONTENTS" + "(INTERMEDIATE_DRIVE_ID,DRIVE_ID) " + "VALUES(?,?)";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			statement.setInt(counter++, intermediateId);
			statement.setInt(counter++, driveId);
			statement.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("Inserting in DRIVE_CONTENTS failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	public int getDriveId(Drive drive) {
		String query = "select DRIVE_ID from DRIVES where CITY_FROM=? AND CITY_TO=?";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			statement.setString(counter++, drive.getFrom());
			statement.setString(counter++, drive.getTo());
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getInt("DRIVE_ID");
			}
		} catch (SQLException ex) {
			System.err.println("Selecting drive id via cities failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
		return -1;
	}

	public void updateDrive(Drive drive) {
		String query = "update DRIVES set CITY_FROM=?,CITY_TO=? " + "where DRIVE_ID=?";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			statement.setString(counter++, drive.getFrom());
			statement.setString(counter++, drive.getTo());
			statement.setInt(counter++, drive.getId());
			statement.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("Updating drive failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	public void deleteDrive(Drive drive) {
		String query = "delete from DRIVES where DRIVE_ID=?";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			statement.setInt(1, drive.getId());
			statement.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("Deleting drive failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	/** INSERT UPDATE DELETE BUS */
	public void insertBus(Bus bus) {
		String query = "INSERT INTO " + "BUSES" + "(BUS_ID,BUS_MODEL_ID,BOUGHT_TIME,LICENSE_PLATE,"
				+ "SERIAL_NUMBER,SEATS_NUMBER,MILEAGE,CLASS_RATE) " + "VALUES (BUS_SEQ.NEXTVAL,?,?,?,?,?,?,?)";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			BusModel model = getBusModelByName(bus.getModelName());
			statement.setInt(counter++, model.getId());
			statement.setDate(counter++, bus.getDateOfBuy());
			statement.setString(counter++, bus.getLicensePlate());
			statement.setString(counter++, bus.getSereialNumber());
			statement.setInt(counter++, bus.getSeats());
			statement.setInt(counter++, bus.getMileage());
			statement.setFloat(counter++, bus.getClassRate());
			statement.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("Inserting bus failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	public void updateBus(Bus bus) {
		String query = "update BUSES set BUS_MODEL_ID=?,BOUGHT_TIME=?,LICENSE_PLATE=?,"
				+ "SERIAL_NUMBER=?,SEATS_NUMBER=?,MILEAGE=?,CLASS_RATE=? where BUS_ID=?";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			BusModel model = getBusModelByName(bus.getModelName());
			statement.setInt(counter++, model.getId());
			statement.setDate(counter++, bus.getDateOfBuy());
			statement.setString(counter++, bus.getLicensePlate());
			statement.setString(counter++, bus.getSereialNumber());
			statement.setInt(counter++, bus.getSeats());
			statement.setInt(counter++, bus.getMileage());
			statement.setFloat(counter++, bus.getClassRate());
			statement.setInt(counter++, bus.getBusId());
			statement.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("Updating bus failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	public void deleteBus(Bus bus) {
		String query = "delete from BUSES where BUS_ID=?";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			statement.setInt(1, bus.getBusId());
			statement.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("Deleting bus failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	/** Getting bus model by name */
	private BusModel getBusModelByName(String busModelName) {
		String query = "select * from BUS_MODELS where NAME='" + busModelName + "'";
		BusModel busModel = null;
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				busModel = new BusModel(resultSet.getInt(BusModelConsts.ID),
						resultSet.getString(BusModelConsts.MODEL_NAME));
			}
		} catch (SQLException ex) {
			System.err.println("Selecting  bus model by name failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return busModel;
	}

	public void insertServicesIntoBusServiceBook(Bus bus, List<Service> services, Date serviceDate, int mileageThisTime,
			String state, String carServiceAddress) {
		String query = "insert into SERVICES_BOOK_POSITIONS(SERVICES_BOOK_POSITION_ID,BUS_ID,SERVICE_DATE,MILEAGE_THIS_TIME,"
				+ "STATE,CAR_SERVICE_ADDRESS) values(SERVICES_BOOK_POSITION_SEQ.NEXTVAL,?,?,?,?,?)";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement pstatement = c.prepareStatement(query);
			int counter = 1;
			pstatement.setInt(counter++, bus.getBusId());
			pstatement.setDate(counter++, serviceDate);
			pstatement.setInt(counter++, mileageThisTime);
			pstatement.setString(counter++, state);
			pstatement.setString(counter++, carServiceAddress);
			pstatement.executeUpdate();

			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT SERVICES_BOOK_POSITION_SEQ.CURRVAL FROM dual");
			long generatedId = -1;
			if (rs != null && rs.next())
				generatedId = rs.getInt(1);
			for (Service service : services)
				insertServicesAccomplishment(generatedId, service);
		} catch (SQLException ex) {
			System.err.println("Inserting service book position  failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	private void insertServicesAccomplishment(long positionId, Service service) {
		String query = "insert into SERVICE_ACCOMPLISHMENTS(SERVICES_BOOK_POSITION_ID,SERVICE_ID) VALUES(" + positionId
				+ "," + service.getId() + ")";
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException ex) {
			System.err.println("Inserting  service accomplishment failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	public List<Category> getCategories() {
		String query = "select * from CATEGORIES";
		List<Category> result = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Category category = new Category(resultSet.getInt(CategoryConsts.ID),
						resultSet.getString(CategoryConsts.SHORTCUT), resultSet.getString(CategoryConsts.DESCRIPTION),
						resultSet.getFloat(CategoryConsts.PRICE_RATE));
				result.add(category);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting all categories failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	public List<TimeTablePosition> getTimeTablePositionfForDrive(int driveId) {
		String query = "select * from TIME_TABLE_POSITIONS where DRIVE_ID=" + driveId;
		List<TimeTablePosition> timeTable = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				TimeTablePosition timetablePosition = new TimeTablePosition(
						resultSet.getInt(TimeTablePositionConsts.ID),
						resultSet.getInt(TimeTablePositionConsts.DRIVE_ID),
						resultSet.getString(TimeTablePositionConsts.WEEK_DAY),
						resultSet.getString(TimeTablePositionConsts.LEAVING_HOUR),
						resultSet.getString(TimeTablePositionConsts.REGISTER_TELEPHONE_NUMBER),
						resultSet.getFloat(TimeTablePositionConsts.TIME_CATEGORY_RATE));
				timeTable.add(timetablePosition);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting time table positions for drive failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return timeTable;
	}

	public List<Bus> getFreeBusesByCategory(Course courseData, int categoryId) {
		List<Bus> result = new ArrayList<>();
		String colidatingBusIds = getColidingBusesId(courseData).stream().map(i -> i.toString())
				.collect(Collectors.joining(","));
		String query = null;
		if (!colidatingBusIds.isEmpty())
			query = "select * from BUSES join BUS_MODELS on BUSES.BUS_MODEL_ID = BUS_MODELS.BUS_MODEL_ID WHERE BUS_ID NOT IN ("
					+ getColidingBusesId(courseData).stream().map(i -> i.toString()).collect(Collectors.joining(","))
					+ ") AND CATEGORY_ID=" + categoryId;
		else
			query = "select * from BUSES join BUS_MODELS on BUSES.BUS_MODEL_ID = BUS_MODELS.BUS_MODEL_ID WHERE CATEGORY_ID="
					+ categoryId;

		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Bus bus = new Bus(resultSet.getInt(BusConsts.ID), resultSet.getInt(BusConsts.MODEL_ID),
						resultSet.getDate(BusConsts.BOUGHT_TIME), resultSet.getString(BusConsts.LICENSE_PLATE),
						resultSet.getString(BusConsts.SERIAL_NUMBER), resultSet.getInt(BusConsts.SEATS_NUM),
						resultSet.getInt(BusConsts.MILEAGE), resultSet.getFloat(BusConsts.CLASS_RATE),
						resultSet.getString(BusConsts.BUS_MODEL_NAME));
				result.add(bus);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting all buses failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	public List<Integer> getColidingBusesId(Course courseData) {
		String query = "select BUS_ID from COURSES c,TIME_TABLE_POSITIONS ttp "
				+ "where c.TIME_TABLE_POSITION_ID=ttp.TIME_TABLE_POSITION_ID "
				+ "and CAST(c.COURSE_DATE as TIMESTAMP) between  ? and ? or " + "(CAST(c.COURSE_DATE as TIMESTAMP) + "
				+ "(select sum(TIME) from INTERMEDIATE_DRIVES idr where  idr.INTERMEDIATE_DRIVE_ID in "
				+ "(select INTERMEDIATE_DRIVE_ID from DRIVE_CONTENTS where DRIVE_ID=ttp.DRIVE_ID))/1440) "
				+ " between ? and ? or " + "(CAST(c.COURSE_DATE as TIMESTAMP) < ? and "
				+ "(CAST(c.COURSE_DATE as TIMESTAMP) + "
				+ "(select sum(TIME) from INTERMEDIATE_DRIVES idr where  idr.INTERMEDIATE_DRIVE_ID in "
				+ "(select INTERMEDIATE_DRIVE_ID from DRIVE_CONTENTS where DRIVE_ID=ttp.DRIVE_ID))/1440) > ?)";
		Calendar leavingCourseTime = Calendar.getInstance();
		leavingCourseTime.setTimeInMillis(courseData.getDate().getTimeInMillis());
		Calendar arrivingCourseTime = Calendar.getInstance();
		arrivingCourseTime.setTimeInMillis(courseData.getDate().getTimeInMillis());
		arrivingCourseTime.add(Calendar.MINUTE, courseData.getCourseDrive().getTime());
		List<Integer> colidingBusesId = new ArrayList<>();
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement pstatement = c.prepareStatement(query);
			int counter = 1;
			pstatement.setTimestamp(counter++, new Timestamp(leavingCourseTime.getTimeInMillis()));
			pstatement.setTimestamp(counter++, new Timestamp(arrivingCourseTime.getTimeInMillis()));
			pstatement.setTimestamp(counter++, new Timestamp(leavingCourseTime.getTimeInMillis()));
			pstatement.setTimestamp(counter++, new Timestamp(arrivingCourseTime.getTimeInMillis()));
			pstatement.setTimestamp(counter++, new Timestamp(leavingCourseTime.getTimeInMillis()));
			pstatement.setTimestamp(counter++, new Timestamp(arrivingCourseTime.getTimeInMillis()));
			ResultSet resultSet = pstatement.executeQuery();
			while (resultSet.next())
				colidingBusesId.add(resultSet.getInt("BUS_ID"));
		} catch (SQLException ex) {
			System.err.println("Selecting colidating buses failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
		return colidingBusesId;
	}

	/** Getting all free drivers in time current */
	public List<Person> getFreeDrivers(Course courseData) {
		List<Person> result = new ArrayList<>();
		List<GraphicPosition> graphicPositionFromLeaving = getGraphicPositionsFromLeavingDate(
				courseData.getDate().getTimeInMillis()).stream()
						.filter(gp -> gp.getDriverId() != 0 && gp.getStatus().equals("FREE"))
						.collect(Collectors.toList());

		Calendar dateArriving = Calendar.getInstance();
		dateArriving.setTimeInMillis(courseData.getDate().getTimeInMillis());
		dateArriving.add(Calendar.MINUTE, courseData.getCourseDrive().getTime());

		List<GraphicPosition> graphicPositionFromArriving = getGraphicPositionsFromArrivingDate(
				dateArriving.getTimeInMillis()).stream()
						.filter(gp -> gp.getDriverId() != 0 && gp.getStatus().equals("FREE"))
						.collect(Collectors.toList());

		List<GraphicPosition> graphicPositionsStrictInsideCourseTime = getGraphicPositionsStrictInsideCourseTime(
				courseData.getDate().getTimeInMillis(), dateArriving.getTimeInMillis());

		List<Integer> driverIds = graphicPositionFromLeaving.stream().map(GraphicPosition::getDriverId)
				.collect(Collectors.toList());

		List<Integer> freeDrivers = new ArrayList<>();
		for (int id : driverIds) {
			if (graphicPositionFromArriving.stream().map(GraphicPosition::getDriverId).collect(Collectors.toList())
					.contains(id)) {
				if (graphicPositionsStrictInsideCourseTime.isEmpty())
					freeDrivers.add(id);
				else if (graphicPositionsStrictInsideCourseTime.stream()
						.filter(gp -> gp.getDriverId() == id && gp.getStatus().equals("FREE"))
						.count() == graphicPositionsStrictInsideCourseTime.size())
					freeDrivers.add(id);
			}
		}
		if (freeDrivers.isEmpty())
			return result;

		String query = "select * from DRIVERS d,PERSONS p  where d.DRIVER_ID in ("
				+ freeDrivers.stream().map(i -> i.toString()).collect(Collectors.joining(","))
				+ ") AND d.PERSON_ID=p.PERSON_ID";

		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Person person = new Person(resultSet.getInt(PersonConsts.ID), resultSet.getString(PersonConsts.NAME),
						resultSet.getString(PersonConsts.SURNAME));
				result.add(person);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting free drivers failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	private List<GraphicPosition> getGraphicPositionsFromLeavingDate(long dateLeaving) {
		List<GraphicPosition> result = new ArrayList<>();
		String query = "select * from GRAPHIC_POSITIONS where "
				+ "? between TIME_FROM and TIME_TO AND COURSE_ID IS NULL";

		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement pstatement = c.prepareStatement(query);
			pstatement.setTimestamp(1, new Timestamp(dateLeaving));
			ResultSet resultSet = pstatement.executeQuery();

			while (resultSet.next()) {
				Calendar calFrom = Calendar.getInstance();
				calFrom.setTimeInMillis(resultSet.getTimestamp(GraphicPositionConsts.TIME_FROM).getTime());

				Calendar calTo = Calendar.getInstance();
				calTo.setTimeInMillis(resultSet.getTimestamp(GraphicPositionConsts.TIME_TO).getTime());

				GraphicPosition gp = new GraphicPosition(resultSet.getInt(GraphicPositionConsts.ID),
						resultSet.getInt(GraphicPositionConsts.HOSTESS_ID),
						resultSet.getInt(GraphicPositionConsts.DRIVER_ID),
						resultSet.getInt(GraphicPositionConsts.COURSE_ID), calFrom, calTo,
						resultSet.getString(GraphicPositionConsts.STATUS),
						resultSet.getFloat(GraphicPositionConsts.HOURLY_BID));
				result.add(gp);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting graphic positions by leaving date failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	private List<GraphicPosition> getGraphicPositionsStrictInsideCourseTime(long dateLeaving, long dateArriving) {
		List<GraphicPosition> result = new ArrayList<>();
		String query = "select * from GRAPHIC_POSITIONS where " + "TIME_FROM > ? and TIME_TO < ? and COURSE_ID IS NULL";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement pstatement = c.prepareStatement(query);
			pstatement.setTimestamp(1, new Timestamp(dateLeaving));
			pstatement.setTimestamp(2, new Timestamp(dateArriving));
			ResultSet resultSet = pstatement.executeQuery();

			while (resultSet.next()) {
				Calendar calFrom = Calendar.getInstance();
				calFrom.setTimeInMillis(resultSet.getTimestamp(GraphicPositionConsts.TIME_FROM).getTime());

				Calendar calTo = Calendar.getInstance();
				calTo.setTimeInMillis(resultSet.getTimestamp(GraphicPositionConsts.TIME_TO).getTime());

				GraphicPosition gp = new GraphicPosition(resultSet.getInt(GraphicPositionConsts.ID),
						resultSet.getInt(GraphicPositionConsts.HOSTESS_ID),
						resultSet.getInt(GraphicPositionConsts.DRIVER_ID),
						resultSet.getInt(GraphicPositionConsts.COURSE_ID), calFrom, calTo,
						resultSet.getString(GraphicPositionConsts.STATUS),
						resultSet.getFloat(GraphicPositionConsts.HOURLY_BID));
				result.add(gp);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting graphic positions by leaving date failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	private List<GraphicPosition> getGraphicPositionsFromArrivingDate(long dateArriving) {
		List<GraphicPosition> result = new ArrayList<>();
		String query = "select * from GRAPHIC_POSITIONS where "
				+ "? between TIME_FROM and TIME_TO AND COURSE_ID IS NULL";

		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement pstatement = c.prepareStatement(query);
			pstatement.setTimestamp(1, new Timestamp(dateArriving));
			ResultSet resultSet = pstatement.executeQuery();

			while (resultSet.next()) {
				Calendar calFrom = Calendar.getInstance();
				calFrom.setTimeInMillis(resultSet.getTimestamp(GraphicPositionConsts.TIME_FROM).getTime());

				Calendar calTo = Calendar.getInstance();
				calTo.setTimeInMillis(resultSet.getTimestamp(GraphicPositionConsts.TIME_TO).getTime());

				GraphicPosition gp = new GraphicPosition(resultSet.getInt(GraphicPositionConsts.ID),
						resultSet.getInt(GraphicPositionConsts.HOSTESS_ID),
						resultSet.getInt(GraphicPositionConsts.DRIVER_ID),
						resultSet.getInt(GraphicPositionConsts.COURSE_ID), calFrom, calTo,
						resultSet.getString(GraphicPositionConsts.STATUS),
						resultSet.getFloat(GraphicPositionConsts.HOURLY_BID));
				result.add(gp);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting graphic positions by arriving date failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	/** Getting all free hostess in time current */
	public List<Person> getFreeHostess(Course courseData) {
		List<Person> result = new ArrayList<>();
		List<GraphicPosition> graphicPositionFromLeaving = getGraphicPositionsFromLeavingDate(
				courseData.getDate().getTimeInMillis()).stream()
						.filter(gp -> gp.getHostessId() != 0 && gp.getStatus().equals("FREE"))
						.collect(Collectors.toList());

		Calendar dateArriving = Calendar.getInstance();
		dateArriving.setTimeInMillis(courseData.getDate().getTimeInMillis());
		dateArriving.add(Calendar.MINUTE, courseData.getCourseDrive().getTime());

		List<GraphicPosition> graphicPositionFromArriving = getGraphicPositionsFromArrivingDate(
				dateArriving.getTimeInMillis()).stream()
						.filter(gp -> gp.getHostessId() != 0 && gp.getStatus().equals("FREE"))
						.collect(Collectors.toList());

		List<GraphicPosition> graphicPositionsStrictInsideCourseTime = getGraphicPositionsStrictInsideCourseTime(
				courseData.getDate().getTimeInMillis(), dateArriving.getTimeInMillis());

		List<Integer> hostessIds = graphicPositionFromLeaving.stream().map(GraphicPosition::getHostessId)
				.collect(Collectors.toList());

		List<Integer> freeHostess = new ArrayList<>();
		for (int id : hostessIds) {
			if (graphicPositionFromArriving.stream().map(GraphicPosition::getHostessId).collect(Collectors.toList())
					.contains(id)) {
				if (graphicPositionsStrictInsideCourseTime.isEmpty())
					freeHostess.add(id);
				else if (graphicPositionsStrictInsideCourseTime.stream()
						.filter(gp -> gp.getHostessId() == id && gp.getStatus().equals("FREE"))
						.count() == graphicPositionsStrictInsideCourseTime.size())
					freeHostess.add(id);
			}
		}
		if (freeHostess.isEmpty())
			return result;

		String query = "select * from HOSTESS d,PERSONS p  where d.HOSTESS_ID in ("
				+ freeHostess.stream().map(i -> i.toString()).collect(Collectors.joining(","))
				+ ") AND d.PERSON_ID=p.PERSON_ID";

		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Person person = new Person(resultSet.getInt(PersonConsts.ID), resultSet.getString(PersonConsts.NAME),
						resultSet.getString(PersonConsts.SURNAME));
				result.add(person);
			}
		} catch (SQLException ex) {
			System.err.println("Selecting free hostess failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return result;
	}

	public void insertCourse(Course courseData, List<Person> staff) {
		String query = "insert into COURSES(COURSE_ID,TIME_TABLE_POSITION_ID,BUS_ID,COURSE_DATE,STATUS,BUSY_SITS) "
				+ "values(COURSE_SEQ.NEXTVAL,?,?,?,'EMPTY',0)";
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			statement.setInt(counter++, courseData.getTimeTablePosition().getId());
			statement.setInt(counter++, courseData.getBus().getBusId());
			statement.setTimestamp(counter++, new Timestamp(courseData.getDate().getTimeInMillis()));
			statement.executeUpdate();

			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COURSE_SEQ.CURRVAL FROM dual");
			int generatedId = -1;
			if (rs != null && rs.next())
				generatedId = rs.getInt(1);
			for (Person person : staff) {
				courseData.setId(generatedId);
				insertStaffForCourse(person.getId(), courseData);
			}
		} catch (SQLException ex) {
			System.err.println("Inserting course failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	private void insertStaffForCourse(int staffMemberId, Course courseData) {
		String query = "insert into STAFF_COURSE_MEMBERS(COURSE_ID,PERSON_ID) " + "values(" + courseData.getId() + ","
				+ staffMemberId + ")";
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			statement.executeQuery(query);
		} catch (SQLException ex) {
			System.err.println("Inserting staff failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		int driverId = getDriverIdFromPersonId(staffMemberId);
		if (driverId != -1) {
			splitGraphicForDriver(driverId, courseData);
			return;
		}
		int hostessId = getHostessIdFromPersonId(staffMemberId);
		if (hostessId != -1)
			splitGraphicForHostess(hostessId, courseData);
	}

	private void splitGraphicForHostess(int hostessId, Course courseData) {
		GraphicPosition graphicPositionFromLeaving = getGraphicPositionsFromLeavingDate(
				courseData.getDate().getTimeInMillis()).stream()
						.filter(gp -> gp.getHostessId() == hostessId && gp.getStatus().equals("FREE")).findFirst()
						.get();

		Calendar dateArriving = Calendar.getInstance();
		dateArriving.setTimeInMillis(courseData.getDate().getTimeInMillis());
		dateArriving.add(Calendar.MINUTE, courseData.getCourseDrive().getTime());

		GraphicPosition graphicPositionFromArriving = getGraphicPositionsFromArrivingDate(
				dateArriving.getTimeInMillis()).stream()
						.filter(gp -> gp.getHostessId() == hostessId && gp.getStatus().equals("FREE")).findFirst()
						.get();

		List<GraphicPosition> graphicPositionsStrictInsideCourseTime = getGraphicPositionsStrictInsideCourseTime(
				courseData.getDate().getTimeInMillis(), dateArriving.getTimeInMillis()).stream()
						.filter(gp -> gp.getHostessId() == hostessId && gp.getStatus().equals("FREE"))
						.collect(Collectors.toList());

		insertGraphicPosition(-1, hostessId, -1, graphicPositionFromLeaving.getTimeFrom().getTimeInMillis(),
				courseData.getDate().getTimeInMillis(), graphicPositionFromLeaving.getHourlyBid());
		insertGraphicPosition(-1, hostessId, courseData.getId(), courseData.getDate().getTimeInMillis(),
				dateArriving.getTimeInMillis(), graphicPositionFromLeaving.getHourlyBid());
		insertGraphicPosition(-1, hostessId, -1, dateArriving.getTimeInMillis(),
				graphicPositionFromArriving.getTimeTo().getTimeInMillis(), graphicPositionFromLeaving.getHourlyBid());
		if (graphicPositionFromLeaving.getId() == graphicPositionFromArriving.getId()) {
			deleteGraphicPosition(graphicPositionFromLeaving.getId());
		} else if (graphicPositionsStrictInsideCourseTime.isEmpty()) {
			deleteGraphicPosition(graphicPositionFromLeaving.getId());
			deleteGraphicPosition(graphicPositionFromArriving.getId());
		} else {
			deleteGraphicPosition(graphicPositionFromLeaving.getId());
			deleteGraphicPosition(graphicPositionFromArriving.getId());
			graphicPositionsStrictInsideCourseTime.forEach(gp -> deleteGraphicPosition(gp.getId()));
		}
	}

	private void splitGraphicForDriver(int driverId, Course courseData) {
		GraphicPosition graphicPositionFromLeaving = getGraphicPositionsFromLeavingDate(
				courseData.getDate().getTimeInMillis()).stream()
						.filter(gp -> gp.getDriverId() == driverId && gp.getStatus().equals("FREE")).findFirst().get();

		Calendar dateArriving = Calendar.getInstance();
		dateArriving.setTimeInMillis(courseData.getDate().getTimeInMillis());
		dateArriving.add(Calendar.MINUTE, courseData.getCourseDrive().getTime());

		GraphicPosition graphicPositionFromArriving = getGraphicPositionsFromArrivingDate(
				dateArriving.getTimeInMillis()).stream()
						.filter(gp -> gp.getDriverId() == driverId && gp.getStatus().equals("FREE")).findFirst().get();

		List<GraphicPosition> graphicPositionsStrictInsideCourseTime = getGraphicPositionsStrictInsideCourseTime(
				courseData.getDate().getTimeInMillis(), dateArriving.getTimeInMillis()).stream()
						.filter(gp -> gp.getDriverId() == driverId && gp.getStatus().equals("FREE"))
						.collect(Collectors.toList());

		insertGraphicPosition(driverId, -1, -1, graphicPositionFromLeaving.getTimeFrom().getTimeInMillis(),
				courseData.getDate().getTimeInMillis(), graphicPositionFromLeaving.getHourlyBid());
		insertGraphicPosition(driverId, -1, courseData.getId(), courseData.getDate().getTimeInMillis(),
				dateArriving.getTimeInMillis(), graphicPositionFromLeaving.getHourlyBid());
		insertGraphicPosition(driverId, -1, -1, dateArriving.getTimeInMillis(),
				graphicPositionFromArriving.getTimeTo().getTimeInMillis(), graphicPositionFromLeaving.getHourlyBid());
		if (graphicPositionFromLeaving.getId() == graphicPositionFromArriving.getId()) {
			deleteGraphicPosition(graphicPositionFromLeaving.getId());
		} else if (graphicPositionsStrictInsideCourseTime.isEmpty()) {
			deleteGraphicPosition(graphicPositionFromLeaving.getId());
			deleteGraphicPosition(graphicPositionFromArriving.getId());
		} else {
			deleteGraphicPosition(graphicPositionFromLeaving.getId());
			deleteGraphicPosition(graphicPositionFromArriving.getId());
			graphicPositionsStrictInsideCourseTime.forEach(gp -> deleteGraphicPosition(gp.getId()));
		}
	}

	private void deleteGraphicPosition(int id) {
		String query = "delete from GRAPHIC_POSITIONS where GRAPHIC_POSITION_ID=" + id;
		Connection c = null;
		try {
			c = createConnection();
			Statement statement = c.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException ex) {
			System.err.println("Deleting graphic position failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	private void insertGraphicPosition(int driverId, int hostessId, int courseId, long from, long to, float hourlyBid) {
		String query = null;
		if (courseId != -1) {
			if (driverId != -1)
				query = "INSERT INTO GRAPHIC_POSITIONS(GRAPHIC_POSITION_ID,DRIVER_ID,COURSE_ID"
						+ ",TIME_FROM,TIME_TO,STATUS,HOURLY_BID) " + "VALUES(GRAPHIC_POSITIONS_SEQ.NEXTVAL," + driverId
						+ ",?" + ",?,?,?,?)";
			else
				query = "INSERT INTO GRAPHIC_POSITIONS(GRAPHIC_POSITION_ID,HOSTESS_ID,COURSE_ID"
						+ ",TIME_FROM,TIME_TO,STATUS,HOURLY_BID) " + "VALUES(GRAPHIC_POSITIONS_SEQ.NEXTVAL," + hostessId
						+ ",?" + ",?,?,?,?)";
		} else {
			if (driverId != -1)
				query = "INSERT INTO GRAPHIC_POSITIONS(GRAPHIC_POSITION_ID,DRIVER_ID"
						+ ",TIME_FROM,TIME_TO,STATUS,HOURLY_BID) " + "VALUES(GRAPHIC_POSITIONS_SEQ.NEXTVAL," + driverId
						+ ",?,?,?,?)";
			else
				query = "INSERT INTO GRAPHIC_POSITIONS(GRAPHIC_POSITION_ID,HOSTESS_ID"
						+ ",TIME_FROM,TIME_TO,STATUS,HOURLY_BID) " + "VALUES(GRAPHIC_POSITIONS_SEQ.NEXTVAL," + hostessId
						+ ",?,?,?,?)";
		}
		Connection c = null;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			if (courseId != -1)
				statement.setInt(counter++, courseId);
			// statement.setDate(counter++, new Date(from));
			// statement.setDate(counter++, new Date(to));
			statement.setTimestamp(counter++, new Timestamp(from));
			statement.setTimestamp(counter++, new Timestamp(to));
			if (courseId != -1)
				statement.setString(counter++, "AT COURSE");
			else
				statement.setString(counter++, "FREE");
			statement.setFloat(counter++, hourlyBid);
			statement.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("Inserting graphic position failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}
	}

	private int getDriverIdFromPersonId(int staffMemberId) {
		String query = "select DRIVER_ID from DRIVERS where PERSON_ID=?";
		Connection c = null;
		int driverId = -1;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			statement.setInt(1, staffMemberId);
			ResultSet rs = statement.executeQuery();

			if (rs.next())
				driverId = rs.getInt("DRIVER_ID");

		} catch (SQLException ex) {
			System.err.println("Getting drvier from person id failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return driverId;
	}

	private int getHostessIdFromPersonId(int staffMemberId) {
		String query = "select HOSTESS_ID from HOSTESS where PERSON_ID=?";
		Connection c = null;
		int driverId = -1;
		try {
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			statement.setInt(1, staffMemberId);
			ResultSet rs = statement.executeQuery();

			if (rs.next())
				driverId = rs.getInt("HOSTESS_ID");

		} catch (SQLException ex) {
			System.err.println("Getting hostess from person id failed.\n" + ex.getSQLState());
		} finally {
			endConnection(c);
		}

		return driverId;
	}
}
