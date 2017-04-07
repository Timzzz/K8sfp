package org.k8sfp.encog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.arrayutil.VectorWindow;
import org.encog.util.csv.ReadCSV;
import org.k8sfp.data.K8sCommonDataElement;
import org.k8sfp.interfaces.IK8sDataElement;
import org.k8sfp.interfaces.IK8sDataElementTimeseries;
import org.k8sfp.interfaces.IK8sDataSource;
import org.k8sfp.interfaces.IK8sPredictor;
import org.k8sfp.interfaces.IK8sTimeSeriesPredictor;
import org.k8sfp.interfaces.special.IK8sVersatileDataSource;

public class EncogTimeSeriesPredictor implements IK8sTimeSeriesPredictor {

    private IK8sDataSource dataSource;
    private final EncogTimeSeriesPredictorConfig conf;
    private static final int WINDOW_SIZE = 30;

    private String fieldName = "cpu_usage_total";

    public EncogTimeSeriesPredictor(EncogTimeSeriesPredictorConfig encogTimeSeriesPredictorConfig) {
        this.conf = encogTimeSeriesPredictorConfig;
    }

    public List<IK8sDataElement> predict(Date begin, Date end, int count) {
        List<IK8sDataElement> dp = dataSource.getData();
        for(IK8sDataElement _it : dp) {
            IK8sDataElementTimeseries it = (IK8sDataElementTimeseries)_it;
            it.getColumns().remove("_DATE");
            it.getColumns().put("col2", it.getColumns().get(fieldName));
        }
        ((IK8sVersatileDataSource)dataSource).setCurrentData(dp);   // omit date field, ensure 2 columns are present

        List<IK8sDataElement> res = new ArrayList<IK8sDataElement>();
        VersatileDataSource source = (VersatileDataSource) dataSource;
        VersatileMLDataSet data = new VersatileMLDataSet(source);

        ColumnDefinition columnSSN = data.defineSourceColumn(fieldName,
                ColumnType.continuous);
        ColumnDefinition columnDEV = data.defineSourceColumn("col2",
                ColumnType.continuous);

        // Analyze the data, determine the min/max/mean/sd of every column.
        data.analyze();

        data.defineInput(columnSSN);
        data.defineInput(columnDEV);
        data.defineOutput(columnSSN);

        EncogModel model = new EncogModel(data);
        model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

        model.setReport(new ConsoleStatusReportable());

        data.normalize();

        data.setLeadWindowSize(1);
        data.setLagWindowSize(WINDOW_SIZE);

        model.holdBackValidation(0.3, false, 1001);
        model.selectTrainingType(data);
        MLRegression bestMethod = (MLRegression) model.crossvalidate(5,
                false);
        System.out.println("Training error: "
                + model.calculateError(bestMethod,
                        model.getTrainingDataset()));
        System.out.println("Validation error: "
                + model.calculateError(bestMethod,
                        model.getValidationDataset()));

        NormalizationHelper helper = data.getNormHelper();
        System.out.println(helper.toString());

        // Display the final model.
        System.out.println("Final model: " + bestMethod);

        source.rewind();
        String[] line = new String[2];

        double[] slice = new double[2];
        VectorWindow window = new VectorWindow(WINDOW_SIZE + 1);
        MLData input = helper.allocateInputVector(WINDOW_SIZE + 1);

        for (int i = 0; i < WINDOW_SIZE + 1; ++i) {
            int idx = (WINDOW_SIZE + 1 - i);
            IK8sDataElementTimeseries e = (IK8sDataElementTimeseries)dp.get(idx);
            String[] arr = new String[e.getColumns().size()];
            arr = e.getColumns().values().toArray(arr);
            helper.normalizeInputVector(arr, slice, false);
            window.add(slice);
            System.out.println("arr: " + arr[0] + "; " + arr[1]);
        }
        if (!window.isReady()) {
            throw new IllegalStateException();
        }
        int predCount = count;
        for (int i = 0; i < predCount; ++i) {
            window.copyWindow(input.getData(), 0);
            MLData output = bestMethod.compute(input);
            String predicted0 = helper.denormalizeOutputVectorToString(output)[0];

            System.out.println("predicted: " + predicted0);
            window.add(output.getData());
            IK8sDataElement element = new K8sCommonDataElement(null, fieldName, predicted0);
            res.add(element);
        }
        return res;
    }

    public void addDataSource(IK8sDataSource source) {
        this.dataSource = source;
    }

    public List<IK8sDataElement> predict(int beginIndex, int amount, int forecastCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
