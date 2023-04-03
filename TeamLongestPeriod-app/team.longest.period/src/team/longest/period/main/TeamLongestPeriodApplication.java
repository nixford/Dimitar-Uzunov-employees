package team.longest.period.main;

import java.io.File;
import java.util.Scanner;

import team.longest.period.repository.TeamLongestPeriodRepository;
import team.longest.period.repository.TeamLongestPeriodRepositoryImpl;
import team.longest.period.services.TeamLongestPeriodService;
import team.longest.period.services.TeamLongestPeriodServiceImpl;

public class TeamLongestPeriodApplication {
	
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        
    	// The csv files can be set with console UI
        String filePath = null;
        File file = null;
        while (file == null) {
            System.out.println("Enter the file path and file name string (e.g. employee-records.csv): ");
            String filePathEntered = scanner.nextLine();

            try {
                file = new File(TeamLongestPeriodApplication.class.getResource(filePathEntered).getFile());
                filePath = file.getAbsolutePath();
            } catch (NullPointerException e) {
                System.out.println("Invalid file path entered, please try again.");
            }
        }
        
        // All data formats can be set with console UI 
        System.out.println("Enter the date format string (e.g. yyyy-MM-dd): ");
        String dateFormat = scanner.nextLine();
        
        
        scanner.close();
        
        // Set the repository with data from the "csv" file (comma separated)        
        TeamLongestPeriodRepository repository = new TeamLongestPeriodRepositoryImpl(filePath, dateFormat);        
        
        // Get the employees with longest common project
        TeamLongestPeriodService employeeProjectService = new TeamLongestPeriodServiceImpl(repository);
        
        System.out.println("employeeProjectService: " + employeeProjectService.getEmployeesWithLongestCommonProject());
    }

}
