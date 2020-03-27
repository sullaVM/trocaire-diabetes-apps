package com.example.doctor_app.data.responses;

import androidx.annotation.Nullable;

public class GetPatientLogsResponse extends PatientResponse {
    @Nullable
    public LogRecord[] logs;
}
