package ru.mirea.coursework.defecttracker.domain;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class IssueLifecycle {
    private static final Map<IssueStatus, Set<IssueStatus>> TRANSITIONS = new EnumMap<>(IssueStatus.class);

    static {
        TRANSITIONS.put(IssueStatus.NEW, EnumSet.of(IssueStatus.TRIAGED, IssueStatus.CLOSED));
        TRANSITIONS.put(IssueStatus.TRIAGED, EnumSet.of(IssueStatus.IN_PROGRESS, IssueStatus.CLOSED));
        TRANSITIONS.put(IssueStatus.IN_PROGRESS, EnumSet.of(IssueStatus.READY_FOR_TEST, IssueStatus.TRIAGED));
        TRANSITIONS.put(IssueStatus.READY_FOR_TEST, EnumSet.of(IssueStatus.VERIFIED, IssueStatus.IN_PROGRESS));
        TRANSITIONS.put(IssueStatus.VERIFIED, EnumSet.of(IssueStatus.CLOSED, IssueStatus.REOPENED));
        TRANSITIONS.put(IssueStatus.CLOSED, EnumSet.of(IssueStatus.REOPENED));
        TRANSITIONS.put(IssueStatus.REOPENED, EnumSet.of(IssueStatus.TRIAGED, IssueStatus.IN_PROGRESS));
    }

    private IssueLifecycle() { }

    public static boolean canTransition(IssueStatus from, IssueStatus to) {
        return from != null && to != null && TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }

    public static Set<IssueStatus> nextStatuses(IssueStatus from) {
        return TRANSITIONS.getOrDefault(from, Set.of());
    }
}
