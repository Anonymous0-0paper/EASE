package farsi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import farsi.experiment.Experiment;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class Export {
    public static void main() throws Exception {

        // must be sync with configs in Main class
        int[][] task_configs = new int[][]{
                {50, 50}, {50, 100}, {50, 200}, {50, 300}, {50, 400}, {50, 500}, {50, 600}, {50, 700}, {50, 800}, {50, 900}, {50, 1000},
        };
        int[][] edge_configs = new int[][]{
                {10, 500}, {20, 500}, {30, 500}, {40, 500}, {50, 500}, {60, 500}, {70, 500}, {80, 500}, {90, 500}, {100, 500},
        };
        String[] algorithms = new String[]{"EVO", "PSO", "IBGWO", "PIMR"};

        // load results
        ObjectMapper mapper = new ObjectMapper();
        List<Experiment> experimentsRaw = mapper.readValue(new File("result.json"), new TypeReference<>() {
        });
        HashMap<String, Experiment> experiments = new HashMap<>();
        for (Experiment experiment : experimentsRaw)
            experiments.put(experiment.getName(), experiment);

        Workbook workbook = new XSSFWorkbook();

        energy(workbook, experiments, task_configs, edge_configs, algorithms);
        completionTime(workbook, experiments, task_configs, edge_configs, algorithms);
        successrate(workbook, experiments, task_configs, edge_configs, algorithms);
        utilization(workbook, experiments, task_configs, edge_configs, algorithms);


        FileOutputStream fileOut = new FileOutputStream("result.xlsx");
        workbook.write(fileOut);
    }

    private static void energy(Workbook workbook, HashMap<String, Experiment> experiments,
                               int[][] task_configs, int[][] edge_configs, String[] algorithms) {

        Sheet sheet = workbook.createSheet("Energy");

        {
            // write header
            int row = 0;
            int column = 0;
            for (int[] config : task_configs) {
                column += 1;
                write(sheet, row, column, config[1]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : task_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    write(sheet, row, column, experiments.get(name).getEnergy());
                }
            }
        }
        {
            // write header
            int row = 20;
            int column = 0;
            for (int[] config : edge_configs) {
                column += 1;
                write(sheet, row, column, config[0]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : edge_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    write(sheet, row, column, experiments.get(name).getEnergy());
                }
            }
        }
    }

    private static void successrate(Workbook workbook, HashMap<String, Experiment> experiments,
                                    int[][] task_configs, int[][] edge_configs, String[] algorithms) {

        Sheet sheet = workbook.createSheet("SuccessRate");

        {
            // write header
            int row = 0;
            int column = 0;
            for (int[] config : task_configs) {
                column += 1;
                write(sheet, row, column, config[1]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : task_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    double successrate = experiments.get(name).getSuccessTaskCount() / experiments.get(name).getTaskCount();
                    write(sheet, row, column, successrate);
                }
            }
        }
        {
            // write header
            int row = 20;
            int column = 0;
            for (int[] config : edge_configs) {
                column += 1;
                write(sheet, row, column, config[0]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : edge_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    double successrate = experiments.get(name).getSuccessTaskCount() / experiments.get(name).getTaskCount();
                    write(sheet, row, column, successrate);
                }
            }
        }
    }

    private static void completionTime(Workbook workbook, HashMap<String, Experiment> experiments,
                                       int[][] task_configs, int[][] edge_configs, String[] algorithms) {

        Sheet sheet = workbook.createSheet("CompletionTime");

        {
            // write header
            int row = 0;
            int column = 0;
            for (int[] config : task_configs) {
                column += 1;
                write(sheet, row, column, config[1]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : task_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    write(sheet, row, column, experiments.get(name).getTotalTime());
                }
            }
        }
        {
            // write header
            int row = 20;
            int column = 0;
            for (int[] config : edge_configs) {
                column += 1;
                write(sheet, row, column, config[0]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : edge_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    write(sheet, row, column, experiments.get(name).getTotalTime());
                }
            }
        }
    }

    private static void utilization(Workbook workbook, HashMap<String, Experiment> experiments,
                                    int[][] task_configs, int[][] edge_configs, String[] algorithms) {

        Sheet sheet = workbook.createSheet("Utilization");

        {
            // write header
            int row = 0;
            int column = 0;
            for (int[] config : task_configs) {
                column += 1;
                write(sheet, row, column, config[1]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : task_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    double utilization = 0;
                    for (double u : experiments.get(name).getUtilization())
                        utilization += u;
                    utilization = utilization / config[1];
                    write(sheet, row, column, utilization);
                }
            }
        }
        {
            // write header
            int row = 20;
            int column = 0;
            for (int[] config : edge_configs) {
                column += 1;
                write(sheet, row, column, config[0]);
            }

            for (String algorithm : algorithms) {
                row += 1;
                column = 0;
                write(sheet, row, column, algorithm);
                for (int[] config : edge_configs) {
                    column += 1;
                    String name = getConfigName(config, algorithm);
                    double utilization = 0;
                    for (double u : experiments.get(name).getUtilization())
                        utilization += u;
                    utilization = utilization / config[1];
                    write(sheet, row, column, utilization);
                }
            }
        }
    }

    private static String getConfigName(int[] config, String algorithm) {
        return config[0] + "-" + config[1] + "-" + algorithm;
    }

    private static void write(Sheet sheet, int row, int column, String value) {

        Row rowE = sheet.getRow(row);
        if (rowE == null)
            rowE = sheet.createRow(row);
        Cell cell = rowE.getCell(column);
        if (cell == null)
            cell = rowE.createCell(column);
        cell.setCellValue(value);
    }

    private static void write(Sheet sheet, int row, int column, double value) {

        Row rowE = sheet.getRow(row);
        if (rowE == null)
            rowE = sheet.createRow(row);
        Cell cell = rowE.getCell(column);
        if (cell == null)
            cell = rowE.createCell(column);
        cell.setCellValue(value);
    }
}
