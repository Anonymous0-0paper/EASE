package farsi;

import farsi.base.Algorithm;
import farsi.base.Edge;
import farsi.base.Task;
import lombok.Getter;

import java.util.*;

public class IBGWO extends Algorithm {
    private final Map<Long, Long> mapper;
    private final int Num_Wolves = 10;
    private final int Max_Iteration = 100;

    public IBGWO(List<Edge> edges, List<Task> tasks) {
        super(edges, tasks);
        this.mapper = new HashMap<>();
        mapping();
        super.createBroker(mapper);
    }

    public void mapping() {
        List<Wolf> wolves = new ArrayList<>();
        for (int i = 0; i < Num_Wolves; i++) {
            Wolf wolf = new Wolf(this.getEdges(), this.getTasks());
            wolves.add(wolf);
        }

        double alphaFitness = Double.MAX_VALUE;
        double betaFitness = Double.MAX_VALUE;
        double deltaFitness = Double.MAX_VALUE;

        int[] alphaPosition = new int[this.getTasks().size()];
        int[] betaPosition = new int[this.getTasks().size()];
        int[] deltaPosition = new int[this.getTasks().size()];

        for (int t = 0; t < Max_Iteration; t++) {
            // Identify Alpha, Beta, Delta wolves
            for (Wolf wolf : wolves) {
                double fitness = wolf.calculateFitness();
                if (fitness < alphaFitness) {
                    deltaFitness = betaFitness;
                    deltaPosition = betaPosition.clone();

                    betaFitness = alphaFitness;
                    betaPosition = alphaPosition.clone();

                    alphaFitness = fitness;
                    alphaPosition = wolf.getPosition().clone();
                } else if (fitness < betaFitness) {
                    deltaFitness = betaFitness;
                    deltaPosition = betaPosition.clone();

                    betaFitness = fitness;
                    betaPosition = wolf.getPosition().clone();
                } else if (fitness < deltaFitness) {
                    deltaFitness = fitness;
                    deltaPosition = wolf.getPosition().clone();
                }
            }

            // Move each wolf based on Alpha, Beta, Delta
            for (Wolf wolf : wolves) {
                wolf.move(alphaPosition, betaPosition, deltaPosition, t, Max_Iteration);
            }
        }

        // Map the best solution found to edges and tasks
        for (int i = 0; i < alphaPosition.length; i++) {
            Task task = getTasks().get(i);
            Edge edge = getEdges().get(alphaPosition[i]);
            this.mapper.put(task.getId(), edge.getId());
        }
    }

    class Wolf {
        private List<Edge> edges;
        private List<Task> tasks;

        @Getter
        private int[] position;
        private double bestFitness;

        public Wolf(List<Edge> edges, List<Task> tasks) {
            this.edges = edges;
            this.tasks = tasks;
            position = new int[tasks.size()];
            bestFitness = Double.MAX_VALUE;

            Random random = new Random();
            for (int i = 0; i < position.length; i++) {
                position[i] = random.nextInt(edges.size());
            }
        }

        private double calculateFitness() {
            double totalDelay = 0;

            List<List<Task>> edgeTasks = new ArrayList<>();
            for (int i = 0; i < this.edges.size(); i++) {
                edgeTasks.add(new ArrayList<>());
            }

            for (int i = 0; i < this.position.length; i++) {
                edgeTasks.get(this.position[i]).add(this.tasks.get(i));
            }

            for (int i = 0; i < this.position.length; i++) {
                int edgeIndex = this.position[i];
                Task task = this.tasks.get(i);
                double delay = this.edges.get(edgeIndex).calculatingCompletionTime(task, edgeTasks.get(edgeIndex));

                totalDelay += delay;
            }

            double fitness = totalDelay;
            if (fitness < bestFitness) {
                bestFitness = fitness;
            }

            return fitness;
        }

        public void move(int[] alphaPosition, int[] betaPosition, int[] deltaPosition, int currentIteration, int maxIterations) {
            Random random = new Random();
            double a = 2 - (2 * ((double) currentIteration / maxIterations));

            for (int d = 0; d < position.length; d++) {
                // Calculate the influence of Alpha wolf
                double r1 = random.nextDouble();
                double r2 = random.nextDouble();
                double A1 = 2 * a * r1 - a;
                double C1 = 2 * r2;
                double D_alpha = Math.abs(C1 * alphaPosition[d] - position[d]);
                double X1 = alphaPosition[d] - A1 * D_alpha;

                // Calculate the influence of Beta wolf
                r1 = random.nextDouble();
                r2 = random.nextDouble();
                double A2 = 2 * a * r1 - a;
                double C2 = 2 * r2;
                double D_beta = Math.abs(C2 * betaPosition[d] - position[d]);
                double X2 = betaPosition[d] - A2 * D_beta;

                // Calculate the influence of Delta wolf
                r1 = random.nextDouble();
                r2 = random.nextDouble();
                double A3 = 2 * a * r1 - a;
                double C3 = 2 * r2;
                double D_delta = Math.abs(C3 * deltaPosition[d] - position[d]);
                double X3 = deltaPosition[d] - A3 * D_delta;

                // Update position by averaging the influences
                position[d] = (int) Math.round((X1 + X2 + X3) / 3) % this.edges.size();
                if (position[d] < 0)
                    position[d] += this.edges.size();
            }
        }
    }
}
