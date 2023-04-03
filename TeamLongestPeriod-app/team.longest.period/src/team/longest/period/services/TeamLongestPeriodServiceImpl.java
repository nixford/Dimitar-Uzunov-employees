package team.longest.period.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

		// 1) Get all employee projects from the repository using the specified date
		// format
		List<EmployeeProject> employeeProjects = repository.getAllEmployeeProjects();

		// 2) Create map to store the projects each employee has worked on
		Map<String, List<String>> employeeProjectsMap = new HashMap<>();
		for (EmployeeProject employeeProject : employeeProjects) {
			String empId = employeeProject.getEmpId();
			String projectId = employeeProject.getProjectId();
			if (!employeeProjectsMap.containsKey(empId)) {
				employeeProjectsMap.put(empId, new ArrayList<>());
			}
			employeeProjectsMap.get(empId).add(projectId);
		}

		// 3) Create map to store the common projects between each pair of employees
		Map<String, Map<String, LocalDate[]>> commonProjectsMap = new HashMap<>();

		// Find common projects between all pairs of employees and their simultaneous
		// periods
		for (int i = 0; i < employeeProjects.size(); i++) {
			for (int j = i + 1; j < employeeProjects.size(); j++) {
				EmployeeProject empProj1 = employeeProjects.get(i);
				EmployeeProject empProj2 = employeeProjects.get(j);

				if (empProj1.getProjectId().equals(empProj2.getProjectId())) {
					String empId1 = empProj1.getEmpId();
					String empId2 = empProj2.getEmpId();

					LocalDate startDate = empProj1.getDateFrom().isBefore(empProj2.getDateFrom())
							? empProj2.getDateFrom()
							: empProj1.getDateFrom();
					LocalDate endDate = empProj1.getDateTo() == null || empProj2.getDateTo() == null ? LocalDate.now()
							: (empProj1.getDateTo().isBefore(empProj2.getDateTo()) ? empProj1.getDateTo()
									: empProj2.getDateTo());

					int days = (int) (endDate.toEpochDay() - startDate.toEpochDay());

					String pairKey = empId1 + "_" + empId2;
					String reversePairKey = empId2 + "_" + empId1;

					if (commonProjectsMap.containsKey(pairKey)) {
						Map<String, LocalDate[]> projectPeriods = commonProjectsMap.get(pairKey);
						if (projectPeriods.containsKey(empProj1.getProjectId())) {
							LocalDate[] period = projectPeriods.get(empProj1.getProjectId());
							if (startDate.isBefore(period[1]) && endDate.isAfter(period[0])) {
								period[0] = startDate.isAfter(period[0]) ? startDate : period[0];
								period[1] = endDate.isBefore(period[1]) ? endDate : period[1];
							}
						} else {
							projectPeriods.put(empProj1.getProjectId(), new LocalDate[] { startDate, endDate });
						}
					} else if (commonProjectsMap.containsKey(reversePairKey)) {
						Map<String, LocalDate[]> projectPeriods = commonProjectsMap.get(reversePairKey);
						if (projectPeriods.containsKey(empProj1.getProjectId())) {
							LocalDate[] period = projectPeriods.get(empProj1.getProjectId());
							if (startDate.isBefore(period[1]) && endDate.isAfter(period[0])) {
								period[0] = startDate.isAfter(period[0]) ? startDate : period[0];
								period[1] = endDate.isBefore(period[1]) ? endDate : period[1];
							}
						} else {
							projectPeriods.put(empProj1.getProjectId(), new LocalDate[] { startDate, endDate });
						}
						commonProjectsMap.put(pairKey, projectPeriods);
						commonProjectsMap.remove(reversePairKey);
					} else {
						Map<String, LocalDate[]> projectPeriods = new HashMap<>();
						projectPeriods.put(empProj1.getProjectId(), new LocalDate[] { startDate, endDate });
						commonProjectsMap.put(pairKey, projectPeriods);
					}
				}
			}
		}

		System.out.println("commonProjectsMap: " + commonProjectsMap);

		// 4) Find the pair of employees with the longest common project
		List<String> longestPairs = new ArrayList<>();
		int maxDays = 0; // It will store maximum number of days two employees have worked together
		for (String pair : commonProjectsMap.keySet()) {
			Map<String, LocalDate[]> commonProjects = commonProjectsMap.get(pair);
			LocalDate startDate = null;
			LocalDate endDate = null;
			boolean foundProject = false;
			for (EmployeeProject employeeProject : employeeProjects) {
				if (commonProjects.containsKey(employeeProject.getProjectId())) {
					LocalDate projectStartDate = employeeProject.getDateFrom();
					LocalDate projectEndDate = employeeProject.getDateTo() == null ? LocalDate.now()
							: employeeProject.getDateTo();
					if (startDate == null || projectStartDate.isAfter(startDate)) {
						startDate = projectStartDate;
					}
					if (endDate == null || projectEndDate.isBefore(endDate)) {
						endDate = projectEndDate;
					}
					foundProject = true;
				}
			}
			if (foundProject) {
				int days = (int) (endDate.toEpochDay() - startDate.toEpochDay());
				if (days > maxDays) {
					longestPairs.clear(); // clear the previous longest pair(s)
					longestPairs.add(pair); // add the new longest pair
					maxDays = days;
				} else if (days == maxDays) {
					longestPairs.add(pair); // add another longest pair
				}
			}
		}

		System.out.println("longestPair: " + longestPairs);

		// 5) Return the IDs of the employees in the longest pair

		return longestPairs;

	}
}
