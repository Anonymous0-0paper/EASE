package farsi;

import farsi.base.Algorithm;
import farsi.base.Edge;
import farsi.base.Task;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PIMR extends Algorithm {
    private final Map<Long, Long> mapper;
    private final int Num_Particles = 10;
    private final int Max_Iteration = 100;
    private final double W = 0.2;
    private final double C1 = 1;
    private final double C2 = 1;
    private final double Mutation_Probability = 0.1;

    public PIMR(List<Edge> edges, List<Task> tasks) {
        super(edges, tasks);
        this.mapper = new HashMap<>();
        mapping();
        super.createBroker(mapper);
    }

    public void mapping() {
        List<Particle> particles = new ArrayList<>();
        for (int i = 0; i < Num_Particles; i++) {
            Particle particle = new Particle(this.getEdges(), this.getTasks());
            particles.add(particle);
        }

        double globalBestFitness = Double.MAX_VALUE;
        int[] globalBestPosition = new int[this.getTasks().size()];

        for (int i = 0; i < Max_Iteration; i++) {
            for (Particle particle : particles) {
                double fitness = particle.calculateFitness();
                if (globalBestFitness > fitness) {
                    globalBestFitness = fitness;
                    System.arraycopy(particle.getPosition(), 0, globalBestPosition, 0, particle.position.length);
                }
            }

            for (Particle particle : particles) {
                particle.setGlobalBestPosition(globalBestPosition);
                particle.move();
                particle.applyImbalancedMutation();
            }
        }

        for (int i = 0; i < globalBestPosition.length; i++) {
            Task task = getTasks().get(i);
            Edge edge = getEdges().get(globalBestPosition[i]);
            this.mapper.put(task.getId(), edge.getId());
        }
    }

    class Particle {
        private List<Edge> edges;
        private List<Task> tasks;

        @Getter
        private int[] position;
        private double[] velocity;
        private int[] bestPosition;
        private double bestFitness;

        @Setter
        private int[] globalBestPosition;

        public Particle(List<Edge> edges, List<Task> tasks) {
            this.edges = edges;
            this.tasks = tasks;
            position = new int[tasks.size()];
            velocity = new double[tasks.size()];
            bestPosition = new int[tasks.size()];
            bestFitness = Double.MAX_VALUE;

            Random random = new Random();
            for (int i = 0; i < position.length; i++) {
                position[i] = random.nextInt(edges.size());
                velocity[i] = random.nextDouble() * 2 - 1;
                bestPosition[i] = position[i];
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
                System.arraycopy(position, 0, bestPosition, 0, position.length);
            }

            return fitness;
        }

        public void move() {
            Random random = new Random();
            for (int d = 0; d < position.length; d++) {
                velocity[d] = W * velocity[d] +
                        C1 * random.nextDouble() * (bestPosition[d] - position[d]) +
                        C2 * random.nextDouble() * (globalBestPosition[d] - position[d]);

                position[d] = (int) Math.round(position[d] + velocity[d]);
                position[d] = position[d] % this.edges.size();
                if (position[d] < 0) position[d] += this.edges.size();
            }
        }

        public void applyImbalancedMutation() {
            Random random = new Random();
            // Classification tasks based on edges
            List<List<Task>> edgeTasks = new ArrayList<>();
            for (int i = 0; i < this.edges.size(); i++) {
                edgeTasks.add(new ArrayList<>());
            }
            for (int i = 0; i < this.position.length; i++) {
                edgeTasks.get(this.position[i]).add(this.tasks.get(i));
            }

            for (int i = 0; i < position.length; i++) {
                if (random.nextDouble() < Mutation_Probability) {
                    Edge edge = edges.get(position[i]);
                    double completionTime = edge.calculatingCompletionTime(tasks.get(i), edgeTasks.get(position[i]));
                    double capacityRatio = (tasks.get(i).getDeadline() - completionTime) / completionTime;
                    if (random.nextDouble() < capacityRatio) {
                        position[i] = random.nextInt(edges.size());
                    }
                }
            }
        }
    }
}
