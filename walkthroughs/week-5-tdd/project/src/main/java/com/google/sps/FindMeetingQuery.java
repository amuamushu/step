// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Finds potential meeting times. */
public final class FindMeetingQuery {
  public Collection<TimeRange> timesForEveryone;
  public Collection<TimeRange> timesForConfirmed;
  public int currentEndTime;

  /**
   * Returns available TimeRanges for the {@code request} given {@code events}. 
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    timesForEveryone = new ArrayList<>();
    timesForConfirmed = new ArrayList<>();

    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      // If the event is longer than a day, no time slots are available to book a meeting.
      return timesForEveryone;
    }
    if (request.getAttendees().size() == 0 && request.getOptionalAttendees().size() == 0) {
      // If there are no attendees, the entire day is available to book a meeting.
      timesForEveryone.add(TimeRange.WHOLE_DAY);
      return timesForEveryone;
    }

    currentEndTime = TimeRange.START_OF_DAY;
    for (Event event : events) {
      // Ignores this event's time range if the people attending this event are 
      // not attending the requested meeting. 
      Set<String> eventAttendees = new HashSet<String>(event.getAttendees());
      eventAttendees.retainAll(request.getAttendees());
      Set<String> optionalEventAttendeesForRequestedMeeting = 
          new HashSet<String>(event.getAttendees());
      optionalEventAttendeesForRequestedMeeting.retainAll(request.getOptionalAttendees());
      if (eventAttendees.isEmpty() && optionalEventAttendeesForRequestedMeeting.isEmpty()) {
        continue;
      }
      setTimeGapIfPossible(request, eventAttendees, event);
    }
    // Adds a timerange for after the latest event ends.
    if (currentEndTime < TimeRange.END_OF_DAY) {
      TimeRange latestTimeRange = TimeRange.fromStartEnd(currentEndTime, 
          TimeRange.END_OF_DAY, true);
      timesForEveryone.add(latestTimeRange);
    }

    if (timesForEveryone.size() == 0 && request.getAttendees().size() != 0) {
      return timesForConfirmed;
    }
    return timesForEveryone;
  }

  /**
   * Adds a TimeRange to indicate a time gap for either {@code timesForEveryone} or {@code timesForConfirmed} depending
   * on whether optional or confirmed attendees are attending the current {@code event}.
   * 
   * <p>Checks {@code request.duration} and {@code eventAttendees} to see if a time gap is possible for
   * everybody, only confirmed attendees, only optional attendees, or no one at all.
   */
  private void setTimeGapIfPossible(MeetingRequest request, Set<String> eventAttendees, Event event) {
    TimeRange time = event.getWhen();
    // Ignores this all-day event if only optional attendees are present.
    if (time.equals(TimeRange.WHOLE_DAY) && eventAttendees.isEmpty()) {
      return;
    }

    // Handles overlapping events and the case where the requested meeting duration is longer
    // than the time gap.
    if ((time.start() <= currentEndTime) || (time.end() <= currentEndTime) ||
        (request.getDuration() > (time.start() - currentEndTime))) {
      // If only optional attendees are attending this event, then this time slot would work for 
      // confirmed attendees.
      if (eventAttendees.isEmpty()) {
        int latestEndTime = (int) Math.max(time.start(), currentEndTime + request.getDuration());
        TimeRange gapTimeRange = TimeRange.fromStartEnd(currentEndTime, latestEndTime, false);
        timesForConfirmed.add(gapTimeRange);
        this.currentEndTime = latestEndTime;
      }
      this.currentEndTime = Math.max(time.end(), currentEndTime);
      return;
    }
    TimeRange gapTimeRange = TimeRange.fromStartEnd(currentEndTime, time.start(), false);
    this.currentEndTime = time.end();
    timesForEveryone.add(gapTimeRange);
    return;
  }
}
