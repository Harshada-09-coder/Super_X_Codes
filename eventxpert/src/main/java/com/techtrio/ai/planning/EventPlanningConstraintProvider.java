package com.techtrio.ai.planning;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;

public class EventPlanningConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                // HARD CONSTRAINTS
                slotConflict(factory),
                taskMustHaveSlot(factory),
                dependencyOrdering(factory),

                // SOFT CONSTRAINTS
                prioritizeHighPriorityTasks(factory),
                chronologicalSequencePreference(factory),
                minimizeIdleGapBetweenConsecutiveTasks(factory)
        };
    }

    // HARD 1: No two distinct tasks can occupy the exact same time slot
    Constraint slotConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(EventPlanningTask.class,
                        Joiners.equal(EventPlanningTask::getPlanningSlot))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Slot Conflict (Two tasks scheduled simultaneously)");
    }

    // HARD 2: Every selected-service task must be assigned to a valid slot
    Constraint taskMustHaveSlot(ConstraintFactory factory) {
        return factory.forEachIncludingNullVars(EventPlanningTask.class)
                .filter(task -> task.getPlanningSlot() == null)
                .penalize(HardSoftScore.ofHard(10))
                .asConstraint("Unassigned Task");
    }

    // HARD 3: Task sequence dependencies (e.g. Stage Setup before Ceremony)
    Constraint dependencyOrdering(ConstraintFactory factory) {
        return factory.forEachUniquePair(EventPlanningTask.class)
                .filter((taskA, taskB) -> {
                    if (taskA.getPlanningSlot() == null || taskB.getPlanningSlot() == null) return false;
                    // If taskB depends on taskA, taskA's slot must be earlier than taskB's slot
                    if (taskB.getDependencyTaskId() != null && taskB.getDependencyTaskId().equals(taskA.getTaskId())) {
                        int slotA = taskA.getPlanningSlot().getDayIndex() * 1440 + taskA.getPlanningSlot().getStartMinuteOfDay();
                        int slotB = taskB.getPlanningSlot().getDayIndex() * 1440 + taskB.getPlanningSlot().getStartMinuteOfDay();
                        return slotA >= slotB;
                    }
                    return false;
                })
                .penalize(HardSoftScore.ofHard(5))
                .asConstraint("Dependency Ordering Violation");
    }

    // SOFT 1: Schedule high priority tasks earlier in the timeline
    Constraint prioritizeHighPriorityTasks(ConstraintFactory factory) {
        return factory.forEach(EventPlanningTask.class)
                .filter(task -> task.getPlanningSlot() != null && task.getPriority() == 1)
                .reward(HardSoftScore.ofSoft(10))
                .asConstraint("High Priority Task Scheduled");
    }

    // SOFT 2: Maintain logical sequence (Prep tasks earlier, Cleanup tasks later)
    Constraint chronologicalSequencePreference(ConstraintFactory factory) {
        return factory.forEachUniquePair(EventPlanningTask.class,
                        Joiners.equal(EventPlanningTask::getCategory))
                .filter((task1, task2) -> {
                    if (task1.getPlanningSlot() == null || task2.getPlanningSlot() == null) return false;
                    int slot1 = task1.getPlanningSlot().getDayIndex() * 1440 + task1.getPlanningSlot().getStartMinuteOfDay();
                    int slot2 = task2.getPlanningSlot().getDayIndex() * 1440 + task2.getPlanningSlot().getStartMinuteOfDay();
                    return (task1.getSequenceOrder() < task2.getSequenceOrder() && slot1 > slot2)
                            || (task1.getSequenceOrder() > task2.getSequenceOrder() && slot1 < slot2);
                })
                .penalize(HardSoftScore.ofSoft(3))
                .asConstraint("Sequence Order Inversion");
    }

    // SOFT 3: Minimize long gaps between consecutive service operations
    Constraint minimizeIdleGapBetweenConsecutiveTasks(ConstraintFactory factory) {
        return factory.forEachUniquePair(EventPlanningTask.class,
                        Joiners.equal(EventPlanningTask::getCategory))
                .filter((task1, task2) -> {
                    if (task1.getPlanningSlot() == null || task2.getPlanningSlot() == null) return false;
                    if (task1.getPlanningSlot().getDayIndex() != task2.getPlanningSlot().getDayIndex()) return false;
                    int gap = Math.abs(task1.getPlanningSlot().getStartMinuteOfDay() - task2.getPlanningSlot().getStartMinuteOfDay());
                    return gap > 180; // More than 3 hours gap between same category tasks
                })
                .penalize(HardSoftScore.ofSoft(1))
                .asConstraint("Avoid Extended Gaps");
    }
}