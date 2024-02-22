import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class Employee {
    private String name;
    private int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        // user ko name variable ka direct nhi de rha hu encapsulation -iss class ke
        // bahar name ko directly access nhi kar sakhte
        return name;
    }

    public int getId() {
        // user ko name variable ka direct nhi de rha hu
        return id;
    }

    public abstract double calculateSalary();

    // @Override
    // public String toString() {
    // return "Employee [name=" + name + ", id=" + id + ", salary=" +
    // calculateSalary() + "]";
    // }
}

class FullTimeEmployee extends Employee {
    private double monthlySalary;

    public FullTimeEmployee(String name, int id, double monthlySalary) {
        super(name, id);//
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}

class PartTimeEmployee extends Employee {
    private int hoursWorked;
    private double hourlyRate;

    public PartTimeEmployee(String name, int id, int hoursWorked, double hourlyRate) {
        super(name, id);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }
}

class PayRollSystem {
    public Connection conn;

    public PayRollSystem() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/payrollmanagement", "root", "");
            if (conn != null) {
                System.out.println("Successfully connected");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addEmployee(Employee employee) {
        // Check if the employee already exists based on ID or name
        boolean employeeExists = checkEmployeeExists(employee.getId());
        
        if (!employeeExists) {
            // Employee doesn't exist, proceed with adding
            try {
                // Insert the employee into the database
                String query = "INSERT INTO EMPLOYEE (id, name) VALUES (?, ?)";
                PreparedStatement preparedStment = conn.prepareStatement(query);
                preparedStment.setInt(1, employee.getId());
                preparedStment.setString(2, employee.getName());
                preparedStment.execute();
            } catch (SQLException e) {
                System.out.println(e);
            }
        } 
        else 
        {
            System.out.println("Employee already exists.");
        }
    }
    private boolean checkEmployeeExists(int id) {
        boolean exists = false;
        try {
            String query = "SELECT COUNT(*) FROM EMPLOYEE WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                exists = count > 0;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return exists;
    }
    public void removeEmployee(int id) {

        boolean employeeExists = checkEmployeeExists(id);
        if (employeeExists) {
            try{
                String sql="DELETE FROM EMPLOYEE WHERE ID="+id;
                PreparedStatement preparedStment = conn.prepareStatement(sql);

                preparedStment.execute();
                preparedStment.close();
            }catch(SQLException e)
            {
                System.out.println(e);
            }
        }
        else
        {
            System.out.println("Employee doesn't exists");
        }
    }

    public void displayEmployees() {
        try {

            java.sql.Statement statement = conn.createStatement();

            // Execute a SELECT query
            String query = "SELECT * FROM EMPLOYEE";
            ResultSet resultSet = statement.executeQuery(query);

            // Process the results
            while (resultSet.next()) {
                // Retrieve data from each row
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                // Process retrieved data as needed
                System.out.println("ID: " + id + " Name: " + name);
            }

            // Close resources
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("****************EmployeePayroll Management System**************");
        PayRollSystem payRollSystem = new PayRollSystem();
        FullTimeEmployee emp1 = new FullTimeEmployee("piyush", 1, 70000.0);
        PartTimeEmployee emp2 = new PartTimeEmployee("sarvesh", 2, 40, 100);

        payRollSystem.addEmployee(emp1);
        payRollSystem.addEmployee(emp2);
        System.out.println("Initial Employee Details: ");
        payRollSystem.displayEmployees();
        System.out.println("Removing employees");
        payRollSystem.removeEmployee(2);
        System.out.println("Remaining Employee Details: ");
        payRollSystem.displayEmployees();
        payRollSystem.addEmployee(emp1);
        System.out.println("Removing employees");
        payRollSystem.removeEmployee(2);

        // calculate salary dono employee method body alag hai -polymorphism
    }
}
