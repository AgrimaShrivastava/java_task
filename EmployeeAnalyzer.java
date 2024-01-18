package corejava;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Employee {
    String name;
    String position;
    List<Date> workDays;

    // Constructor with parameters
    public Employee(String name, String position) {
        this.name = name;
        this.position = position;
        this.workDays = new ArrayList<>();
    }

    // Getter methods for name and position
    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    // Getter method for workDays
    public List<Date> getWorkDays() {
        return workDays;
    }
}


public class EmployeeAnalyzer {

	// Updated date format for parsing date-time from the input file
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    public static void main(String[] args) {
        // Provide the path to your input file
        String filePath = "C:\\Users\\91964\\Desktop\\Assignment_Timecard.xlsx - Sheet1.csv";

        try {
            List<Employee> employees = processFile(filePath);

            // Analyze and print results
            analyzeAndPrintResults(employees);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // ... (rest of the code remains unchanged)

    private static List<Employee> processFile(String filePath) throws IOException, ParseException {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {  // Ensure there are enough elements in the array
                    String name = data[8]; // Assuming file number is at index 8 (Note: arrays are zero-based)
                    String position = data[1]; // Assuming position status is at index 1
                    String dateString = data[2]; // Assuming time is at index 2

                    // Check if the date string is not empty
                    if (!dateString.isEmpty()) {
                        Date workTime = dateFormat.parse(dateString);
                        Employee employee = findEmployee(employees, name, position);
                        employee.workDays.add(workTime);
                    }
                }
            }
        }

        return employees;
    }



    private static Employee findEmployee(List<Employee> employees, String name, String position) {
        for (Employee employee : employees) {
            if (employee.getName().equals(name) && employee.getPosition().equals(position)) {
                return employee;
            }
        }

        // If not found, create a new Employee instance
        Employee newEmployee = new Employee(name, position);
        employees.add(newEmployee);
        return newEmployee;
    }

    private static void analyzeAndPrintResults(List<Employee> employees) {
    	System.out.println("Employees who worked for 7 consecutive days :");
        for (Employee employee : employees) {
            if (hasWorkedForConsecutiveDays(employee, 7)) {
                System.out.println("Name: "+employee.name + " Position: "+ employee.position);
            }
        }   
        
        System.out.println("\n--------------------------------------");
    	System.out.println("Employees who has less than 10 hours between shifts but greater than 1 hour: ");
        for (Employee employee : employees)
        {
        	if (hasLessThanHoursBetweenShifts(employee, 10) && hasMoreThanHoursBetweenShifts(employee, 1)) {
                System.out.println("Name: "+employee.name + " Position: " + employee.position);
            }	
        }

        System.out.println("\n--------------------------------------");
    	System.out.println("Employees who has worked for more than 14 hours in a single shift: ");
        for (Employee employee : employees)
        {
        	if (hasWorkedMoreThanHoursInSingleShift(employee, 14)) {
                System.out.println("Name: "+employee.name + " Position: " + employee.position);
            }
        }

            
        
    }

    private static boolean hasWorkedForConsecutiveDays(Employee employee, int consecutiveDays) {
        Collections.sort(employee.workDays);
        Set<String> uniqueDates = new HashSet<>();

        for (Date workTime : employee.workDays) {
            uniqueDates.add(dateFormat.format(workTime));
        }

        return uniqueDates.size() >= consecutiveDays;
    }

    private static boolean hasLessThanHoursBetweenShifts(Employee employee, int hours) {
        return hasHoursBetweenShifts(employee, hours, true);
    }

    private static boolean hasMoreThanHoursBetweenShifts(Employee employee, int hours) {
        return hasHoursBetweenShifts(employee, hours, false);
    }

    private static boolean hasHoursBetweenShifts(Employee employee, int hours, boolean isLessThan) {
        Collections.sort(employee.workDays);

        for (int i = 0; i < employee.workDays.size() - 1; i++) {
            long hoursBetween = (employee.workDays.get(i + 1).getTime() - employee.workDays.get(i).getTime()) / (60 * 60 * 1000);
            if (isLessThan) {
                if (hoursBetween < hours) {
                    return true;
                }
            } else {
                if (hoursBetween > hours) {
                    return true;
                }
            }
        }

        return false;
    }


    private static boolean hasWorkedMoreThanHoursInSingleShift(Employee employee, int hours) {
        for (int i = 0; i < employee.workDays.size() - 1; i++) {
            long hoursWorked = (employee.workDays.get(i + 1).getTime() - employee.workDays.get(i).getTime()) / (60 * 60 * 1000);
            if (hoursWorked > hours) {
                return true;
            }
        }

        return false;
    }
}
