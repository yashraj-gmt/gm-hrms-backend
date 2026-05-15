package com.gm.hrms.service;

import com.gm.hrms.entity.LeaveRequest;

public interface LeaveAttendanceService {

    void markLeaveAttendance(LeaveRequest leaveRequest);

    void revertLeaveAttendance(LeaveRequest leaveRequest);
}