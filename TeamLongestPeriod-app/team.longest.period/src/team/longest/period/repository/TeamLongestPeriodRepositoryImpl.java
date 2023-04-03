package team.longest.period.repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import team.longest.period.model.EmployeeProject;

public class TeamLongestPeriodRepositoryImpl implements TeamLongestPeriodRepository {
	
	private final String filePath;

    public TeamLongestPeriodRepositoryImpl(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<EmployeeProject> getAllEmployeeProjects(String dateFormat) {
    	
    	// Create list which will save records in case of successful file read
        List<EmployeeProject> employeeProjects = new ArrayList<>();
        
        // Read the file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String empId = values[0].trim().toString();
                String projectId = values[1].trim().toString();
                LocalDate dateFrom = LocalDate.parse(values[2].trim(), formatter);
                LocalDate dateTo = values[3].trim().equals("NULL") ? LocalDate.now() : LocalDate.parse(values[3].trim(), formatter);
                employeeProjects.add(new EmployeeProject(empId, projectId, dateFrom, dateTo));
            }
        } catch (Exception e) {
            System.err.println("There is an error while reading employee project data from file: " + e.getMessage());
        }
        return employeeProjects;
    }

}
