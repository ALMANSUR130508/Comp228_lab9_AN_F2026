package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {

    private String url = "jdbc:mysql://localhost:3306/hospital_db";
    private String user = "root";
    private String pass = "class123";

    public void add(Patient patient) throws SQLException {

        String sql = "INSERT INTO patients(name, age, gender, phone, diagnosis, admission_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, patient.getName());
            statement.setInt(2, patient.getAge());
            statement.setString(3, patient.getGender());
            statement.setString(4, patient.getPhone());
            statement.setString(5, patient.getDiagnosis());
            statement.setDate(6, Date.valueOf(patient.getAdmissionDate()));

            statement.executeUpdate();
        }
    }

    public List<Patient> getAll() throws SQLException {

        String sql = "SELECT * FROM patients ORDER BY patient_id DESC";

        List<Patient> patients = new ArrayList<>();

        try (
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                patients.add(mapPatient(resultSet));
            }
        }

        return patients;
    }

    public void update(Patient patient) throws SQLException {

        String sql = "UPDATE patients "
                + "SET name=?, age=?, gender=?, phone=?, diagnosis=?, admission_date=? "
                + "WHERE patient_id=?";

        try (
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, patient.getName());
            statement.setInt(2, patient.getAge());
            statement.setString(3, patient.getGender());
            statement.setString(4, patient.getPhone());
            statement.setString(5, patient.getDiagnosis());
            statement.setDate(6, Date.valueOf(patient.getAdmissionDate()));
            statement.setInt(7, patient.getPatientId());

            statement.executeUpdate();
        }
    }

    public void delete(int patientId) throws SQLException {

        String sql = "DELETE FROM patients WHERE patient_id=?";

        try (
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);
            statement.executeUpdate();
        }
    }

    public List<Patient> search(String text) throws SQLException {

        String sql = "SELECT * FROM patients WHERE name LIKE ?";

        List<Patient> patients = new ArrayList<>();

        try (
            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, "%" + text + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                patients.add(mapPatient(resultSet));
            }
        }

        return patients;
    }

    private Patient mapPatient(ResultSet resultSet) throws SQLException {

        return new Patient(
                resultSet.getInt("patient_id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                resultSet.getString("gender"),
                resultSet.getString("phone"),
                resultSet.getString("diagnosis"),
                resultSet.getDate("admission_date").toLocalDate()
        );
    }
}