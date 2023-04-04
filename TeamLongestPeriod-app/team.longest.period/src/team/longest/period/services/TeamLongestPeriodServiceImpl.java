package team.longest.period.services;

import java.time.LocalDate;
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

		System.out.println("employeeProjects: " + employeeProjects);

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

		System.out.println("multipleEmployeesProjectsMap: " + multipleEmployeesProjectsMap);

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
		
		System.out.println("employeeSimultaneouslyWorkOnProjectsMap: " + employeeSimultaneouslyWorkOnProjectsMap);


		// 4) Find the pair of employees with the longest common project
		List<String> longestPairs = new ArrayList<>();

		// System.out.println("longestPair: " + longestPairs);

		// 5) Return the IDs of the employees in the longest pair

		return longestPairs;

	}
	

}
