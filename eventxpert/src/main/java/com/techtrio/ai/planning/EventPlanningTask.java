package com.techtrio.ai.planning;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class EventPlanningTask {

    @PlanningId
    private String taskId;
    private String taskName;
    private String category; // Maps to one of the 8 customer-selected requirements
    private int durationMinutes;
    private int priority; // 1 = High, 2 = Medium, 3 = Standard
    private int sequenceOrder; // 1 = Prep, 2 = Main Service, 3 = Post/Cleanup
    private String dependencyTaskId; // Optional prerequisite task ID

    private EventPlanningSlot planningSlot;

    public EventPlanningTask() {
    }

    public EventPlanningTask(String taskId, String taskName, String category, int durationMinutes, int priority, int sequenceOrder) {
        this(taskId, taskName, category, durationMinutes, priority, sequenceOrder, null);
    }

    public EventPlanningTask(String taskId, String taskName, String category, int durationMinutes, int priority, int sequenceOrder, String dependencyTaskId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.priority = priority;
        this.sequenceOrder = sequenceOrder;
        this.dependencyTaskId = dependencyTaskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public String getDependencyTaskId() {
        return dependencyTaskId;
    }

    public void setDependencyTaskId(String dependencyTaskId) {
        this.dependencyTaskId = dependencyTaskId;
    }

    @PlanningVariable(valueRangeProviderRefs = "slotRange")
    public EventPlanningSlot getPlanningSlot() {
        return planningSlot;
    }

    public void setPlanningSlot(EventPlanningSlot planningSlot) {
        this.planningSlot = planningSlot;
    }

    @Override
    public String toString() {
        return taskName + " (" + category + ")";
    }
}