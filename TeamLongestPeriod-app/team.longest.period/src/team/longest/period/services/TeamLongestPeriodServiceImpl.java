package team.longest.period.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import team.longest.period.model.EmployeeProject;
import team.longest.period.repository.TeamLongestPeriodRepository;

public class TeamLongestPeriodServiceImpl implements TeamLongestPeriodService {

	private final TeamLongestPeriodRepository repository;

	public TeamLongestPeriodServiceImpl(TeamLongestPeriodRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<String> getEmployeesWithLongestCommonProject() {

		// 1) Get all employee projects from the repository
		List<EmployeeProject> employeeProjects = repository.getAllEmployeeProjects();

		// 2) Create hash map with all the projectIds as keys and List<EmployeeProject>
		HashMap<String, List<EmployeeProject>> multipleEmployeesProjectsMap = new HashMap<>();
		for (EmployeeProject employeeProject : employeeProjects) {
			String projectId = employeeProject.getProjectId();		
			if (!multipleEmployeesProjectsMap.containsKey(projectId)) {
				multipleEmployeesProjectsMap.put(projectId, new ArrayList<>());
			}	
			multipleEmployeesProjectsMap.get(projectId).add(employeeProject);
		}
		
		// 3) Find all records with more than one employee working on project
		multipleEmployeesProjectsMap.entrySet().removeIf(entry -> entry.getValue().size() <= 1);		

		// 4) Find all projects where employees have worked simultaneously (in same period) for this project 
		// (so their period overlap, according to the requirement: "pair of employees who have worked together on common projects")
		HashMap<String, List<EmployeeProject>> employeeSimultaneouslyWorkOnProjectsMap = new HashMap<>();
		for (Map.Entry<String, List<EmployeeProject>> entry : multipleEmployeesProjectsMap.entrySet()) {
			
		    String projectId = entry.getKey();
		    List<EmployeeProject> employees = entry.getValue();
		    
		    for (int i = 0; i < employees.size(); i++) {
		        EmployeeProject employee1 = employees.get(i);
		        LocalDate dateFrom1 = employee1.getDateFrom();
		        LocalDate dateTo1 = employee1.getDateTo();
		        
		        for (int j = i+1; j < employees.size(); j++) {
		            EmployeeProject employee2 = employees.get(j);
		            LocalDate dateFrom2 = employee2.getDateFrom();
		            LocalDate dateTo2 = employee2.getDateTo();
		            
		            if (dateFrom1.isBefore(dateTo2) && dateFrom2.isBefore(dateTo1)) {
		                // the date ranges overlap, add both employees to the map
		                if (!employeeSimultaneouslyWorkOnProjectsMap.containsKey(projectId)) {
		                    employeeSimultaneouslyWorkOnProjectsMap.put(projectId, new ArrayList<>());
		                }
		                employeeSimultaneouslyWorkOnProjectsMap.get(projectId).add(employee1);
		                employeeSimultaneouslyWorkOnProjectsMap.get(projectId).add(employee2);
		            }
		        }
		    }
		}		

		// 5) Find the total time working together
		long longestOverlap = 0;
	    String[] longestOverlapPair = new String[2];
		 for (Entry<String, List<EmployeeProject>> entry : employeeSimultaneouslyWorkOnProjectsMap.entrySet()) {
		        List<EmployeeProject> projects = entry.getValue();
		        
		        for (int i = 0; i < projects.size(); i++) {
		            for (int j = i + 1; j < projects.size(); j++) {
		                EmployeeProject project1 = projects.get(i);
		                EmployeeProject project2 = projects.get(j);
		                
		                if (project1.getDateFrom().isBefore(project2.getDateTo()) && project1.getDateTo().isAfter(project2.getDateFrom())) {
		                    long overlap = ChronoUnit.DAYS.between(
		                        project1.getDateFrom().atStartOfDay(), 
		                        project2.getDateTo().atStartOfDay()
		                    );
		                    
		                    if (overlap > longestOverlap) {
		                        longestOverlap = overlap;
		                        longestOverlapPair[0] = project1.getEmpId();
		                        longestOverlapPair[1] = project2.getEmpId();
		                    }
		                }
		            }
		        }
		    }
		

		 // 6) Return the IDs of the employees in the longest pair		 
		 List<String> longestPair = new ArrayList<>();
		 longestPair.add(longestOverlapPair[0]);
		 longestPair.add(longestOverlapPair[1]);

		return longestPair;

	}
	

}
