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

		


		// 4) Find the pair of employees with the longest common project
		List<String> longestPairs = new ArrayList<>();

		// System.out.println("longestPair: " + longestPairs);

		// 5) Return the IDs of the employees in the longest pair

		return longestPairs;

	}
}
