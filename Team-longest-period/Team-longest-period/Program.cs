using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;

namespace Team_longest_period
{
    class Program
    {
        static void Main(string[] args)
        {
            
            Dictionary<string, Dictionary<string, int>> lineData =
                    new Dictionary<string, Dictionary<string, int>>();

            // Takes all files from directory with extention .txt
            string extention = ".txt";
            string path = @"../../../Files";
            string selectedFileName = "";
            string[] fileNames = Directory.GetFiles(path, $"*{extention}*");           

            for (int i = 0; i < fileNames.Length; i++)
            {
                string currFileName = fileNames[i]
                    .Split(separator: new string[] { @"\" },
                    StringSplitOptions.RemoveEmptyEntries)[1];

                Console.WriteLine($"No: {i + 1} -> {currFileName}");
            }

            Console.WriteLine("Enter a file number to proceed: ");
            int inputFileNum = int.Parse(Console.ReadLine());
            selectedFileName = fileNames[inputFileNum - 1]
                .Split(separator: new string[] { @"\" },
                StringSplitOptions.RemoveEmptyEntries)[1];


            string[] lines = File.ReadAllLines($"{path}/{selectedFileName}");

            foreach (var line in lines)
            {
                string[] currLine = line
                .Split(separator: new string[] { ", " },
                StringSplitOptions.RemoveEmptyEntries);

                string emplID = currLine[1];
                string projectID = currLine[0];
                string dateFrom = currLine[2];
                string dateTo = currLine[3];

                // Set the data separated by projectID in Dictionary
                if (!lineData.ContainsKey(projectID))
                {
                    lineData.Add(projectID, new Dictionary<string, int>());
                }

                // Take date difference (total time for each employee) in days
                DateTime startDate;
                DateTime.TryParse(dateFrom,
                   CultureInfo.InvariantCulture,
                   DateTimeStyles.None,
                   out startDate);
                DateTime endDate;
                bool isEndDateValid = DateTime.TryParse(dateTo,
                   CultureInfo.InvariantCulture,
                   DateTimeStyles.None,
                   out endDate);

                if (isEndDateValid == false)
                {
                    endDate = DateTime.UtcNow;
                }

                int diff = (int)Math.Floor((endDate - startDate).TotalDays);

                // The code logic assumes that the EmpID exists only one time per ProjectID - otherwise
                // new changes and cheks has to be made 
                lineData[projectID].Add(emplID, diff);
            }

            StringBuilder sb = new StringBuilder();

            if (lineData.Values.Count == 0)
            {
                Console.Write("No information in the selected file");
                return;
            }

            // Takes only projects which has more that two employees
            foreach (var (k, nestedDict) in lineData
                .Where(d => d.Value.Count > 1)
                .OrderByDescending(d => d.Value.Count))
            {
                sb.AppendLine($"ProjectID: {k}:");

                sb.AppendLine($"The employee couple worked for the longest time on this project is:");
                int count = 0;
                foreach (var kvp in nestedDict.OrderByDescending(d => d.Value))
                {
                    if (count == 0)
                    {
                        sb.Append($"EmpID: {kvp.Key} - for: {kvp.Value} days ");
                    }
                    else
                    {
                        sb.Append($"and EmpID: {kvp.Key} - for: {kvp.Value} days");
                        sb.Append(Environment.NewLine);
                        break;
                    }

                    count++;
                }
            }

            Console.WriteLine(sb.ToString().TrimEnd());
        }
    }
}
