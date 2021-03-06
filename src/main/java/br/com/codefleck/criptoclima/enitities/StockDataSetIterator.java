package br.com.codefleck.criptoclima.enitities;

import com.google.common.collect.ImmutableMap;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StockDataSetIterator implements DataSetIterator {

    /** category and its index */
    private final Map<PriceCategory, Integer> featureMapIndex = ImmutableMap.of(PriceCategory.OPEN, 0, PriceCategory.CLOSE, 1,
            PriceCategory.LOW, 2, PriceCategory.HIGH, 3, PriceCategory.VOLUME, 4);

    private final int VECTOR_SIZE = 6; // number of features for a stock data
    private int miniBatchSize; // mini-batch size
    private int exampleLength; // default 22, say, 22 working days per month
    private int predictLength = 24; // default 1, say, one day ahead prediction

    /** minimal values of each feature in stock dataset */
    private float[] minArray = new float[VECTOR_SIZE];
    /** maximal values of each feature in stock dataset */
    private float[] maxArray = new float[VECTOR_SIZE];

    /** feature to be selected as a training target */
    private PriceCategory category;

    /** mini-batch offset */
    private LinkedList<Integer> exampleStartOffsets = new LinkedList<>();

    /** stock dataset for training */
    private List<CustomStockData> train;

    /** adjusted stock dataset for testing */
    private List<Pair<INDArray, INDArray>> test;

    public StockDataSetIterator (String filename, int miniBatchSize, int exampleLength, double splitRatio, PriceCategory category) {
        List<CustomStockData> stockDataList = readStockDataFromFile(filename);
        this.miniBatchSize = miniBatchSize;
        this.exampleLength = exampleLength;
        this.category = category;
        int split = (int) Math.round(stockDataList.size() * splitRatio);
        train = stockDataList.subList(0, split);
        test = generateTestDataSet(stockDataList.subList(split, stockDataList.size()));
        initializeOffsets();
    }

    /** initialize the mini-batch offsets */
    private void initializeOffsets () {
        exampleStartOffsets.clear();
        int window = exampleLength + predictLength;
        for (int i = 0; i < train.size() - window; i++) { exampleStartOffsets.add(i); }
    }

    public List<Pair<INDArray, INDArray>> getTestDataSet() { return test; }

    public float[] getMaxArray() { return maxArray; }

    public float[] getMinArray() { return minArray; }

    public double getMaxNum (PriceCategory category) { return maxArray[featureMapIndex.get(category)]; }

    public double getMinNum (PriceCategory category) { return minArray[featureMapIndex.get(category)]; }

    public List<CustomStockData> getTrain() {return train; }

    public void setTrain(List<CustomStockData> train) { this.train = train; }

    public void setTest(List<Pair<INDArray, INDArray>> test) { this.test = test; }

    public DataSet next(int num) {
        if (exampleStartOffsets.size() == 0) throw new NoSuchElementException();
        int actualMiniBatchSize = Math.min(num, exampleStartOffsets.size());
        INDArray input = Nd4j.create(new int[] {actualMiniBatchSize, VECTOR_SIZE, exampleLength}, 'f');
        INDArray label;

        if (category.equals(PriceCategory.ALL))
            label = Nd4j.create(new int[] {actualMiniBatchSize, VECTOR_SIZE, exampleLength}, 'f');
        else
            label = Nd4j.create(new int[] {actualMiniBatchSize, predictLength, exampleLength}, 'f');

        for (int index = 0; index < actualMiniBatchSize; index++) {
            int startIdx = exampleStartOffsets.removeFirst();
            int endIdx = startIdx + exampleLength;
            CustomStockData curData = train.get(startIdx);
            CustomStockData nextData;
            for (int i = startIdx; i < endIdx; i++) {
                int c = i - startIdx;
                input.putScalar(new int[] {index, 0, c}, (curData.getOpen() - minArray[0]) / (maxArray[0] - minArray[0]));
                input.putScalar(new int[] {index, 1, c}, (curData.getClose() - minArray[1]) / (maxArray[1] - minArray[1]));
                input.putScalar(new int[] {index, 2, c}, (curData.getLow() - minArray[2]) / (maxArray[2] - minArray[2]));
                input.putScalar(new int[] {index, 3, c}, (curData.getHigh() - minArray[3]) / (maxArray[3] - minArray[3]));
                input.putScalar(new int[] {index, 4, c}, (curData.getVolume() - minArray[4]) / (maxArray[4] - minArray[4]));
                input.putScalar(new int[] {index, 5, c}, (curData.getDailyChangePercentage() - minArray[5]) / (maxArray[5] - minArray[5]));
                nextData = train.get(i + 1);
                if (category.equals(PriceCategory.ALL)) {
                    label.putScalar(new int[] {index, 0, c}, (nextData.getOpen() - minArray[1]) / (maxArray[1] - minArray[1]));
                    label.putScalar(new int[] {index, 1, c}, (nextData.getClose() - minArray[1]) / (maxArray[1] - minArray[1]));
                    label.putScalar(new int[] {index, 2, c}, (nextData.getLow() - minArray[2]) / (maxArray[2] - minArray[2]));
                    label.putScalar(new int[] {index, 3, c}, (nextData.getHigh() - minArray[3]) / (maxArray[3] - minArray[3]));
                    label.putScalar(new int[] {index, 4, c}, (nextData.getVolume() - minArray[4]) / (maxArray[4] - minArray[4]));
                    label.putScalar(new int[] {index, 5, c}, (nextData.getDailyChangePercentage() - minArray[5]) / (maxArray[5] - minArray[5]));
                } else {
                    label.putScalar(new int[]{index, 0, c}, feedLabel(nextData));
                }
                curData = nextData;
            }
            if (exampleStartOffsets.size() == 0) break;
        }
        return new DataSet(input, label);
    }

    private double feedLabel(CustomStockData data) {
        double value;
        switch (category) {
            case OPEN: value = (data.getOpen() - minArray[0]) / (maxArray[0] - minArray[0]); break;
            case CLOSE: value = (data.getClose() - minArray[1]) / (maxArray[1] - minArray[1]); break;
            case LOW: value = (data.getLow() - minArray[2]) / (maxArray[2] - minArray[2]); break;
            case HIGH: value = (data.getHigh() - minArray[3]) / (maxArray[3] - minArray[3]); break;
            case VOLUME: value = (data.getVolume() - minArray[4]) / (maxArray[4] - minArray[4]); break;
            case DAILY_CHANGE_PERC: value = (data.getDailyChangePercentage() - minArray[5]) / (maxArray[5] - minArray[5]); break;
            default: throw new NoSuchElementException();
        }
        return value;
    }

    public int totalExamples() { return train.size() - exampleLength - predictLength; }

    public int inputColumns() { return VECTOR_SIZE; }

    @Override public int totalOutcomes() {
        if (this.category.equals(PriceCategory.ALL)) return VECTOR_SIZE;
        else return predictLength;
    }

    public boolean resetSupported() { return false; }

    public boolean asyncSupported() { return false; }

    public void reset() { initializeOffsets(); }

    public int batch() { return miniBatchSize; }

    public int cursor() { return totalExamples() - exampleStartOffsets.size(); }

    public int numExamples() { return totalExamples(); }

    public void setPreProcessor(DataSetPreProcessor dataSetPreProcessor) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public DataSetPreProcessor getPreProcessor() { throw new UnsupportedOperationException("Not Implemented"); }

    public List<String> getLabels() { throw new UnsupportedOperationException("Not Implemented"); }

    public boolean hasNext() { return exampleStartOffsets.size() > 0; }

    public DataSet next() { return next(miniBatchSize); }

    private List<Pair<INDArray, INDArray>> generateTestDataSet (List<CustomStockData> stockDataList) {
        System.out.println("generating test dataset...");
        int window = exampleLength + predictLength;
        List<Pair<INDArray, INDArray>> test = new ArrayList<>();
        for (int i = 0; i < stockDataList.size() - window; i++) {
            INDArray input = Nd4j.create(new int[] {exampleLength, VECTOR_SIZE}, 'f');
            for (int j = i; j < i + exampleLength; j++) {
                CustomStockData stock = stockDataList.get(j);
                input.putScalar(new int[] {j - i, 0}, (stock.getOpen() - minArray[0]) / (maxArray[0] - minArray[0]));
                input.putScalar(new int[] {j - i, 1}, (stock.getClose() - minArray[1]) / (maxArray[1] - minArray[1]));
                input.putScalar(new int[] {j - i, 2}, (stock.getLow() - minArray[2]) / (maxArray[2] - minArray[2]));
                input.putScalar(new int[] {j - i, 3}, (stock.getHigh() - minArray[3]) / (maxArray[3] - minArray[3]));
                input.putScalar(new int[] {j - i, 4}, (stock.getVolume() - minArray[4]) / (maxArray[4] - minArray[4]));
                input.putScalar(new int[] {j - i, 4}, (stock.getDailyChangePercentage() - minArray[5]) / (maxArray[5] - minArray[5]));
            }
            CustomStockData stock = stockDataList.get(i + exampleLength);
            INDArray label;
            if (category.equals(PriceCategory.ALL)) {
                label = Nd4j.create(new int[]{VECTOR_SIZE}, 'f'); // ordering is set as 'f', faster construct
                label.putScalar(new int[] {0}, stock.getOpen());
                label.putScalar(new int[] {1}, stock.getClose());
                label.putScalar(new int[] {2}, stock.getLow());
                label.putScalar(new int[] {3}, stock.getHigh());
                label.putScalar(new int[] {4}, stock.getVolume());
                label.putScalar(new int[] {5}, stock.getDailyChangePercentage());
            } else {
                label = Nd4j.create(new int[] {1}, 'f');
                switch (category) {
                    case OPEN: label.putScalar(new int[] {0}, stock.getOpen()); break;
                    case CLOSE: label.putScalar(new int[] {0}, stock.getClose()); break;
                    case LOW: label.putScalar(new int[] {0}, stock.getLow()); break;
                    case HIGH: label.putScalar(new int[] {0}, stock.getHigh()); break;
                    case VOLUME: label.putScalar(new int[] {0}, stock.getVolume()); break;
                    case DAILY_CHANGE_PERC: label.putScalar(new int[] {0}, stock.getDailyChangePercentage()); break;
                    default: throw new NoSuchElementException();
                }
            }
            test.add(new Pair<>(input, label));
        }
        return test;
    }

    @SuppressWarnings("resource")
    public List<CustomStockData> readStockDataFromFile (String filename) {
        System.out.println("Reading stock data from file...");
        List<CustomStockData> stockDataList = new ArrayList<>();
        try {
            for (int i = 0; i < maxArray.length; i++) {
                maxArray[i] = Float.MIN_VALUE;
                minArray[i] = Float.MAX_VALUE;
            }
            LineIterator it = FileUtils.lineIterator(new File(filename), "UTF-8");
            List<String[]> list = new ArrayList<>();
            boolean isFileHeader = true;
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    if (isFileHeader) {
                        isFileHeader = false;
                        continue;
                    }
                    String[] splitted = line.split(",");
                    list.add(splitted);
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
            for (String[] arr : list) {
                float[] nums = new float[VECTOR_SIZE];
                for (int i = 0; i < arr.length - 2; i++) {
                    nums[i] = Float.valueOf(arr[i + 2]);
                    if (nums[i] > maxArray[i]) maxArray[i] = nums[i];
                    if (nums[i] < minArray[i]) minArray[i] = nums[i];
                }
                CustomStockData stockData = new CustomStockData(arr[0], arr[1], nums[0], nums[1], nums[2], nums[3], nums[4], Double.valueOf(arr[7]));
                stockDataList.add(stockData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(stockDataList);
        return stockDataList;
    }
}
