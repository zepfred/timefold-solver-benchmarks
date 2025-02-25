package ai.timefold.solver.benchmarks.examples.taskassigning.app;

import java.util.stream.Stream;

import ai.timefold.solver.benchmarks.examples.common.app.SolverSmokeTest;
import ai.timefold.solver.benchmarks.examples.taskassigning.domain.TaskAssigningSolution;
import ai.timefold.solver.core.api.score.buildin.bendable.BendableScore;

class TaskAssigningSmokeTest extends SolverSmokeTest<TaskAssigningSolution, BendableScore> {

    private static final String UNSOLVED_DATA_FILE = "data/taskassigning/unsolved/50tasks-5employees.json";

    @Override
    protected TaskAssigningApp createCommonApp() {
        return new TaskAssigningApp();
    }

    @Override
    protected Stream<TestData<BendableScore>> testData() {
        return Stream.of(
                TestData.of(UNSOLVED_DATA_FILE,
                        BendableScore.of(new int[] { 0 }, new int[] { 0, -3925, -6293940, -7772, -20463 }),
                        BendableScore.of(new int[] { 0 }, new int[] { 0, -3925, -6312519, -10049, -20937 })));
    }
}
