package no.rutebanken.nabu.rest.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.rest.domain.JobStatus.Action;
import no.rutebanken.nabu.rest.domain.JobStatus.State;

public class JobStatusEvent {

    @JsonProperty("state")
    public State state;

    @JsonProperty("date")
    public Date date;

    @JsonProperty("action")
    public Action action;

    @JsonProperty("chouetteJobId")
    public Long chouetteJobId;

    @JsonProperty("referential")
    public String referential;


    public JobStatusEvent(Action action, State state, Date date, Long chouetteJobId, String referential) {
        this.action = action;
        this.state = state;
        this.date = date;
        this.chouetteJobId = chouetteJobId;
        this.referential = referential;
    }

    public static JobStatusEvent createFromJobEvent(JobEvent e) {
        Long chouetteId = e.getExternalId() == null ? null : Long.parseLong(e.getExternalId());
        return new JobStatusEvent(JobStatus.Action.valueOf(e.getActionSubType()),
                                         JobStatus.State.valueOf(e.getState().name()), Date.from(e.getEventTime()), chouetteId
                                         , e.getReferential());
    }
}
