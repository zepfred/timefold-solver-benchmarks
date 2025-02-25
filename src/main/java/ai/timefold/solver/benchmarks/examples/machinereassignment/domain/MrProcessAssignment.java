package ai.timefold.solver.benchmarks.examples.machinereassignment.domain;

import java.util.Objects;

import ai.timefold.solver.benchmarks.examples.common.domain.AbstractPersistable;
import ai.timefold.solver.benchmarks.examples.machinereassignment.domain.solver.MrProcessAssignmentDifficultyComparator;
import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity(difficultyComparatorClass = MrProcessAssignmentDifficultyComparator.class)
public class MrProcessAssignment extends AbstractPersistable {

    public static MrProcessAssignment withOriginalMachine(long id,
            MrProcess process,
            MrMachine originalMachine) {
        return new MrProcessAssignment(id, process, originalMachine, null);
    }

    public static MrProcessAssignment withTargetMachine(long id,
            MrProcess process,
            MrMachine targetMachine) {
        return new MrProcessAssignment(id, process, null, targetMachine);
    }

    private MrProcess process;
    private MrMachine originalMachine;
    private MrMachine machine;

    @SuppressWarnings("unused")
    MrProcessAssignment() {
    }

    public MrProcessAssignment(long id, MrProcess process) {
        super(id);
        this.process = process;
    }

    public MrProcessAssignment(long id, MrProcess process,
            MrMachine originalMachine,
            MrMachine machine) {
        super(id);
        this.process = process;
        this.originalMachine = originalMachine;
        this.machine = machine;
    }

    public MrProcess getProcess() {
        return process;
    }

    public MrMachine getOriginalMachine() {
        return originalMachine;
    }

    public void
            setOriginalMachine(MrMachine originalMachine) {
        this.originalMachine = originalMachine;
    }

    @PlanningVariable
    public MrMachine getMachine() {
        return machine;
    }

    public void setMachine(MrMachine machine) {
        this.machine = machine;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public MrService getService() {
        return process.getService();
    }

    @JsonIgnore
    public boolean isMoved() {
        if (machine == null) {
            return false;
        }
        return !Objects.equals(originalMachine, machine);
    }

    @JsonIgnore
    public int getProcessMoveCost() {
        return process.getMoveCost();
    }

    @JsonIgnore
    public int getMachineMoveCost() {
        return (machine == null || originalMachine == null) ? 0 : originalMachine.getMoveCostTo(machine);
    }

    @JsonIgnore
    public MrNeighborhood getNeighborhood() {
        return machine == null ? null : machine.getNeighborhood();
    }

    @JsonIgnore
    public MrLocation getLocation() {
        return machine == null ? null : machine.getLocation();
    }

    @JsonIgnore
    public long getUsage(MrResource resource) {
        return process.getUsage(resource);
    }

    @JsonIgnore
    public String getLabel() {
        return "Process " + getId();
    }

    @Override
    public String toString() {
        return process.toString();
    }

}
