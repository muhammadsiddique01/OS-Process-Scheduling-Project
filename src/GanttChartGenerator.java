import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GanttChartGenerator extends JPanel {
    private List<Process> processes;
    private static final int BAR_HEIGHT = 30;
    private static final int BAR_WIDTH_PER_UNIT = 20; // Adjust this value to change the width of each time unit

    public GanttChartGenerator(List<Process> processes) {
        this.processes = processes;
        setPreferredSize(new Dimension(800, 250)); // Adjust the initial size of the panel
    }

  @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    int yPosition = 50;
    int currentTime = 0;

    for (Process process : processes) {
        int startTime = Math.max(currentTime, process.getArrivalTime());
        int endTime = startTime + process.getBurstTime();
        int barWidth = (endTime - startTime) * BAR_WIDTH_PER_UNIT; // Adjust width based on time duration

        // Draw process bar
        g.setColor(Color.BLUE);
        g.fillRect(startTime * BAR_WIDTH_PER_UNIT, yPosition, barWidth, BAR_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(startTime * BAR_WIDTH_PER_UNIT, yPosition, barWidth, BAR_HEIGHT);

        // Draw process ID
        g.drawString("P" + process.getId(), startTime * BAR_WIDTH_PER_UNIT + 10, yPosition + 20);

        // Draw start and end times
       
        g.drawString(String.valueOf(endTime), (endTime - 1) * BAR_WIDTH_PER_UNIT + 10, yPosition + BAR_HEIGHT + 15);

        // Update current time for the next process
        currentTime = endTime;
    }
}
}