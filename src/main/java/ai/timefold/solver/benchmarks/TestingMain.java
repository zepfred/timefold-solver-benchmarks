package ai.timefold.solver.benchmarks;

import ai.timefold.solver.benchmarks.examples.cloudbalancing.app.CloudBalancingApp;
import ai.timefold.solver.benchmarks.examples.conferencescheduling.app.ConferenceSchedulingApp;
import ai.timefold.solver.benchmarks.examples.tsp.app.TspApp;
import ai.timefold.solver.benchmarks.examples.vehiclerouting.app.VehicleRoutingApp;

public final class TestingMain {

    private static final String EXAMPLE = "cloudbalancing"; // Change this.
    private static final int MOVE_THREAD_COUNT = 2;         // Change this.

    private TestingMain() {
    }

    public static void main(String[] args) {
        try {
            Class.forName("ai.timefold.solver.enterprise.core.multithreaded.MoveSelectorInListProxy");
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Please run this against Chris' Multi-threaded PR.");
        }
        runExample(EXAMPLE);
    }

    private static Object runExample(String example) {
        int runTimeInMinutes = 6 * 60;
        return switch (example) {
            case "cloudbalancing" ->
                    new CloudBalancingApp().solve("1600computers-4800processes.json", runTimeInMinutes, MOVE_THREAD_COUNT);
            case "conferencescheduling" ->
                    new ConferenceSchedulingApp().solve("216talks-18timeslots-20rooms.xlsx", runTimeInMinutes, MOVE_THREAD_COUNT);
            case "tsp" -> new TspApp().solve("lu980.json", runTimeInMinutes, MOVE_THREAD_COUNT);
            case "vehiclerouting" ->
                    new VehicleRoutingApp().solve("vehiclerouting-belgium-tw-n2750-k55.json", runTimeInMinutes, MOVE_THREAD_COUNT);
            default -> throw new IllegalArgumentException();
        };
    }

}
