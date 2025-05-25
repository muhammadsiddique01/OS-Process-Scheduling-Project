import java.util.*;

public class Scheduler {
    private List<Process> processes;
    private double averageWaitingTime;
    private double averageTurnaroundTime;

    public Scheduler(List<Process> processes) {
        this.processes = processes;
    }

    public void fcfs() {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process process : processes) {
            if (currentTime < process.getArrivalTime()) {
                currentTime = process.getArrivalTime();
            }
            process.setStartTime(currentTime);
            currentTime += process.getBurstTime();
            process.setFinishTime(currentTime);

            int turnaroundTime = process.getFinishTime() - process.getArrivalTime();
            int waitingTime = turnaroundTime - process.getBurstTime();
            process.setTurnaroundTime(turnaroundTime);
            process.setWaitingTime(waitingTime);
            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;
        }

        averageWaitingTime = (double) totalWaitingTime / processes.size();
        averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();
    }

public void sjfPreemptive() {
    List<Process> readyQueue = new ArrayList<>(processes);
    int currentTime = 0;
    int totalWaitingTime = 0;
    int totalTurnaroundTime = 0;

    while (!readyQueue.isEmpty()) {
        Process shortestJob = null;
        int shortestBurstTime = Integer.MAX_VALUE;

        for (Process p : readyQueue) {
            if (p.getArrivalTime() <= currentTime && p.getBurstTime() < shortestBurstTime && p.getBurstTime() > 0) {
                shortestJob = p;
                shortestBurstTime = p.getBurstTime();
            }
        }

        if (shortestJob != null) {
            // If a process is selected, execute it until it finishes
            shortestJob.setStartTime(currentTime);
            int remainingTime = shortestJob.getBurstTime();
            currentTime += remainingTime;
            shortestJob.setFinishTime(currentTime);

            int turnaroundTime = shortestJob.getFinishTime() - shortestJob.getArrivalTime();
            int waitingTime = turnaroundTime - shortestJob.getBurstTime();
            shortestJob.setTurnaroundTime(turnaroundTime);
            shortestJob.setWaitingTime(waitingTime);
            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;

            readyQueue.remove(shortestJob);
        } else {
            currentTime++;
        }
    }

    // Calculate average waiting time and turnaround time
    averageWaitingTime = (double) totalWaitingTime / processes.size();
    averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();

}

    public void priorityPreemptive() {
        int n = processes.size();
        int[] burstRemaining = new int[n];
        int[] isCompleted = new int[n];

        for (int i = 0; i < n; i++) {
            burstRemaining[i] = processes.get(i).getBurstTime();
            isCompleted[i] = 0;
        }

        int currentTime = 0;
        int completed = 0;
        int prev = 0;

        while (completed != n) {
            int idx = -1;
            int highestPriority = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (processes.get(i).getArrivalTime() <= currentTime && isCompleted[i] == 0) {
                    if (processes.get(i).getPriority() < highestPriority) {
                        highestPriority = processes.get(i).getPriority();
                        idx = i;
                    }
                    if (processes.get(i).getPriority() == highestPriority) {
                        if (processes.get(i).getArrivalTime() < processes.get(idx).getArrivalTime()) {
                            highestPriority = processes.get(i).getPriority();
                            idx = i;
                        }
                    }
                }
            }

            if (idx != -1) {
                if (burstRemaining[idx] == processes.get(idx).getBurstTime()) {
                    processes.get(idx).setStartTime(currentTime);
                }
                burstRemaining[idx] -= 1;
                currentTime++;
                prev = currentTime;

                if (burstRemaining[idx] == 0) {
                    processes.get(idx).setFinishTime(currentTime);
                    processes.get(idx).setTurnaroundTime(processes.get(idx).getFinishTime() - processes.get(idx).getArrivalTime());
                    processes.get(idx).setWaitingTime(processes.get(idx).getTurnaroundTime() - processes.get(idx).getBurstTime());
                    processes.get(idx).setResponseTime(processes.get(idx).getStartTime() - processes.get(idx).getArrivalTime());

                    averageTurnaroundTime += processes.get(idx).getTurnaroundTime();
                    averageWaitingTime += processes.get(idx).getWaitingTime();

                    isCompleted[idx] = 1;
                    completed++;
                }
            } else {
                currentTime++;
            }
        }

        averageWaitingTime /= n;
        averageTurnaroundTime /= n;
    }

public void roundRobinWithPriority(int timeQuantum) {
        Queue<Process> readyQueue = new LinkedList<>(processes);
        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        while (!readyQueue.isEmpty()) {
            Process currentProcess = readyQueue.poll();
            int waitingTime = 0; // Track waiting time for the process in this iteration

            if (currentProcess.getBurstTime() > timeQuantum) {
                // Process still has burst time remaining after time quantum
                currentTime += timeQuantum;
                currentProcess.setBurstTime(currentProcess.getBurstTime() - timeQuantum);
                readyQueue.add(currentProcess); // Add process back to ready queue
                waitingTime += timeQuantum; // Process waited in ready queue for time quantum
            } else {
                // Process finishes within time quantum
                currentTime += currentProcess.getBurstTime();
                currentProcess.setFinishTime(currentTime);
                int turnaroundTime = currentProcess.getFinishTime() - currentProcess.getArrivalTime();
                waitingTime = currentProcess.getFinishTime() - currentProcess.getBurstTime() - currentProcess.getArrivalTime();
                currentProcess.setTurnaroundTime(turnaroundTime);
                currentProcess.setWaitingTime(waitingTime);
                totalTurnaroundTime += turnaroundTime;
                totalWaitingTime += waitingTime;
            }
        }

     averageWaitingTime = (double) totalWaitingTime / processes.size();
averageTurnaroundTime = (double) totalTurnaroundTime / processes.size();

    }



    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public List<Process> getProcesses() {
        return processes;
    }
}
