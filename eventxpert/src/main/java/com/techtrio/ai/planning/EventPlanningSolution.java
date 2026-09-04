package com.techtrio.ai.planning;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PlanningSolution
public class EventPlanningSolution {

    private String eventName;
    private String eventType;
    private Set<String> selectedRequirements = new HashSet<>();

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "slotRange")
    private List<EventPlanningSlot> slotList = new ArrayList<>();

    @PlanningEntityCollectionProperty
    private List<EventPlanningTask> taskList = new ArrayList<>();

    @PlanningScore
    private HardSoftScore score;

    public EventPlanningSolution() {
    }

    public EventPlanningSolution(String eventName, String eventType, Set<String> selectedRequirements,
                                 List<EventPlanningSlot> slotList, List<EventPlanningTask> taskList) {
        this.eventName = eventName;
        this.eventType = eventType;
        this.selectedRequirements = selectedRequirements;
        this.slotList = slotList;
        this.taskList = taskList;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Set<String> getSelectedRequirements() {
        return selectedRequirements;
    }

    public void setSelectedRequirements(Set<String> selectedRequirements) {
        this.selectedRequirements = selectedRequirements;
    }

    public List<EventPlanningSlot> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<EventPlanningSlot> slotList) {
        this.slotList = slotList;
    }

    public List<EventPlanningTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<EventPlanningTask> taskList) {
        this.taskList = taskList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}