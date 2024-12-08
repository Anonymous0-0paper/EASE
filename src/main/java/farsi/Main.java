package farsi;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import farsi.base.Algorithm;
import farsi.base.Edge;
import farsi.base.Task;
import farsi.experiment.EdgeTemplate;
import farsi.experiment.Experiment;
import farsi.experiment.TaskTemplate;
import org.cloudsimplus.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main() throws Exception {

        Log.setLevel(Level.ERROR);

        int iterations = 10;
        int[][] configs = new int[][]{
                // Edges, Tasks
                {50, 50}, {50, 100}, {50, 200}, {50, 300}, {50, 400}, {50, 500}, {50, 600}, {50, 700}, {50, 800}, {50, 900}, {50, 1000},
                {10, 500}, {20, 500}, {30, 500}, {40, 500}, {50, 500}, {60, 500}, {70, 500}, {80, 500}, {90, 500}, {100, 500},
        };

        Class<Algorithm>[] algorithms = new Class[]{EVO.class, PSO.class, IBGWO.class, PIMR.class};

        Experiment[] experiments = new Experiment[configs.length * algorithms.length];
        long total = iterations * configs.length * algorithms.length;
        long current = 0;

        for (int[] config : configs) {

            int edges_count = config[0];
            int tasks_count = config[1];

            Experiment[] experimentPerAlgorithm = new Experiment[algorithms.length];
            for (int j = 0; j < algorithms.length; j++) {
                String name = edges_count + "-" + tasks_count + "-" + algorithms[j].getSimpleName();
                experimentPerAlgorithm[j] = Experiment.builder().name(name).utilization(new double[edges_count]).build();
            }

            for (int i = 0; i < iterations; i++) {
                List<EdgeTemplate> edgeTemplates = createEdgeTemplates(edges_count);
                List<TaskTemplate> taskTemplates = createTaskTemplates(tasks_count);

                for (int j = 0; j < algorithms.length; j++) {
                    current += 1;
                    printProgress(current, total, experimentPerAlgorithm[j].getName());

                    List<Edge> edges = createEdges(edgeTemplates);
                    List<Task> tasks = createTasks(taskTemplates);

                    Constructor<Algorithm> constructor = algorithms[j].getConstructor(List.class, List.class);
                    Algorithm object = constructor.newInstance(edges, tasks);
                    object.run();

                    experimentPerAlgorithm[j].add(object.output());
                }
            }

            for (int j = 0; j < algorithms.length; j++) {
                experimentPerAlgorithm[j].normalize(iterations);
                experiments[(int) current / iterations - (algorithms.length - j)] = experimentPerAlgorithm[j];
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("result.json"), experiments);
    }

    public static List<EdgeTemplate> createEdgeTemplates(int edges_count) {
        List<EdgeTemplate> edges = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < edges_count; i++) {
            int mips = random.nextInt(1000, 3000);
            double staticPower = random.nextDouble(2.5, 25);
            double dynamicPower = random.nextDouble(2.5, 25);
            long ram = random.nextInt(100000, 200000);

            EdgeTemplate template = EdgeTemplate.builder()
                    .id((long) i).pes(4).mips(mips).staticPower(staticPower).dynamicPower(dynamicPower).ram(ram)
                    .build();
            edges.add(template);
        }

        return edges;
    }

    public static List<TaskTemplate> createTaskTemplates(int tasks_count) {
        List<TaskTemplate> tasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < tasks_count; i++) {
            int deadline = random.nextInt(10, 100);
            int length = random.nextInt(10000, 30000);

            TaskTemplate template = TaskTemplate.builder()
                    .id((long) i).deadline(deadline).length(length)
                    .build();
            tasks.add(template);
        }

        return tasks;
    }

    public static List<Edge> createEdges(List<EdgeTemplate> edgeTemplates) {
        List<Edge> edges = new ArrayList<>();

        for (EdgeTemplate template : edgeTemplates) {
            edges.add(new Edge(template));
        }

        return edges;
    }

    public static List<Task> createTasks(List<TaskTemplate> taskTemplates) {
        List<Task> tasks = new ArrayList<>();

        for (TaskTemplate template : taskTemplates) {
            tasks.add(new Task(template));
        }

        return tasks;
    }

    private static void printProgress(long current, long total, String label) {
        int progressBarLength = 100;
        int progress = (int) ((double) current / total * progressBarLength);

        StringBuilder bar = new StringBuilder();
        bar.append("\r[");

        for (int i = 0; i < progressBarLength; i++) {
            if (i < progress) {
                bar.append("=");
            } else {
                bar.append(" ");
            }
        }

        bar.append("] ").append(current * 100 / total).append("%");
        bar.append(" | ").append(label);

        System.out.print(bar);
    }
}
