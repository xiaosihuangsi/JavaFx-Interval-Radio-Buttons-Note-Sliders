import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class IntervalRadioButtons extends Application {

    private Spinner<Integer> fromNoteSpinner; // Spinner for selecting starting note
    private Spinner<Integer> toNoteSpinner; // Spinner for selecting ending note
    private Button updateButton; // Button to update job
    private TextField jobNameInput; // Text field for entering job name
    private List<RadioButton> intervalButtons; // List of radio buttons for interval selection
    private Job job; // Job object to store job details

    private Slider noteDurationSlider;
    private Label noteDurationLabel;
    private Slider noteDecaySlider;
    private Label noteDecayLabel;
    private Slider noteGapSlider;
    private Label noteGapLabel;
    private Canvas noteTimingCanvas;
    private GridPane parameterGrid;
    private TitledPane noteTimesTitledPane;

    public GridPane getParameterGrid() {
        if (parameterGrid == null) {
            initializeParameters();
        }
        return parameterGrid;
    }

    public TitledPane getNoteTimesTitledPane() {
        if (noteTimesTitledPane == null) {
            initializeParameters();
        }
        return noteTimesTitledPane;
    }

    private void initializeParameters() {
        // Initialize parameterGrid and noteTimesTitledPane here
        // Code for initializing parameterGrid and noteTimesTitledPane remains the same as in the start() method
    }


    @Override
    public void start(Stage stage) {
        this.job = new Job();

        // Setup for IntervalRadioButtons functionality
        IntegerSpinnerValueFactory fromNoteFactory = new IntegerSpinnerValueFactory(0, 127, this.job.getFromNote());
        this.fromNoteSpinner = new Spinner<>(fromNoteFactory);

        // Add listeners for fromNoteSpinner
        this.fromNoteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.job.setFromNote(newValue);
            printJob();
        });

        // Similar setup for toNoteSpinner
        IntegerSpinnerValueFactory toNoteValueFactory = new IntegerSpinnerValueFactory(0, 127, this.job.getToNote());
        this.toNoteSpinner = new Spinner<>(toNoteValueFactory);
        this.toNoteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.job.setToNote(newValue);
            printJob();
        });

        // Code for radio buttons and their functionality
        List<String> intervalButtonNames = new ArrayList<>();
        intervalButtonNames.add("One");
        intervalButtonNames.add("Three");
        intervalButtonNames.add("Six");
        intervalButtonNames.add("Twelve");

        ToggleGroup intervalButtonGroup = new ToggleGroup();
        this.intervalButtons = new ArrayList<>();

        for (int i = 0; i < intervalButtonNames.size(); i++) {
            RadioButton rb = new RadioButton(intervalButtonNames.get(i));
            rb.setToggleGroup(intervalButtonGroup);
            final int index = i;
            rb.setOnAction(event -> handleIntervalButtonAction(index));
            this.intervalButtons.add(rb);
        }
        this.intervalButtons.get(0).setSelected(true);

        // GUI setup for IntervalRadioButtons
        HBox intervalBox = new HBox(); // Use HBox for radio buttons
        intervalBox.getChildren().addAll(this.intervalButtons);
        intervalBox.setSpacing(10);
        TitledPane intervalPane = new TitledPane("Interval", intervalBox);

        // Additional controls for IntervalRadioButtons
        this.updateButton = new Button("Update Job");
        this.updateButton.setOnAction(this::handleUpdateButton);

        Label jobNameLabel = new Label("Job Name:");
        this.jobNameInput = new TextField();
        this.jobNameInput.setPromptText("Enter job name");


        parameterGrid = new GridPane();
        parameterGrid.setAlignment(Pos.CENTER);
        parameterGrid.setHgap(10);
        parameterGrid.setVgap(10);

        parameterGrid.add(jobNameLabel, 0, 0);
        parameterGrid.add(this.jobNameInput, 1, 0);
        parameterGrid.add(this.updateButton, 0, 1, 2, 1);
        GridPane.setHalignment(this.updateButton, HPos.CENTER);
        parameterGrid.add(new Label("Set the note sequence parameters"), 0, 2, 2, 1);
        parameterGrid.add(new Label("From Note:"), 0, 3);
        parameterGrid.add(this.fromNoteSpinner, 1, 3);
        parameterGrid.add(new Label("To Note:"), 0, 4);
        parameterGrid.add(this.toNoteSpinner, 1, 4);
        parameterGrid.add(intervalPane, 0, 5, 2, 1);


        // Setup for NoteSlidersTeacher functionality
        this.noteDurationLabel = new Label(String.format("Duration: %d ms", this.job.getNoteDuration()));
        this.noteDurationSlider = new Slider(100, 5000, this.job.getNoteDuration());
        this.noteDurationSlider.setShowTickMarks(true);
        this.noteDurationSlider.setMajorTickUnit(100);
        this.noteDurationSlider.setBlockIncrement(100);
        this.noteDurationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int duration = newValue.intValue();
            job.setNoteDuration(duration);
            noteDurationLabel.setText(String.format("Duration: %d ms", duration));
            drawNoteTiming(); // Update color bar when note duration slider changes
        });

        // NoteDecaySlider initialization
        this.noteDecaySlider = new Slider(100, 4500, this.job.getNoteDecay());
        this.noteDecaySlider.setShowTickMarks(true);
        this.noteDecaySlider.setMajorTickUnit(100);
        this.noteDecaySlider.setBlockIncrement(10);

        // NoteGapSlider initialization
        this.noteGapSlider = new Slider(0, 1000, this.job.getNoteGap());
        this.noteGapSlider.setShowTickMarks(true);
        this.noteGapSlider.setMajorTickUnit(100);
        this.noteGapSlider.setBlockIncrement(10);

        this.noteDecaySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int decay = newValue.intValue();
            job.setNoteDecay(decay);
            noteDecayLabel.setText(String.format("Decay: %d ms", decay));
            drawNoteTiming();
        });

        this.noteGapSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int gap = newValue.intValue();
            job.setNoteGap(gap);
            noteGapLabel.setText(String.format("Gap: %d ms", gap));
            drawNoteTiming();
        });

        this.noteDecayLabel = new Label(String.format("Decay: %d ms", this.job.getNoteDecay()));

        this.noteGapLabel = new Label(String.format("Gap: %d ms", this.job.getNoteGap()));

        int height = 30;
        this.noteTimingCanvas = new Canvas(400, height);
        GraphicsContext gc = this.noteTimingCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, this.noteTimingCanvas.getWidth(), this.noteTimingCanvas.getHeight());

        gc.setFill(Color.BLUE);
        int durationWidth = this.job.getNoteDuration() / 6;
        gc.fillRect(0, 0, durationWidth, height);
        gc.strokeRect(0, 0, durationWidth, height);

        gc.setFill(Color.LIGHTBLUE);
        int decayWidth = this.job.getNoteDecay() / 6;
        gc.fillRect(durationWidth, 0, decayWidth, height);
        gc.strokeRect(durationWidth, 0, decayWidth, height);

        LinearGradient decayGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.LIGHTBLUE),
                new Stop(0.5, Color.LIGHTBLUE),
                new Stop(1.0, Color.LIGHTGRAY)
        );

        gc.setFill(decayGradient);
        gc.fillRect(0, 0, this.noteTimingCanvas.getWidth(), height);

        VBox noteDurationBox = new VBox(
                this.noteDurationLabel,
                this.noteDurationSlider
        );

        VBox noteDecayBox = new VBox(
                this.noteDecayLabel,
                this.noteDecaySlider
        );

        VBox noteGapBox = new VBox(
                this.noteGapLabel,
                this.noteGapSlider
        );

        VBox noteTimesPane = new VBox(
                noteDurationBox,
                noteDecayBox,
                noteGapBox,
                this.noteTimingCanvas
        );
        noteTimesPane.setSpacing(10); // Add spacing between the sliders


        noteTimesTitledPane = new TitledPane(
                "Note Times",
                noteTimesPane
        );

        // Set up the scene
        Scene scene = new Scene(getPane(), 500, 600); // Increased height to accommodate both functionalities
        stage.setTitle("Interval Radio Buttons & Note Sliders");
        stage.setScene(scene);
        stage.show();

        // Draw initial note timing
        drawNoteTiming();
    }

    public Pane getPane() {
        VBox controlBox = new VBox();
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setSpacing(20);

        if (parameterGrid == null || noteTimesTitledPane == null) {
            System.err.println("错误：parameterGrid 或 TitledPane 为空。");
            return controlBox;
        }

        controlBox.getChildren().addAll(parameterGrid, noteTimesTitledPane);

        return controlBox;
    }


    private void handleIntervalButtonAction(int index) {
        Job.Interval interval = Job.Interval.values()[index];
        this.job.setInterval(interval);
        printJob();
    }

    private void handleUpdateButton(ActionEvent event) {
        // Set job name from the input TextField
        String jobName = this.jobNameInput.getText().trim();
        if (jobName.isEmpty()) {
            showErrorDialog("Please enter a valid job name.");
            return;
        }
        double noteGap = this.noteGapSlider.getValue(); // Get the note gap value
        int noteGapValue = (int) noteGap; // Convert double to int
        if (noteGapValue <= 0) { // Check if note gap is not positive
            showErrorDialog("Note gap time must be positive.");
            return;
        }
        this.job.setName(jobName);
        printJob();
    }

    private void printJob() {
        System.out.println(this.job);
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void drawNoteTiming() {
        GraphicsContext gc = this.noteTimingCanvas.getGraphicsContext2D();
        double totalWidth = this.noteDurationSlider.getValue() +
                this.noteDecaySlider.getValue() +
                this.noteGapSlider.getValue();
        double durationWidth = this.noteDurationSlider.getValue() / totalWidth * this.noteTimingCanvas.getWidth();
        double decayWidth = this.noteDecaySlider.getValue() / totalWidth * this.noteTimingCanvas.getWidth();
        double gapWidth = this.noteGapSlider.getValue() / totalWidth * this.noteTimingCanvas.getWidth();

        gc.clearRect(0, 0, this.noteTimingCanvas.getWidth(), this.noteTimingCanvas.getHeight());

        // Set different solid colors for note duration, decay, and gap areas
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, durationWidth, this.noteTimingCanvas.getHeight());
        gc.setStroke(Color.BLACK); // Set outline color
        gc.strokeRect(0, 0, durationWidth, this.noteTimingCanvas.getHeight()); // Draw outline

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(durationWidth, 0, decayWidth, this.noteTimingCanvas.getHeight());
        gc.setStroke(Color.BLACK); // Set outline color
        gc.strokeRect(durationWidth, 0, decayWidth, this.noteTimingCanvas.getHeight()); // Draw outline

        gc.setFill(Color.LIGHTCORAL);
        gc.fillRect(durationWidth + decayWidth, 0, gapWidth, this.noteTimingCanvas.getHeight());
        gc.setStroke(Color.BLACK); // Set outline color
        gc.strokeRect(durationWidth + decayWidth, 0, gapWidth, this.noteTimingCanvas.getHeight()); // Draw outline
    }

    public static void main(String[] args) {
        launch(args);
    }
}
