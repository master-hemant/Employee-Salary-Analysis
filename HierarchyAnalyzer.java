import java.io.*;
import java.util.*;

class Employee {
    int id;
    String firstName;
    String lastName;
    double salary;
    int managerId;
    
    Employee(int id, String firstName, String lastName, double salary, int managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }
}

public class HierarchyAnalyzer {
    private Map<Integer, Employee> employees = new HashMap<>();
    private Map<Integer, List<Integer>> hierarchy = new HashMap<>();
    private int ceoId = -1;
    
    public void loadCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine(); // Skip header
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0].trim());
            String firstName = parts[1].trim();
            String lastName = parts[2].trim();
            double salary = Double.parseDouble(parts[3].trim());
            int managerId = parts[4].trim().isEmpty() ? -1 : Integer.parseInt(parts[4].trim());
            
            Employee emp = new Employee(id, firstName, lastName, salary, managerId);
            employees.put(id, emp);
            
            if (managerId == -1) {
                ceoId = id;
            } else {
                hierarchy.putIfAbsent(managerId, new ArrayList<>());
                hierarchy.get(managerId).add(id);
            }
        }
        br.close();
    }

    public void findSalaryDiscrepancies() {
        for (Map.Entry<Integer, List<Integer>> entry : hierarchy.entrySet()) {
            int managerId = entry.getKey();
            Employee manager = employees.get(managerId);
            List<Integer> subordinates = entry.getValue();
            
            double avgSubSalary = 0;
            for (int subId : subordinates) {
                avgSubSalary += employees.get(subId).salary;
            }
            avgSubSalary /= subordinates.size();
            
            if (manager.salary < avgSubSalary * 1.2) {
                System.out.println("Underpaid Manager: " + manager.firstName + " " + manager.lastName);
            }
            if (manager.salary > avgSubSalary * 1.5) {
                System.out.println("Overpaid Manager: " + manager.firstName + " " + manager.lastName);
            }
        }
    }

    public void checkReportingDepth() {
        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> depth = new HashMap<>();
        
        queue.add(ceoId);
        depth.put(ceoId, 0);
        
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            int currDepth = depth.get(curr);
            
            if (hierarchy.containsKey(curr)) {
                for (int sub : hierarchy.get(curr)) {
                    depth.put(sub, currDepth + 1);
                    queue.add(sub);
                    
                    if (currDepth + 1 > 4) {
                        System.out.println("Deep Reporting Employee: " + employees.get(sub).firstName + " " + employees.get(sub).lastName + " at depth " + (currDepth + 1));
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        HierarchyAnalyzer analyzer = new HierarchyAnalyzer();
        analyzer.loadCSV("employees.csv");
        analyzer.findSalaryDiscrepancies();
        analyzer.checkReportingDepth();
    }
}
