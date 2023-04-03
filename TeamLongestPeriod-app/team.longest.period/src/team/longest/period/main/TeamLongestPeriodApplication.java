package team.longest.period.main;

import java.io.File;
import java.util.Scanner;

import team.longest.period.repository.TeamLongestPeriodRepository;
import team.longest.period.repository.TeamLongestPeriodRepositoryImpl;

public class TeamLongestPeriodApplication {
	
    public static void main(String[] args) {
        
    	// Get file path
        File file = new File(TeamLongestPeriodApplication.class.getResource("employee-records-second-format.csv").getFile());
        String filePath = file.getAbsolutePath();
        
        // All data formats can be set with console UI 
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the date format string (e.g. yyyy-MM-dd): ");
        String dateFormat = scanner.nextLine();
        scanner.close();
        
        // Set the repository with data from the "csv" file (comma separated)        
        TeamLongestPeriodRepository repository = new TeamLongestPeriodRepositoryImpl(filePath);        
        System.out.println("employeeProjects: " + repository.getAllEmployeeProjects(dateFormat));
    }

}
