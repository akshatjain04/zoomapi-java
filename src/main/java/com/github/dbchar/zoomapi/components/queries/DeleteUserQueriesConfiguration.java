package com.github.dbchar.zoomapi.components.queries;

public class DeleteUserQueriesConfiguration {
    public enum Action {
        DISASSOCIATE("disassociate"), DELETE("delete");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final Action action;
    private final String transferEmail;
    private final boolean transferMeeting;
    private final boolean transferWebinar;
    private final boolean transferRecording;

    public DeleteUserQueriesConfiguration(Action action, String transferEmail, boolean transferMeeting, boolean transferWebinar, boolean transferRecording) {
        this.action = action;
        this.transferEmail = transferEmail;
        this.transferMeeting = transferMeeting;
        this.transferWebinar = transferWebinar;
        this.transferRecording = transferRecording;
    }

    public Action getAction() {
        return action;
    }

    public String getTransferEmail() {
        return transferEmail;
    }

    public boolean isTransferMeeting() {
        return transferMeeting;
    }

    public boolean isTransferWebinar() {
        return transferWebinar;
    }

    public boolean isTransferRecording() {
        return transferRecording;
    }
}
