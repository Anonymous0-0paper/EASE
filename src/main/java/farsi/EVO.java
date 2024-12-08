package farsi;

import farsi.base.Algorithm;
import farsi.base.Edge;
import farsi.base.Task;
import lombok.Getter;

import java.util.*;

public class EVO extends Algorithm {
    private final Map<Long, Long> mapper;

    private final int Max_Iteration = 1000;
    private final double EB = 0.5;
    private final double SB = 0.7;

    public EVO(List<Edge> edges, List<Task> tasks) {
        super(edges, tasks);

        this.mapper = new HashMap<>();
        mapping();
        super.createBroker(mapper);
    }

    public void mapping() {
        // sort edges based on Mips
        List<Edge> edges = sortEdges(this.getEdges());

        // Create particles
        List<Particle> particles = new ArrayList<>();

        for (Task task : this.getTasks()) {
            Particle particle = new Particle(task, edges);
            particles.add(particle);
        }

        // Randomly assign tasks to edge devices
        for (Particle particle : particles) {
            particle.initialize();
        }

        double maxFitnessValue = Double.MIN_VALUE;
        double minFitnessValue = Double.MAX_VALUE;
        double bestFitnessValue = Double.MAX_VALUE;
        int bestFitnessX = 0;

        for (int i = 0; i < Max_Iteration; i++) {

            double minNelValue = Double.MAX_VALUE;
            double maxNelValue = Double.MIN_VALUE;
            double minSlValue = Double.MAX_VALUE;
            double maxSlValue = Double.MIN_VALUE;

            // Calculate fitness of the tasks
            for (Particle particle : particles) {
                double fitnessValue = particle.fitnessFunction(particles, maxFitnessValue);

                minFitnessValue = Math.min(minFitnessValue, fitnessValue);
                maxFitnessValue = Math.max(maxFitnessValue, fitnessValue);

                // Find best x
                if (bestFitnessValue > minFitnessValue) {
                    bestFitnessValue = minFitnessValue;
                    bestFitnessX = particle.getSelectedEdgeIndex();
                }
            }

            // Update global best x
            for (Particle particle : particles) {
                particle.updateBestX(bestFitnessX);
            }


            for (Particle particle : particles) {
                double nelValue = particle.nelCalculating(maxFitnessValue, minFitnessValue);

                minNelValue = Math.min(minNelValue, nelValue);
                maxNelValue = Math.max(maxNelValue, nelValue);
            }

            for (Particle particle : particles) {
                double slValue = particle.slCalculating(maxNelValue, minNelValue);

                minSlValue = Math.min(minSlValue, slValue);
                maxSlValue = Math.max(maxSlValue, slValue);
            }

            for (Particle particle : particles) {
                particle.selectEdge();
            }
        }

        // Store final mapping
        for (Particle particle : particles) {
            this.mapper.put(particle.getTask().getId(), particle.getEdge().getId());
        }
    }

    private List<Edge> sortEdges(List<Edge> edges) {
        List<Edge> newEdges = new ArrayList<>(edges);
        newEdges.sort(Comparator.comparingDouble(e -> e.getVm().getMips()));

        return newEdges;
    }

    class Particle {

        @Getter
        private Task task;
        private List<Edge> edges;

        @Getter
        private int selectedEdgeIndex;
        private int bestEdgeIndex;
        private double fitnessValue;
        private double nelValue;
        private double slValue;

        public Particle(Task task, List<Edge> edges) {
            this.task = task;
            this.edges = edges;
            this.selectedEdgeIndex = -1;
        }

        public void initialize() {
            this.selectedEdgeIndex = new Random().nextInt(edges.size());
        }

        public Edge getEdge() {
            return this.edges.get(selectedEdgeIndex);
        }

        public void updateBestX(int bestX) {
            this.bestEdgeIndex = bestX;
        }

        public double fitnessFunction(List<Particle> particles, double maxFitness) {
            List<Task> edgeTasks = this.getEdgeTasks(particles);
            double energy = getEdge().calculatingEnergy(this.task, edgeTasks);
            double completionTime = getEdge().calculatingCompletionTime(this.task, edgeTasks);
            this.fitnessValue = (0.001 * energy + completionTime);
            if (completionTime > getTask().getDeadline())
                this.fitnessValue = maxFitness;
            return fitnessValue;
        }

        public List<Task> getEdgeTasks(List<Particle> particles) {
            List<Task> edgeTasks = new ArrayList<>();
            for (Particle particle : particles) {
                if (particle.selectedEdgeIndex == this.selectedEdgeIndex)
                    edgeTasks.add(particle.getTask());
            }
            return edgeTasks;
        }

        public double nelCalculating(double maxFitnessValue, double minFitnessValue) {
            double divisor = maxFitnessValue - minFitnessValue;
            this.nelValue = (fitnessValue - minFitnessValue) / divisor;
            return nelValue;
        }

        public double slCalculating(double maxNelValue, double minNelValue) {
            double divisor = maxNelValue - minNelValue;
            this.slValue = (nelValue - minNelValue) / divisor;
            return slValue;
        }

        public void selectEdge() {
            Random random = new Random();

            if (nelValue > EB) {
                if (slValue > SB) {
                    if (random.nextBoolean()) {
                        performAlphaDecay();
                    } else {
                        performGammaDecay();
                    }
                } else {
                    performBetaDecay();
                }
            } else {
                performRandomMovement();
            }
        }

        private void performAlphaDecay() {
            int xBest = getXBest();
            int distance = xBest - this.selectedEdgeIndex;
            int xNew = (int) Math.round((double) this.selectedEdgeIndex + Math.random() * 2 * distance);
            this.selectedEdgeIndex = boundX(xNew);
        }

        private void performGammaDecay() {
            int xNeighbor = getXNeighbor();
            int distance = xNeighbor - this.selectedEdgeIndex;
            int xNew = (int) Math.round((double) this.selectedEdgeIndex + Math.random() * 2 * distance);
            this.selectedEdgeIndex = boundX(xNew);
        }

        private void performBetaDecay() {
            int xCenter = getXCenter();
            int distance = xCenter - this.selectedEdgeIndex;
            int xNew = (int) Math.round((double) this.selectedEdgeIndex + Math.random() * 2 * distance);
            this.selectedEdgeIndex = boundX(xNew);
        }

        private void performRandomMovement() {
            int xNew = (int) Math.round((double) this.selectedEdgeIndex + (Math.random() - 0.5) * 2);
            this.selectedEdgeIndex = boundX(xNew);
        }

        private int getXBest() {
            return this.bestEdgeIndex;
        }

        private int getXNeighbor() {
            int neighborIndex = this.selectedEdgeIndex;

            if ((new Random().nextBoolean() || neighborIndex == 0) && neighborIndex != this.edges.size())
                neighborIndex += 1;
            else if (this.edges.size() != 1)
                neighborIndex -= 1;

            return neighborIndex;
        }

        private int getXCenter() {
            return this.edges.size() / 2;
        }

        private int boundX(int x) {
            x = x % this.edges.size();
            if (x < 0)
                x += this.edges.size();
            return x;
        }
    }
}
